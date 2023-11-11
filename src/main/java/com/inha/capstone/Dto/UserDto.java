package com.inha.capstone.Dto;


import com.inha.capstone.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

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
    public static class LoginResponse{
        private Token token;
        private Long id;
        private String userId;
        private String nickname;

        public LoginResponse(Token token, User user) {
            this.token = token;
            this.id = user.getId();
            this.userId = user.getUserId();
            this.nickname = user.getNickname();
        }
    }

    @Data
    public static class UserListResponse{
        private Long id;
        private String userId;
        private String nickname;

        public UserListResponse(User user) {
            this.id = user.getId();
            this.userId = user.getUserId();
            this.nickname = user.getNickname();
        }
    }

    @Data
    public static class UserInformationResponse{
        private Long id;
        private String userId;
        private String nickname;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;

        public UserInformationResponse(User user) {
            this.id = user.getId();
            this.userId = user.getUserId();
            this.nickname = user.getNickname();
            this.createdDate = user.getCreatedDate();
            this.modifiedDate = user.getCreatedDate();
        }
    }
}
