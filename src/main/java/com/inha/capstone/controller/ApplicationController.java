package com.inha.capstone.controller;

import com.inha.capstone.Dto.ApplicationDto;
import com.inha.capstone.Dto.ApplicationDto.*;
import com.inha.capstone.config.BaseResponse;
import com.inha.capstone.domain.Application;
import com.inha.capstone.domain.ApplicationCategory;
import com.inha.capstone.domain.User;
import com.inha.capstone.service.ApplicationService;
import com.inha.capstone.service.FileService;
import com.inha.capstone.service.UserService;
import com.inha.capstone.util.ApplicationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.inha.capstone.util.LogUtil.getRequestLog;
import static com.inha.capstone.util.LogUtil.getResponseLog;
import static com.inha.capstone.util.LogUtil.HttpRequestMethod.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;
    private final FileService fileService;

    @PostMapping("/v1/application")
    public ResponseEntity<BaseResponse<Void>> createApplicationV1(Principal principal, @RequestBody @Valid CreateApplicationRequestV1 request) throws ParseException {
        String endpointPath = "/v1/application";
        log.info(getRequestLog(POST, endpointPath, request));
        User user = userService.findByUserId(principal.getName());
        Application application = Application.builder()
            .user(user)
            .title(request.getTitle())
            .description(request.getDescription())
            .createdDate(LocalDateTime.now())
            .modifiedDate(LocalDateTime.now())
            .applicationCategory(ApplicationCategory.nameOf(request.getCategory()))
            .build();
        applicationService.saveV1(application, request.getUi());
        log.info(getResponseLog(POST, endpointPath, request, null));

        return ResponseEntity.ok().body(new BaseResponse());
    }

    @PostMapping("/application")
    public ResponseEntity<BaseResponse<Void>> createApplication(
        Principal principal,
        @RequestPart(value = "file") MultipartFile multipartFile,
        @RequestPart(value = "info") CreateApplicationRequest request
    ) throws ParseException {
        String endpointPath = "/application";
        log.info(getRequestLog(POST, endpointPath, request));

        User user = userService.findByUserId(principal.getName());
        String uiImagePath = fileService.saveFileToLocal(multipartFile);
        // TODO: uiImagePath 로 이미지를 html, css 코드로 변환후 해당 파일들이 존재하는 directoryPath 를 uiFilePath 에 저장
        String uiFilePath = null;
        Application application = new Application(request, user);
        applicationService.save(application);
        String applicationUrl = fileService.uploadApplicationToS3(uiFilePath,application.getApplicationId(),user.getId());
        application.setApplicationUrl(applicationUrl);
        applicationService.save(application);
        fileService.deleteFileFromLocal(uiImagePath);
        fileService.deleteFileFromLocal(uiFilePath);

        log.info(getResponseLog(POST, endpointPath, request, null));
        return ResponseEntity.ok().body(new BaseResponse());
    }

    @GetMapping("/v1/applications/{applicationId}")
    public ResponseEntity<BaseResponse<ApplicationUiResponse>> getApplicationUi(@PathVariable Long applicationId){
        String endpointPath = "/v1/applications/" + applicationId;
        log.info(getRequestLog(GET, endpointPath, null));
        JSONObject UI = applicationService.getApplicationUIV1(applicationId);
        ApplicationUiResponse response = new ApplicationUiResponse(UI);
        log.info(getResponseLog(GET, endpointPath, null, response));
        return ResponseEntity.ok().body(new BaseResponse<>(response));
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<BaseResponse<ApplicationUrlResponse>> getApplicationUrl(@PathVariable Long applicationId){
        String endpointPath = "/applications/" + applicationId;
        log.info(getRequestLog(GET, endpointPath, null));
        String applicationUrl = applicationService.getApplicationUrl(applicationId);
        ApplicationUrlResponse response = new ApplicationUrlResponse(applicationUrl);
        log.info(getResponseLog(GET, endpointPath, null, response));
        return ResponseEntity.ok().body(new BaseResponse<>(response));
    }

    @PatchMapping("/v1/applications/{applicationId}")
    public ResponseEntity<BaseResponse<Void>> updateApplicationV1(Principal principal, @PathVariable Long applicationId, @RequestBody @Valid ApplicationDto.UpdateApplicationRequestV1 request) throws ParseException{
        String endpointPath = "/v1/application/" + applicationId;
        log.info(getRequestLog(PATCH, endpointPath, request));
        applicationService.checkPermissionForApplication(principal, applicationId);
        applicationService.updateApplicationV1(applicationId, request.getUi());
        log.info(getResponseLog(PATCH, endpointPath, request, null));
        return ResponseEntity.ok().body(new BaseResponse());
    }

    @PatchMapping("/applications/{applicationId}")
    public ResponseEntity<BaseResponse<Void>> updateApplication(
        Principal principal,
        @RequestPart(value = "uiImage") MultipartFile multipartFile,
        @RequestPart(value = "info") UpdateApplicationRequest request,
        @PathVariable Long applicationId
    ) throws ParseException{
        String endpointPath = "/application/" + applicationId;
        log.info(getRequestLog(PATCH, endpointPath, request));
        applicationService.checkPermissionForApplication(principal, applicationId);

        User user = userService.findByUserId(principal.getName());
        Application application = applicationService.findById(applicationId);
        String uiImagePath = fileService.saveFileToLocal(multipartFile);
        // TODO: uiImagePath 로 이미지를 html, css 코드로 변환후 해당 파일들이 존재하는 directoryPath 를 uiFilePath 에 저장
        String uiFilePath = null;
        String applicationUrl = fileService.uploadApplicationToS3(uiFilePath, applicationId, user.getId());
        application.updateApplication(request);
        applicationService.save(application);
        fileService.deleteFileFromLocal(uiImagePath);
        fileService.deleteFileFromLocal(uiFilePath);

        log.info(getResponseLog(PATCH, endpointPath, request, null));
        return ResponseEntity.ok().body(new BaseResponse());
    }

    @GetMapping("/test/{applicationId}")
    public ResponseEntity<BaseResponse<TestResponse>> test(@PathVariable Long applicationId) throws ParseException{
        String endpointPath = "/test/" + applicationId;
        log.info(getRequestLog(GET, endpointPath, null));
        JSONObject UI = applicationService.getApplicationUIV1(applicationId);
        String ret = ApplicationUtil.parseApplicationUi(UI);
        TestResponse response = new TestResponse(ret);
        log.info(getResponseLog(GET, endpointPath, null, response));
        return ResponseEntity.ok().body(new BaseResponse<>(response));
    }

    @GetMapping("/applications")
    public ResponseEntity<BaseResponse<List<ApplicationListResponse>>> findByApplicationNameContaining(@RequestParam String keyword) {
        String endpointPath = "/applications?keyword=" + keyword;
        log.info(getRequestLog(GET, endpointPath, null));
        List<Application> applicationList = applicationService.findByNameContaining(keyword);
        List<ApplicationListResponse> response = applicationList.stream().map(ApplicationListResponse::new).collect(Collectors.toList());
        log.info(getResponseLog(GET, endpointPath, null, response));
        return ResponseEntity.ok().body(new BaseResponse<>(response));
    }

    @GetMapping("/applications/{applicationId}/information")
    public ResponseEntity<BaseResponse<ApplicationInformationResponse>> findByApplicationNameContaining(@PathVariable Long applicationId) {
        String endpointPath = "/applications/" + applicationId + "/information";
        log.info(getRequestLog(GET, endpointPath, null));
        Application application = applicationService.findById(applicationId);
        ApplicationInformationResponse response = new ApplicationInformationResponse(application);
        log.info(getResponseLog(GET, endpointPath, null, response));
        return ResponseEntity.ok().body(new BaseResponse<>(response));
    }

    @GetMapping("/applications/all")
    public ResponseEntity<BaseResponse<List<ApplicationListResponse>>> findAllApplication() {
        String endpointPath = "/applications/all";
        log.info(getRequestLog(GET, endpointPath, null));
        List<Application> application = applicationService.findAllApplication();
        List<ApplicationListResponse> response  = application.stream().map(ApplicationListResponse::new).collect(Collectors.toList());
        log.info(getResponseLog(GET, endpointPath, null,response));
        return ResponseEntity.ok()
                .body(new BaseResponse<>(response));
    }
}
