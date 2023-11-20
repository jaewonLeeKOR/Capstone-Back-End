package com.inha.capstone.repository;

import com.inha.capstone.domain.Application;
import com.inha.capstone.domain.FileObject;
import com.inha.capstone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileObject, Long> {
  Optional<FileObject> findFileObjectByApplicationAndUserAndComponentId(Application application, User user, Long componentId);
}
