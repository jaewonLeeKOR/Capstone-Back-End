package com.inha.capstone.domain;

import com.inha.capstone.Dto.ApplicationDto.*;
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
    private String title;
    private String subTitle;
    private String description;
    private String applicationUrl;
    private String thumbnailUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private ApplicationCategory applicationCategory;
    public Application(CreateApplicationRequest request, User user) {
        this.title = request.getTitle();
        this.subTitle = request.getSubTitle();
        this.description = request.getDescription();
        this.user = user;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        this.applicationCategory = ApplicationCategory.nameOf(request.getCategory());
    }

    public void updateApplication(UpdateApplicationRequest request) {
        this.title = request.getTitle() == null ? this.title : request.getTitle();
        this.subTitle = request.getSubTitle() == null ? this.subTitle : request.getSubTitle();
        this.description = request.getDescription() == null ? this.description : request.getDescription();
        this.applicationCategory = request.getCategory() == null ? this.applicationCategory: ApplicationCategory.nameOf(request.getCategory());
    }
}
