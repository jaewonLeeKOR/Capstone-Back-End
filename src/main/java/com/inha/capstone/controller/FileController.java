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

import static com.inha.capstone.util.LogUtil.getRequestLog;
import static com.inha.capstone.util.LogUtil.getResponseLog;
import static com.inha.capstone.util.LogUtil.HttpRequestMethod.*;

@Slf4j
@RequestMapping("/file")
@RestController
@RequiredArgsConstructor
public class FileController {
  private final FileService fileService;
  private final UserService userService;
  private final ApplicationService applicationService;

  @PostMapping()
  public ResponseEntity<BaseResponse<String>> uploadFile (
      Principal principal,
      @RequestPart(value = "file") MultipartFile multipartFile,
      @RequestPart(value = "info") PostFileRequest request
  ) {
    String endpointPath = "/file";
    log.info(getRequestLog(POST, endpointPath,request));
    User user = userService.findByUserId(principal.getName());
    String response = fileService.saveFile(multipartFile, request.getFileCategory(), request.getApplicationId(), request.getUserId(), request.getComponentId(), user);
    log.info(getResponseLog(POST, endpointPath,request,response));
    return ResponseEntity.ok().body(new BaseResponse(response));
  }

  @DeleteMapping()
  public ResponseEntity<BaseResponse<Boolean>> deleteFile(Principal principal, @RequestBody DeleteFileRequest request) {
    String endpointPath = "/file";
    log.info(getRequestLog(DELETE, endpointPath, request));
    User requestedUser = userService.findByUserId(principal.getName());
    fileService.deleteFile(request.getApplicationId(), request.getUserId(), request.getComponentId(), requestedUser);
    log.info(getResponseLog(DELETE, endpointPath, request, null));
    return ResponseEntity.ok().body(new BaseResponse<>(true));
  }

  @GetMapping("/global")
  public ResponseEntity<BaseResponse<List<GetFileResponse>>> getFileGlobalPathes() {
    String endpointPath = "/file/global";
    log.info(getRequestLog(GET, endpointPath, null));
    List<GetFileResponse> response = fileService.getGlobalFilePathes();
    log.info(getResponseLog(GET, endpointPath, null, response));
    return ResponseEntity.ok().body(new BaseResponse<>(response));
  }

  @GetMapping("/applicaiton/{applicationId}")
  public ResponseEntity<BaseResponse<List<GetFileResponse>>> getFilePathes(Principal principal, @PathVariable("applicationId") Long applicationId) {
    String endpointPath = "/file/applicaiton/" + applicationId;
    log.info(getRequestLog(GET, endpointPath, null));
    User user = userService.findByUserId(principal.getName());
    List<GetFileResponse> response = fileService.getFilePathes(applicationId, user);
    log.info(getResponseLog(GET, endpointPath, null, response));
    return ResponseEntity.ok().body(new BaseResponse<>(response));
  }
}

