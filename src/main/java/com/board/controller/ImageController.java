package com.board.controller;

import com.board.entity.Image;
import com.board.repository.ImageRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.LinkedList;

@RestController
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @PostMapping("/api/images/pet")
    public ResponseEntity<String> PetImage(@RequestParam("image") MultipartFile multipartFile) throws IOException, InterruptedException {
        String fileName = multipartFile.getOriginalFilename();
        Path path = Paths.get("/home/ubuntu/animalcareplus/image/" + fileName);
        Files.write(path, multipartFile.getBytes());

        Image image = new Image();
        image.setFileName(fileName);
        imageRepository.save(image);

        // 이미지 파일 경로
        Path paths = Paths.get("/home/ubuntu/animalcareplus/image/" + image.getFileName());
        String imagePath = paths.toString();

        // 파이썬 스크립트 경로
        String pythonScriptPath = "/home/ubuntu/animalcareplus/python_ai/pet.py";


        String result = "";
        // 파이썬 실행 명령
        String[] cmd = {"python3", pythonScriptPath, imagePath};

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder output = new StringBuilder();
        StringBuilder errorMessage = new StringBuilder();

        String lastLine = null;
        while ((line = in.readLine()) != null) {
            output.append(line).append("\n");
            lastLine = line; // 각 줄을 읽을 때마다 마지막 줄을 갱신합니다.
        }

        result = lastLine; // 마지막으로 읽은 줄을 결과로 설정합니다.

        while ((line = err.readLine()) != null) {
            errorMessage.append(line).append("\n");
        }

        int exitCode = process.waitFor();

//        System.out.println("Exit code: " + exitCode);
//            result = output.toString();
//        System.out.println("Error: " + errorMessage.toString());

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(result);
    }
    @PostMapping("/api/images/skin")
    public ResponseEntity<String> SkinImage(@RequestParam("image") MultipartFile multipartFile) throws IOException, InterruptedException {
        String fileName = multipartFile.getOriginalFilename();
        Path path = Paths.get("/home/ubuntu/animalcareplus/image/" + fileName);
        Files.write(path, multipartFile.getBytes());

        Image image = new Image();
        image.setFileName(fileName);
        imageRepository.save(image);

        // 이미지 파일 경로
        Path paths = Paths.get("/home/ubuntu/animalcareplus/image/" + image.getFileName());
        String imagePath = paths.toString();

        // 파이썬 스크립트 경로
        String pythonScriptPath = "/home/ubuntu/animalcareplus/python_ai/SkinDisease.py";


        String result = "";
        // 파이썬 실행 명령
        String[] cmd = {"python3", pythonScriptPath, imagePath};

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder output = new StringBuilder();
        StringBuilder errorMessage = new StringBuilder();

        String lastLine = null;
        while ((line = in.readLine()) != null) {
            output.append(line).append("\n");
            lastLine = line; // 각 줄을 읽을 때마다 마지막 줄을 갱신합니다.
        }

        result = lastLine; // 마지막으로 읽은 줄을 결과로 설정합니다.

        while ((line = err.readLine()) != null) {
            errorMessage.append(line).append("\n");
        }

        int exitCode = process.waitFor();

//        System.out.println("Exit code: " + exitCode);
//            result = output.toString();
//        System.out.println("Error: " + errorMessage.toString());

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(result);
    }

    @GetMapping("/api/images/report")
    public ResponseEntity<String> report() throws IOException, InterruptedException {
        // 파이썬 스크립트 경로
        String pythonScriptPath = "/home/ubuntu/animalcareplus/python_ai/news.py";

        // 파이썬 실행 명령
        String[] cmd = {"python3", pythonScriptPath};

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));

//        String result = "";
        String line;
        StringBuilder output = new StringBuilder();
        StringBuilder errorMessage = new StringBuilder();

        LinkedList<String> lastLines = new LinkedList<>();
        while ((line = in.readLine()) != null) {
            if (lastLines.size() == 3) {
                lastLines.removeFirst();
            }
            lastLines.addLast(line);
        }

        while ((line = err.readLine()) != null) {
            errorMessage.append(line).append("\n");
        }

        int exitCode = process.waitFor();

        // 마지막 3줄을 하나의 문자열로 합칩니다.
        StringBuilder resultBuilder = new StringBuilder();
        for (String lastLine : lastLines) {
            resultBuilder.append(lastLine).append("\n");
        }
        String result = resultBuilder.toString().trim(); // 마지막에 불필요한 개행 문자를 제거합니다.

//        System.out.println("Exit code: " + exitCode);
//            result = output.toString();
//        System.out.println("Error: " + errorMessage.toString());

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(result);
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        } else {
            return null;
        }
    }

    private MediaType getMediaTypeForImageExtension(String extension) {
        switch (extension) {
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            default:
                return MediaType.IMAGE_JPEG;
        }
    }
}