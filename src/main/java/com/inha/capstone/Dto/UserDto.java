package com.inha.capstone.Dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;

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
}
