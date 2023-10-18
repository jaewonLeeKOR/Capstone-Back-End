package com.inha.capstone.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "Users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String id;
    private String password;
    private String nickname;
    private LocalDateTime createdDate;
    @OneToMany(mappedBy = "user")
    private List<Application> applicationList;

    public User(String id, String password, String nickname, LocalDateTime createdDate) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
        this.createdDate = createdDate;
    }
}
