package com.inha.capstone.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.inha.capstone.Dto.FIleDto.GetFileResponse;
import com.inha.capstone.config.BaseException;
import com.inha.capstone.config.BaseResponseStatus;
import com.inha.capstone.domain.Application;
import com.inha.capstone.domain.FileCategory;
import com.inha.capstone.domain.FileObject;
import com.inha.capstone.domain.User;
import com.inha.capstone.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
  private final FileRepository fileRepository;
  private final AmazonS3 amazonS3Client;
  private final UserService userService;
  private final ApplicationService applicationService;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;
  @Value("${cloud.aws.s3.root-package}")
  private String rootPackage;

  /**
   * 파일 데이터 저장 메서드
   * @param multipartFile
   * @param fileCategory
   * @param applicationId
   * @param userId
   * @return fileUrl
   */
  public String saveFile(MultipartFile multipartFile, FileCategory fileCategory, Long applicationId, Long userId, Long componentId, User requestedUser) {
    Application application = null;
    User user = null;
    if(applicationId != 0) { // 애플리케이션 내부의 파일
      application = applicationService.findById(applicationId);
      if(userId == 0) { // 애플리케이션 내부의 공용 파일
        if(requestedUser.getId() != application.getUser().getId()) // 애플리케이션의 제작자가 아닌경우 거부
          throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
      }
      if(userId != 0) { // 애플리케이션 내부의 개인 파일
        if(requestedUser.getId() != userId) // 애플리케이션의 개인 파일을 타인이 수정 시 거부
          throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
        user = userService.findOne(userId);
      }
    }
    if(applicationId == 0) { // 애플리케이션 공용 파일
      if(userId != 0) // 애플리케이션 공용 파일은 사용자 지정이 없음 -> TODO: 관리자 권한 없을 시 입력 못하도록 수정 필요해 보임
        throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
    }

    // S3 로 업로드
    String uploadFileUrl = uploadFileToS3(multipartFile, fileCategory, applicationId, userId, componentId);

    Optional<FileObject> file = findFileObject(application, user, componentId);
    if(file.isPresent()) { // 해당 컴포넌트의 파일이 존재할시 S3 상 이미 존재하는 파일을 제거 및 db 데이터 update
      updateFile(user, application, fileCategory, uploadFileUrl, componentId, file.get());
    }
    if(file.isEmpty()) { // 해당 컴포넌트의 파일이 없을 시 db에 새로운 데이터를 생성
      save(user, application, fileCategory, uploadFileUrl, componentId);
    }
    return uploadFileUrl;
  }

  @Transactional
  public void updateFile(User user, Application application, FileCategory fileCategory, String uploadFileUrl, Long componentId, FileObject file) {
    deleteFileByFileObject(file);
    save(user, application, fileCategory, uploadFileUrl, componentId);
  }

  /**
   * File S3 저장 메서드
   * @param multipartFile
   * @param fileCategory
   * @param applicationId
   * @param userId
   * @return fileUrl
   */
  public String uploadFileToS3(MultipartFile multipartFile, FileCategory fileCategory, Long applicationId, Long userId, Long componentId) {
    // 임시파일 경로 지정
    File convertFile = new File(System.getProperty("java.io.tmpdir")+"/" + multipartFile.getOriginalFilename());
    try {
      if (convertFile.createNewFile()) { // 임시파일 생성
        // MultiPartFile -> File 변환
        FileOutputStream fos = new FileOutputStream(convertFile);
        fos.write(multipartFile.getBytes());
      } else {
        if(!convertFile.delete())
          log.warn("임시파일이 삭제되지 않았습니다.");
        throw new BaseException(BaseResponseStatus.CONVERT_MULTIPART_FILE_FAILED);
      }
    } catch (IOException e) {
      throw new BaseException(BaseResponseStatus.CANNOT_CREATE_FILE);
    }
    String fileName = rootPackage + "/" + fileCategory.getName() + "/" + applicationId.toString() + "/" + userId.toString() + "/" + componentId.toString() + "/" + UUID.randomUUID() + "-" + convertFile.getName();
    amazonS3Client.putObject(
        new PutObjectRequest(bucket, fileName, convertFile)
            .withCannedAcl(CannedAccessControlList.PublicRead)
    );
    if(!convertFile.delete())
      log.warn("임시파일이 삭제되지 않았습니다.");
    return amazonS3Client.getUrl(bucket, fileName).toString();
  }

  /**
   * RDB 저장 메서드
   * @param user
   * @param app
   * @param category
   * @param filePath
   */
  public void save(User user, Application app, FileCategory category, String filePath, Long componentId) {
    FileObject fileObject = FileObject.builder()
            .user(user)
            .application(app)
            .category(category)
            .filePath(filePath)
            .componentId(componentId)
            .build();
    fileRepository.save(fileObject);
  }

  /**
   * 파일 데이터 제거 메서드
   * @param applicationId
   * @param userId
   * @param componentId
   * @param requestedUser
   */
  public void deleteFile(Long applicationId, Long userId, Long componentId, User requestedUser){
    Application application = null;
    User user = null;
    if(applicationId == 0 && userId == 0) {} // 전역 공용
    else if(applicationId != 0 && userId == 0) { // 애플리케이션 내 공용
      if(application.getUser().getId() != requestedUser.getId())
        throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
      user = requestedUser;
    }
    else if(applicationId !=0 && userId != 0) { // 애플리케이션 내 개인
      if(userId != requestedUser.getId())
        throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
      user = requestedUser;
    }
    else {
      throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
    }
    FileObject file = findFileObject(application, user, componentId)
        .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_FILE));
    deleteFileByFileObject(file);
  }

  /**
   * fileObject 제거 메서드
   * @param fileObject
   */
  @Transactional
  public void deleteFileByFileObject(FileObject fileObject) {
    String filepath = fileObject.getFilePath();
    deleteFileFromS3(filepath);
    delete(fileObject);
  }

  /**
   * url 주소를 통해 S3 상의 파일 제거
   * @param filepath
   */
  public void deleteFileFromS3(String filepath) {
    String fileKey = filepath.replace(String.format("https://%s.s3.%s.amazonaws.com/", bucket, amazonS3Client.getRegion()),"");
    amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileKey));
  }

  /**
   * RDB 상 파일 데이터 제거 메서드
   * @param file
   */
  @Transactional
  public void delete(FileObject file) {
    fileRepository.deleteById(file.getFileId());
  }

  public List<GetFileResponse> getFilePathes(Long applicationId, User user){
    if(applicationId == 0)
      throw new BaseException(BaseResponseStatus.INCORRECT_APPLICATIONID);
    Application application = applicationService.findById(applicationId);
    List<FileObject> files = fileRepository.findFileObjectsByApplicationAndUser(application, user);
    return files.stream()
        .map(file -> new GetFileResponse(file.getFilePath(), file.getComponentId()))
        .collect(Collectors.toList());
  }

  /**
   * 공용 파일을 조회를 위한 메서드
   * (condition : application == null && uesr == null)
   * @return
   */
  public List<GetFileResponse> getGlobalFilePathes() {
    List<FileObject> files = fileRepository.findFileObjectsWhereApplicationIsNullAndUserIsNull();
    return files.stream()
        .map(file -> new GetFileResponse(file.getFilePath(), file.getComponentId()))
        .collect(Collectors.toList());
  }

  /**
   * RDB 상 FileObject 조회 메서드
   * @param application
   * @param user
   * @param componentId
   * @return
   */
  public Optional<FileObject> findFileObject(Application application, User user, Long componentId) {
    return fileRepository.findFileObjectByApplicationAndUserAndComponentId(application, user, componentId);
  }

