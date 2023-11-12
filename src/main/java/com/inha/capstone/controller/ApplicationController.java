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
import org.hibernate.sql.Update;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;

    @PostMapping("/application")
    public ResponseEntity<BaseResponse<Void>> createApplication(Principal principal, @RequestBody @Valid CreateApplicationRequest request) throws ParseException {
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

        return ResponseEntity.ok()
                .body(new BaseResponse());
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<BaseResponse<ApplicationDto.ApplicationUiResponse>> getApplicationUi(@PathVariable Long applicationId){

        JSONObject UI = applicationService.getApplicationUI(applicationId);

        return ResponseEntity.ok()
                .body(new BaseResponse<>(
                    new ApplicationDto.ApplicationUiResponse(UI)
                ));
    }

    @PatchMapping("/applications/{applicationId}")
    public ResponseEntity<BaseResponse<Void>> updateApplication(Principal principal, @PathVariable Long applicationId, @RequestBody @Valid UpdateApplicationRequest request) throws ParseException{
        applicationService.checkPermissionForApplication(principal, applicationId);
        applicationService.updateApplication(applicationId, request.getUi());

        return ResponseEntity.ok()
                .body(new BaseResponse());
    }

    @GetMapping("/test/{applicationId}")
    public ResponseEntity<BaseResponse<ApplicationDto.TestResponse>> test(@PathVariable Long applicationId) throws ParseException{

        JSONObject UI = applicationService.getApplicationUI(applicationId);
        String ret = ApplicationUtil.parseApplicationUi(UI);

        return ResponseEntity.ok()
                .body(new BaseResponse<>(
                        new ApplicationDto.TestResponse(ret)
                ));
    }

    @GetMapping("/applications")
    public ResponseEntity<BaseResponse<List<ApplicationListResponse>>> findByApplicationNameContaining(@RequestParam String keyword) {

        List<Application> applicationList = applicationService.findByNameContaining(keyword);

        return ResponseEntity.ok()
                .body(new BaseResponse<>(applicationList.stream().map(ApplicationListResponse::new).collect(Collectors.toList())));
    }

    @GetMapping("/applications/{applicationId}/information")
    public ResponseEntity<BaseResponse<ApplicationDto.ApplicationInformationResponse>> findByApplicationNameContaining(@PathVariable Long applicationId) {

        Application application = applicationService.findById(applicationId);

        return ResponseEntity.ok()
                .body(new BaseResponse<>(new ApplicationDto.ApplicationInformationResponse(application)));
    }

    @GetMapping("/applications/all")
    public ResponseEntity<BaseResponse<List<ApplicationListResponse>>> findAllApplication() {

        List<Application> application = applicationService.findAllApplication();

        return ResponseEntity.ok()
                .body(new BaseResponse<>(application.stream().map(ApplicationListResponse::new).collect(Collectors.toList())));
    }
}
