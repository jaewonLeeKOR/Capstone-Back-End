package com.inha.capstone.Dto;

import com.inha.capstone.domain.Application;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;


public class ApplicationDto {

    @Data
    public static class CreateApplicationRequestV1 {
        @NotBlank
        String description;

        @NotBlank
        String category;

        @NotBlank
        String ui;

        @NotBlank
        String title;
    }
    @Data
    public static class CreateApplicationRequest {
        @NotBlank
        String title;
        @NotBlank
        String subTitle;
        @NotBlank
        String description;
        @NotBlank
        String category;
    }

    @Data
    @AllArgsConstructor
    public static class CreateApplicationResponse {
        Long applicationId;
        String applicationUrl;
    }

    @Data
    public static class UpdateApplicationRequestV1 {
        @NotBlank
        String ui;
    }

    @Data
    public static class UpdateApplicationRequest {
        String title;
        String subTitle;
        String description;
        String category;
    }

    @Data
    @AllArgsConstructor
    public static class UpdateApplicationResponse {
        String applicationUrl;
    }

    @Data
    @AllArgsConstructor
    public static class ApplicationUiResponse{
        JSONObject UI;
    }

    @Data
    @AllArgsConstructor
    public static class ApplicationUrlResponse{
        String applicationUrl;
    }

    @Data
    @AllArgsConstructor
    public static class TestResponse{
        String UI;
    }

    @Data
    public static class ApplicationListResponse{
        Long applicationId;
        String title;
        String subTitle;
        String description;
        String applicationUrl;
        String thumbnailUrl;
        String category;
        LocalDateTime createdDate;

        public ApplicationListResponse(Application application) {
            this.applicationId =application.getApplicationId();
            this.title = application.getTitle();
            this.subTitle = application.getSubTitle();
            this.description = application.getDescription();
            this.applicationUrl = application.getApplicationUrl();
            this.thumbnailUrl = application.getThumbnailUrl();
            this.category = application.getApplicationCategory().getName();
            this.createdDate = application.getCreatedDate();
        }
    }

    @Data
    public static class ApplicationInformationResponse{
        Long applicationId;
        String title;
        String subTitle;
        String description;
        String applicationUrl;
        String thumbnailUrl;
        String category;
        LocalDateTime createdDate;
        LocalDateTime modifiedDate;

        public ApplicationInformationResponse(Application application) {
            this.applicationId =application.getApplicationId();
            this.title = application.getTitle();
            this.subTitle = application.getSubTitle();
            this.description = application.getDescription();
            this.applicationUrl = application.getApplicationUrl();
            this.thumbnailUrl = application.getThumbnailUrl();
            this.category = application.getApplicationCategory().getName();
            this.createdDate = application.getCreatedDate();
            this.modifiedDate = application.getModifiedDate();
        }
    }
}
