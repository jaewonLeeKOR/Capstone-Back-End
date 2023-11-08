package com.inha.capstone.controller;


import com.inha.capstone.Dto.Token;
import com.inha.capstone.Dto.UserDto.CreateUserRequest;
import com.inha.capstone.config.BaseResponse;
import com.inha.capstone.domain.User;
import com.inha.capstone.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/users")
    public ResponseEntity<BaseResponse<List<UserListResponse>>> findByUserIdContaining(@RequestParam("keyword")String keyword){
        List<User> userList = userService.findByUserIdContaining(keyword);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(userList.stream().map(UserListResponse::new).collect(Collectors.toList())));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<BaseResponse<Void>> removeUser(@PathVariable Long userId){
        userService.removeUser(userId);

        return ResponseEntity.ok()
                .body(new BaseResponse<>());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<BaseResponse<UserInformationResponse>> getUserDetail(@PathVariable Long userId){
        User user = userService.findOne(userId);

        return ResponseEntity.ok()
                .body(new BaseResponse<>(new UserInformationResponse(user)));
    }

}
