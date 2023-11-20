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

@Slf4j
@RequestMapping("/file")
@RestController
@RequiredArgsConstructor
public class FileController {
  private final FileService fileService;
  private final UserService userService;
  private final ApplicationService applicationService;
  @DeleteMapping("/delete")
  public ResponseEntity<BaseResponse<Boolean>> deleteFile(@RequestBody DeleteFileRequest request) {
    User user = userService.findOne(request.getUserId());
    Application application = applicationService.findById(request.getApplicationId());
    fileService.deleteFile(application, user, request.getComponentId());
    log.info("deleteFile REQUESTED - applicationId : " + application.getApplicationId() + ", userId : " + user.getId() + ",componentId : " + request.getComponentId());
    return ResponseEntity.ok().body(new BaseResponse<>(true));
  }
}

