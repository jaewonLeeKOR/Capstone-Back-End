package com.inha.capstone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.lang.Nullable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FileObject {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long fileId;
  @Nullable
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;
  @Nullable
  @ManyToOne(fetch = FetchType.LAZY)
  private Application application;
  private Long componentId;
  @Enumerated(EnumType.STRING)
  private FileCategory category;
  private String filePath;
}
