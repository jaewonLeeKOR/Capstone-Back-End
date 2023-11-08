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
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @NotBlank
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private ApplicationCategory applicationCategory;

    public Application(User user, String category,String description, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.user = user;
        this.description = description;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.applicationCategory = ApplicationCategory.nameOf(category);
    }
}
