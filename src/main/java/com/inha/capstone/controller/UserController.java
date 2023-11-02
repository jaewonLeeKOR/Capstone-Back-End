package com.inha.capstone.controller;


import com.inha.capstone.Dto.Token;
import com.inha.capstone.Dto.UserDto.CreateUserRequest;
import com.inha.capstone.config.BaseResponse;
import com.inha.capstone.domain.User;
import com.inha.capstone.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
    public ResponseEntity<BaseResponse<Token>> login(@RequestBody @Valid LoginRequest request){

        Token token = userService.login(request.getId(), request.getPassword());

        return ResponseEntity.ok()
                .body(new BaseResponse<>(token));
    }

}
