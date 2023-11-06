package com.inha.capstone.Dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
}
