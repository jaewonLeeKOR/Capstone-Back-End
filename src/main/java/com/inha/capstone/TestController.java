package com.inha.capstone;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class TestController {
    @GetMapping("/test")
    public String testServer() {
        return "test success";
    }
}