package com.inha.capstone.controller;

import com.inha.capstone.Dto.FIleDto.*;
import com.inha.capstone.config.BaseResponse;
import com.inha.capstone.domain.Application;
import com.inha.capstone.domain.User;
import com.inha.capstone.service.ApplicationService;
import com.inha.capstone.service.FileService;
import com.inha.capstone.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Slf4j
@RequestMapping("/file")
@RestController
@RequiredArgsConstructor
public class FileController {
  private final FileService fileService;
  private final UserService userService;
  private final ApplicationService applicationService;

  @PostMapping("/upload")
  public ResponseEntity<BaseResponse<String>> uploadFile (
      Principal principal,
      @RequestPart(value = "file") MultipartFile multipartFile,
      @RequestPart(value = "info") PostFileRequest request
  ) {
    User user = userService.findByUserId(principal.getName());
    String s3FilePath = fileService.saveFile(multipartFile, request.getFileCategory(), request.getApplicationId(), request.getUserId(), request.getComponentId(), user);
    log.info("uploadFile REQUESTED - applicationId : " + request.getApplicationId()+ ",userId : " + request.getUserId() + ",componentId : " + request.getComponentId() + ",fileCategory : " + request.getFileCategory());
    return ResponseEntity.ok().body(new BaseResponse(s3FilePath));
  }

  @DeleteMapping("/delete")
  public ResponseEntity<BaseResponse<Boolean>> deleteFile(@RequestBody DeleteFileRequest request) {
    User user = userService.findOne(request.getUserId());
    Application application = applicationService.findById(request.getApplicationId());
    fileService.deleteFile(application, user, request.getComponentId());
    log.info("deleteFile REQUESTED - applicationId : " + application.getApplicationId() + ", userId : " + user.getId() + ",componentId : " + request.getComponentId());
    return ResponseEntity.ok().body(new BaseResponse<>(true));
  }

  @GetMapping("/applicaiton/{applicationId}")
  public ResponseEntity<BaseResponse<List<GetFileResponse>>> getFilePathes(Principal principal, @PathVariable("applicationId") Long applicationId) {
    User user = userService.findByUserId(principal.getName());
    List<GetFileResponse> filePathes = fileService.getFilePathes(applicationId, user);
    log.info("getFilePathes REQUESTED - applicationId : " + applicationId + ", userId : " + user.getId() + ", files : " + filePathes.size());
    return ResponseEntity.ok().body(new BaseResponse<>(filePathes));
  }
}

