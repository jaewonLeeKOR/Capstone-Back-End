package com.inha.capstone.controller;

import com.inha.capstone.Dto.ApplicationDto;
import com.inha.capstone.Dto.ApplicationDto.*;
import com.inha.capstone.config.BaseException;
import com.inha.capstone.config.BaseResponse;
import com.inha.capstone.config.BaseResponseStatus;
import com.inha.capstone.domain.Application;
import com.inha.capstone.domain.ApplicationCategory;
import com.inha.capstone.domain.FileCategory;
import com.inha.capstone.domain.User;
import com.inha.capstone.service.ApplicationService;
import com.inha.capstone.service.FileService;
import com.inha.capstone.service.UserService;
import com.inha.capstone.util.ApplicationUtil;
import com.inha.capstone.util.Crawler;
import com.inha.capstone.util.MakeRealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
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
    private final Crawler crawler;
    private final MakeRealService makeRealService;
    @Value("${env.path.tmp.html}")
    Path tmpPath;

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
    public ResponseEntity<BaseResponse<CreateApplicationResponse>> createApplication(
        Principal principal,
        @RequestPart(value = "uiImage") MultipartFile multipartUiFile,
        @RequestPart(value = "thumbnailImage", required = false) MultipartFile multipartThumbnailFile,
        @RequestPart(value = "info") CreateApplicationRequest request
    ) throws ParseException {
        String endpointPath = "/application";
        log.info(getRequestLog(POST, endpointPath, request));

        User user = userService.findByUserId(principal.getName());
        Application application = new Application(request, user);
        applicationService.save(application);

        String uiFilePath = null;
        try {
//            uiFilePath = crawler.makeHtml(multipartUiFile);

            String uiImageUrl = fileService.uploadFileToS3WithoutDatabaseMultipartFile(FileCategory.UI_IMAGE, multipartUiFile, application.getApplicationId(), user.getId());
            String htmlString =  makeRealService.makeRealConverter(uiImageUrl);
            uiFilePath = crawler.createHtmlFile(htmlString, tmpPath);
        } catch (IOException e) {
            fileService.deleteFileFromLocal(uiFilePath);
            throw new BaseException(BaseResponseStatus.CRAWLER_ERROR);
//        } catch (InterruptedException e) {
//            fileService.deleteFileFromLocal(uiFilePath);
//            throw new BaseException(BaseResponseStatus.CRAWLER_ERROR);
        }
        String applicationUrl = fileService.uploadFileToS3WithoutDatabase(FileCategory.APPLICATION, uiFilePath, application.getApplicationId(), user.getId());
        application.setApplicationUrl(applicationUrl);
        fileService.deleteFileFromLocal(uiFilePath);

        if(multipartThumbnailFile != null) {
            String thumbnailUrl = fileService.uploadFileToS3(multipartThumbnailFile, FileCategory.IMAGE, application.getApplicationId(), user.getId(), 0L);
            application.setThumbnailUrl(thumbnailUrl);
        }
        application = applicationService.save(application);
        CreateApplicationResponse response = new CreateApplicationResponse(application.getApplicationId(),application.getApplicationUrl());

        log.info(getResponseLog(POST, endpointPath, request, response));
        return ResponseEntity.ok().body(new BaseResponse(response));
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
    public ResponseEntity<BaseResponse<UpdateApplicationResponse>> updateApplication(
        Principal principal,
        @RequestPart(value = "uiImage", required = false) MultipartFile multipartUiFile,
        @RequestPart(value = "thumbnailImage", required = false) MultipartFile multipartThumbnailFile,
        @RequestPart(value = "info") UpdateApplicationRequest request,
        @PathVariable Long applicationId
    ) throws ParseException{
        String endpointPath = "/application/" + applicationId;
        log.info(getRequestLog(PATCH, endpointPath, request));
        applicationService.checkPermissionForApplication(principal, applicationId);

        User user = userService.findByUserId(principal.getName());
        Application application = applicationService.findById(applicationId);
        // TODO: uiImagePath 로 이미지를 html, css 코드로 변환후 해당 파일들이 존재하는 directoryPath 를 uiFilePath 에 저장

        if(multipartUiFile != null) {
            String uiFilePath = null;
            try {
//                uiFilePath = crawler.makeHtml(multipartUiFile);

                String uiImageUrl = fileService.uploadFileToS3WithoutDatabaseMultipartFile(FileCategory.UI_IMAGE, multipartUiFile, applicationId, user.getId());
                String htmlString =  makeRealService.makeRealConverter(uiImageUrl);
                uiFilePath = crawler.createHtmlFile(htmlString, tmpPath);
            } catch (IOException e) {
                fileService.deleteFileFromLocal(uiFilePath);
                throw new BaseException(BaseResponseStatus.CRAWLER_ERROR);
//            } catch (InterruptedException e) {
//                fileService.deleteFileFromLocal(uiFilePath);
//                throw new BaseException(BaseResponseStatus.CRAWLER_ERROR);
            }
            String applicationUrl = fileService.uploadFileToS3WithoutDatabase(FileCategory.APPLICATION, uiFilePath, applicationId, user.getId());
            application.setApplicationUrl(applicationUrl);
            fileService.deleteFileFromLocal(uiFilePath);
        }
        if(multipartThumbnailFile != null) {
            String thumbnailUrl = fileService.uploadFileToS3(multipartThumbnailFile, FileCategory.IMAGE, applicationId, user.getId(), 0L);
            application.setThumbnailUrl(thumbnailUrl);
        }
        application.updateApplication(request);
        application = applicationService.save(application);
        UpdateApplicationResponse response = new UpdateApplicationResponse(application.getApplicationUrl());

        log.info(getResponseLog(PATCH, endpointPath, request, response));
        return ResponseEntity.ok().body(new BaseResponse(response));
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
