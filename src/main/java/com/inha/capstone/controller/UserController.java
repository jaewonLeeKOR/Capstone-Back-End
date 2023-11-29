package com.inha.capstone.controller;


import com.inha.capstone.Dto.Token;
import com.inha.capstone.Dto.UserDto.CreateUserRequest;
import com.inha.capstone.config.BaseResponse;
import com.inha.capstone.domain.User;
import com.inha.capstone.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.inha.capstone.Dto.UserDto.*;
import static com.inha.capstone.util.LogUtil.getRequestLog;
import static com.inha.capstone.util.LogUtil.getResponseLog;
import static com.inha.capstone.util.LogUtil.HttpRequestMethod.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<BaseResponse<Void>> createUser(@RequestBody @Valid CreateUserRequest request){
        String endpointPath = "/signUp";
        log.info(getRequestLog(POST, endpointPath, request));
        User user = new User(request.getId(), request.getPassword(), request.getNickname(), LocalDateTime.now());
        userService.save(user);
        log.info(getResponseLog(POST, endpointPath, request, null));
        return ResponseEntity.ok()
                .body(new BaseResponse());
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request){
        String endpointPath = "/login";
        log.info(getRequestLog(POST, endpointPath, request));
        Token token = userService.login(request.getId(), request.getPassword());
        User user = userService.findByUserId(request.getId());
        LoginResponse response = new LoginResponse(token, user);
        log.info(getResponseLog(POST, endpointPath, request, response));
        return ResponseEntity.ok()
                .body(new BaseResponse<>(response));
    }

    @GetMapping("/users")
    public ResponseEntity<BaseResponse<List<UserListResponse>>> findByUserIdContaining(@RequestParam("keyword")String keyword){
        String endpointPath = "/users?keyword=" + keyword;
        log.info(getRequestLog(GET, endpointPath, "keyword : " + keyword));
        List<User> userList = userService.findByUserIdContaining(keyword);
        List<UserListResponse> response = userList.stream().map(UserListResponse::new).collect(Collectors.toList());
        log.info(getResponseLog(GET, endpointPath, "keyword : " + keyword, response));
        return ResponseEntity.ok()
                .body(new BaseResponse<>(response));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<BaseResponse<Void>> removeUser(@PathVariable Long userId){
        String endpointPath = "/users/" + userId;
        log.info(getRequestLog(DELETE, endpointPath, null));
        userService.removeUser(userId);
        log.info(getResponseLog(DELETE, endpointPath, null, null));
        return ResponseEntity.ok()
                .body(new BaseResponse<>());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<BaseResponse<UserInformationResponse>> getUserDetail(@PathVariable Long userId){
        String endpointPath = "/users/" + userId;
        log.info(getRequestLog(GET, endpointPath, null));
        User user = userService.findOne(userId);
        UserInformationResponse response = new UserInformationResponse(user);
        log.info(getResponseLog(GET, endpointPath, null, response));
        return ResponseEntity.ok()
                .body(new BaseResponse<>(response));
    }

}
