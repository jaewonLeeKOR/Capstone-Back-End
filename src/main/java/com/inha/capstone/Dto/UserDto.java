package com.inha.capstone.Dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

public class UserDto {
    @Data
    public static class CreateUserRequest{
        @NotBlank
        private String id;
        @NotBlank
        private String password;
        @NotBlank
        private String nickname;
    }

    @Data
    public static class LoginRequest{
        @NotBlank
        private String id;
        @NotBlank
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class LoginResponse{
        private Token token;
        private String nickname;
    }
}
