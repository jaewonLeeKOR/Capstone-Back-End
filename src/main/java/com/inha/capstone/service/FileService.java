package com.inha.capstone.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.inha.capstone.domain.Application;
import com.inha.capstone.domain.FileObject;
import com.inha.capstone.domain.User;
import com.inha.capstone.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

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
   * 파일 데이터 제거 메서드
   * @param applcation
   * @param user
   * @param componentId
   */
  public void deleteFile(Application applcation, User user, Long componentId){
    FileObject file = findFileObject(applcation, user, componentId).get();
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
}

