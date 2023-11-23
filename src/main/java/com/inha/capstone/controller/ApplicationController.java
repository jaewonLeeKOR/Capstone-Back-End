package com.inha.capstone.controller;

import com.inha.capstone.Dto.ApplicationDto;
import com.inha.capstone.Dto.ApplicationDto.ApplicationListResponse;
import com.inha.capstone.Dto.ApplicationDto.CreateApplicationRequest;
import com.inha.capstone.Dto.ApplicationDto.UpdateApplicationRequest;
import com.inha.capstone.config.BaseResponse;
import com.inha.capstone.domain.Application;
import com.inha.capstone.domain.ApplicationCategory;
import com.inha.capstone.domain.User;
import com.inha.capstone.service.ApplicationService;
import com.inha.capstone.service.UserService;
import com.inha.capstone.util.ApplicationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Update;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/application")
    public ResponseEntity<BaseResponse<Void>> createApplication(Principal principal, @RequestBody @Valid CreateApplicationRequest request) throws ParseException {
        String endpointPath = "/application";
        log.info(getRequestLog(POST, endpointPath, request));
        User user = userService.findByUserId(principal.getName());
        System.out.println(request);

        Application application = Application.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .applicationCategory(ApplicationCategory.nameOf(request.getCategory()))
                .build();
        applicationService.save(application, request.getUi());
        log.info(getResponseLog(POST, endpointPath, request, null));

        return ResponseEntity.ok()
                .body(new BaseResponse());
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<BaseResponse<ApplicationDto.ApplicationUiResponse>> getApplicationUi(@PathVariable Long applicationId){
        String endpointPath = "/applications/" + applicationId;
        log.info(getRequestLog(GET, endpointPath, null));
        JSONObject UI = applicationService.getApplicationUI(applicationId);
        ApplicationDto.ApplicationUiResponse response = new ApplicationDto.ApplicationUiResponse(UI);
        log.info(getResponseLog(GET, endpointPath, null, response));
        return ResponseEntity.ok()
                .body(new BaseResponse<>(
                    response
                ));
    }

    @PatchMapping("/applications/{applicationId}")
    public ResponseEntity<BaseResponse<Void>> updateApplication(Principal principal, @PathVariable Long applicationId, @RequestBody @Valid UpdateApplicationRequest request) throws ParseException{
        String endpointPath = "/application/" + applicationId;
        log.info(getRequestLog(PATCH, endpointPath, request));
        applicationService.checkPermissionForApplication(principal, applicationId);
        applicationService.updateApplication(applicationId, request.getUi());
        log.info(getResponseLog(PATCH, endpointPath, request, null));
        return ResponseEntity.ok()
                .body(new BaseResponse());
    }

    @GetMapping("/test/{applicationId}")
    public ResponseEntity<BaseResponse<ApplicationDto.TestResponse>> test(@PathVariable Long applicationId) throws ParseException{
        String endpointPath = "/test/" + applicationId;
        log.info(getRequestLog(GET, endpointPath, null));
        JSONObject UI = applicationService.getApplicationUI(applicationId);
        String ret = ApplicationUtil.parseApplicationUi(UI);
        ApplicationDto.TestResponse response = new ApplicationDto.TestResponse(ret);
        log.info(getResponseLog(GET, endpointPath, null, response));
        return ResponseEntity.ok()
                .body(new BaseResponse<>(
                        response
                ));
    }

    @GetMapping("/applications")
    public ResponseEntity<BaseResponse<List<ApplicationListResponse>>> findByApplicationNameContaining(@RequestParam String keyword) {
        String endpointPath = "/applications?keyword=" + keyword;
        log.info(getRequestLog(GET, endpointPath, null));
        List<Application> applicationList = applicationService.findByNameContaining(keyword);
        List<ApplicationListResponse> response = applicationList.stream().map(ApplicationListResponse::new).collect(Collectors.toList());
        log.info(getResponseLog(GET, endpointPath, null, response));
        return ResponseEntity.ok()
                .body(new BaseResponse<>(response));
    }

    @GetMapping("/applications/{applicationId}/information")
    public ResponseEntity<BaseResponse<ApplicationDto.ApplicationInformationResponse>> findByApplicationNameContaining(@PathVariable Long applicationId) {
        String endpointPath = "/applications/" + applicationId + "/information";
        log.info(getRequestLog(GET, endpointPath, null));
        Application application = applicationService.findById(applicationId);
        ApplicationDto.ApplicationInformationResponse response = new ApplicationDto.ApplicationInformationResponse(application);
        log.info(getResponseLog(GET, endpointPath, null, response));
        return ResponseEntity.ok()
                .body(new BaseResponse<>(response));
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
