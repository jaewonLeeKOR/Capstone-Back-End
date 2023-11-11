package com.inha.capstone.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.simple.JSONObject;


public class ApplicationDto {

    @Data
    public static class CreateApplicationRequest{
        @NotBlank
        String description;

        @NotBlank
        String category;

        @NotBlank
        String ui;
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
}