//  public String saveFileToLocal(MultipartFile multipartFile) {
//    // 임시파일 경로 지정
//    File convertFile = new File(System.getProperty("java.io.tmpdir")+"/" + multipartFile.getOriginalFilename());
//    try {
//      if (convertFile.createNewFile()) { // 임시파일 생성
//        // MultiPartFile -> File 변환
//        FileOutputStream fos = new FileOutputStream(convertFile);
//        fos.write(multipartFile.getBytes());
//      } else {
//        if(!convertFile.delete())
//          log.warn("애플리케이션 ui 임시파일이 삭제되지 않았습니다.");
//        throw new BaseException(BaseResponseStatus.CONVERT_MULTIPART_FILE_FAILED);
//      }
//    } catch (IOException e) {
//      throw new BaseException(BaseResponseStatus.CANNOT_CREATE_FILE);
//    }
//    return convertFile.getPath();
//  }

  public void deleteFileFromLocal(String absoluteFilePath) {
    File file = new File(absoluteFilePath);
    if(!file.delete())
      log.warn("애플리케이션 ui 임시파일이 삭제되지 않았습니다.");
  }

  public String uploadFileToS3WithoutDatabase(FileCategory fileCategory, String uiFilePath, Long applicationId, Long userId) {
    File file = new File(uiFilePath);
    String fileName = rootPackage + "/" + fileCategory.getName() + "/" + applicationId.toString() + "/" + userId.toString() + "/0/" + file.getName();
    amazonS3Client.putObject(
        new PutObjectRequest(bucket, fileName, file)
            .withCannedAcl(CannedAccessControlList.PublicRead)
    );
    return amazonS3Client.getUrl(bucket, fileName).toString();
  }
}

