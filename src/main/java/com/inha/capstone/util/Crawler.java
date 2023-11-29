package com.inha.capstone.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
@Component
public class Crawler {

    // 크롤러 저장 위치
    @Value("${spring.python.path}")
    String pythonPath;
    
    // open AI 키
    @Value("${spring.gpt.key}")
    String gptKey;
    
    // UI 임시 저장 경로
    @Value("${spring.image.tmp.path}")
    Path tmpPath;

    // html 저장 경로
    @Value("${spring.html.path}")
    Path htmlPath;
    public String makeHtml(MultipartFile sourceImage, String fileName) throws IOException, InterruptedException {
        try {
            Process process = executeCrawler(sourceImage);
            String htmlString = makeHtmlString(process);
            createHtmlFile(htmlString, htmlPath, fileName);
            System.out.println(htmlPath + "/" + fileName + ".html");
            return htmlPath + "/" + fileName + ".html";
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return "";
        }
    }


    // 파이썬 실행
    private Process executeCrawler(MultipartFile image) throws IOException, InterruptedException {
        Path tempImageFile = createTempImageFile(image.getBytes(), tmpPath);
        String[] command = {"python", pythonPath, gptKey, tempImageFile.toString()};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        process.waitFor();
        Files.delete(tempImageFile);
        return process;
    }

    //
    private String makeHtmlString(Process process) throws IOException{
        InputStream inputStream = process.getInputStream();
        // 각 스트림을 읽어오기 위한 Reader 생성
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        // 표준 출력 읽기
        String line;
        String ret = "";
        while ((line = inputReader.readLine()) != null) {
            ret += line;
        }
        ret = new String(ret.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        return ret;
    }


    // 이미지 임시 저장
    private static Path createTempImageFile(byte[] imageBytes, Path targetDirectory) throws IOException {
        // 임시 이미지 파일 생성
        Path tempImagePath = Files.createTempFile(targetDirectory, "tempImage", ".png");
        // 이미지 바이트를 파일에 기록
        Files.write(tempImagePath, imageBytes, StandardOpenOption.CREATE);

        return tempImagePath;
    }

    private static void createHtmlFile(String html, Path targetDirectory, String fileName) throws IOException{
        Path resolve = targetDirectory.resolve(fileName+".html");
        Files.write(resolve, html.getBytes(StandardCharsets.UTF_8));
    }

}
