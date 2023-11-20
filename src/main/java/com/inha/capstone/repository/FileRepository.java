package com.inha.capstone.repository;

import com.inha.capstone.domain.Application;
import com.inha.capstone.domain.FileObject;
import com.inha.capstone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileObject, Long> {
  /**
   * application의 사용자별 이미지 데이터 조회
   * application 내 공용 이미지, 개인 이미지 통합
   * @param application
   * @param user
   * @return
   */
  @Query("SELECT " +
            "f " +
          "FROM " +
            "FileObject f " +
          "WHERE " +
            "f.application = :application " +
              "AND " +
            "( " +
              "f.user = :user " +
                "OR " +
              "f.user IS NULL " +
      ")"
  )
  List<FileObject> findFileObjectsByApplicationAndUser(Application application, User user);

  Optional<FileObject> findFileObjectByApplicationAndUserAndComponentId(Application application, User user, Long componentId);
}
