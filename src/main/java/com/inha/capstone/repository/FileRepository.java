package com.inha.capstone.repository;

import com.inha.capstone.domain.FileObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileObject, Long> {
}
