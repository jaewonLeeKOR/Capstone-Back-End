package com.inha.capstone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @NotBlank
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private ApplicationCategory applicationCategory;
}
