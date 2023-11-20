package com.inha.capstone.controller;

import com.inha.capstone.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/file")
@RestController
@RequiredArgsConstructor
public class FileController {
  private final FileService fileService;
}

