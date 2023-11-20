package com.inha.capstone.Dto;

import com.inha.capstone.domain.FileCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class FIleDto {
  /**
   * 파일 메타데이터 저장을 위한 request DTO
   * @FileCategory fileCategory
   * @Long applicationId
   * @Long userId
   * @Long componentId
   */
  @Data
  public static class PostFileRequest {
    @NotBlank
    FileCategory fileCategory;
    @NotBlank
    Long applicationId;
    @NotBlank
    Long userId;
    @NotBlank
    Long componentId;
  }

  /**
   * 파일 저장 후 url 반환을 위한 response DTO
   * @String filePath
   */
  @Data
  public static class PostFileResponse {
    String filePath;
  }
}
