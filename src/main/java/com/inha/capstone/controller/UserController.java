package com.inha.capstone.controller;


import com.inha.capstone.Dto.Token;
import com.inha.capstone.Dto.UserDto.CreateUserRequest;
import com.inha.capstone.config.BaseResponse;
import com.inha.capstone.domain.User;
import com.inha.capstone.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.inha.capstone.Dto.UserDto.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<BaseResponse<Void>> createUser(@RequestBody @Valid CreateUserRequest request){

        User user = new User(request.getId(), request.getPassword(), request.getNickname(), LocalDateTime.now());

        userService.save(user);

        return ResponseEntity.ok()
                .body(new BaseResponse());
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request){

        Token token = userService.login(request.getId(), request.getPassword());
        User user = userService.findByUserId(request.getId());
        return ResponseEntity.ok()
                .body(new BaseResponse<>(new LoginResponse(token, user.getNickname())));
    }

}
