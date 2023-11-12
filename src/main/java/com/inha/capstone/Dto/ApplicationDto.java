package com.inha.capstone.Dto;

import com.inha.capstone.domain.Application;
import com.inha.capstone.domain.ApplicationCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.simple.JSONObject;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;


public class ApplicationDto {

    @Data
    public static class CreateApplicationRequest{
        @NotBlank
        String description;

        @NotBlank
        String category;

        @NotBlank
        String ui;

        @NotBlank
        String name;
    }

    @Data
    public static class UpdateApplicationRequest{
        @NotBlank
        String ui;
    }

    @Data
    @AllArgsConstructor
    public static class ApplicationUiResponse{
        JSONObject UI;
    }

    @Data
    @AllArgsConstructor
    public static class TestResponse{
        String UI;
    }

    @Data
    public static class ApplicationListResponse{
        Long applicationId;
        String name;
        String description;
        String category;
        LocalDateTime createdDate;

        public ApplicationListResponse(Application application) {
            this.applicationId =application.getApplicationId();
            this.name = application.getName();
            this.description = application.getDescription();
            this.category = application.getApplicationCategory().getName();
            this.createdDate = application.getCreatedDate();
        }
    }
}
