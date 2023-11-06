package com.inha.capstone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
