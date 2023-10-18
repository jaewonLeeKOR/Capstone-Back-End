package com.inha.capstone.domain;

import jakarta.persistence.*;
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
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
//    category;

    public Application(User user, String description, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.user = user;
        this.description = description;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}
