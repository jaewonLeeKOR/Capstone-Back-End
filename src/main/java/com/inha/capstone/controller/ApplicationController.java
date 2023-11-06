package com.inha.capstone.controller;

import com.inha.capstone.Dto.ApplicationDto;
import com.inha.capstone.Dto.ApplicationDto.CreateApplicationRequest;
import com.inha.capstone.Dto.ApplicationDto.UpdateApplicationRequest;
import com.inha.capstone.config.BaseResponse;
import com.inha.capstone.domain.Application;
import com.inha.capstone.domain.User;
import com.inha.capstone.service.ApplicationService;
import com.inha.capstone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;

    @PostMapping("/application")
    public ResponseEntity<BaseResponse<Void>> createApplication(Principal principal, @RequestBody @Valid CreateApplicationRequest request) throws ParseException {
        User user = userService.findById(principal.getName());
        System.out.println(request);
        Application application = new Application(user, request.getCategory(), request.getDescription(), LocalDateTime.now(), LocalDateTime.now());
        applicationService.save(application, request.getUi());

        return ResponseEntity.ok()
                .body(new BaseResponse());
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<BaseResponse<ApplicationDto.ApplicationUiResponse>> login(@PathVariable Long applicationId){

        JSONObject UI = applicationService.getApplicationUI(applicationId);

        return ResponseEntity.ok()
                .body(new BaseResponse<ApplicationDto.ApplicationUiResponse>(
                    new ApplicationDto.ApplicationUiResponse(UI)
                ));
    }

    @PutMapping("/application/{applicationId}")
    public ResponseEntity<BaseResponse<Void>> updateApplication(Principal principal, @PathVariable Long applicationId, @RequestBody @Valid UpdateApplicationRequest request) throws ParseException{
        applicationService.checkPermissionForApplication(principal, applicationId);
        applicationService.updateApplication(applicationId, request.getUi());

        return ResponseEntity.ok()
                .body(new BaseResponse());
    }
}
