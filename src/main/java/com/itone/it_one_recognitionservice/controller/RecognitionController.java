package com.itone.it_one_recognitionservice.controller;

import com.itone.it_one_recognitionservice.service.RecognitionService;
import com.itone.it_one_recognitionservice.utils.ImageUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/recognition")
public class RecognitionController {
    private final RecognitionService recognitionService;

    public RecognitionController(RecognitionService recognitionService) {
        this.recognitionService = recognitionService;
    }

    @PostMapping("/recognize/{employeeId}")
    public ResponseEntity<Boolean> recognizeFace(
            @PathVariable Long employeeId,
            @RequestBody String imageUrl
    ) {
        try {
            byte[] imageData = ImageUtils.downloadImageFromURL(imageUrl);  // Скачиваем изображение
            boolean result = recognitionService.recognizeFace(imageData, employeeId);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(false);  // Возвращаем ошибку, если не удалось скачать изображение
        }
    }

    @PostMapping("/update/{employeeId}")
    public ResponseEntity<Void> updateFaceModel(
            @PathVariable Long employeeId,
            @RequestBody String imageUrl
    ) {
        try {
            byte[] imageData = ImageUtils.downloadImageFromURL(imageUrl);  // Скачиваем изображение
            recognitionService.updateEmployeeFaceModel(employeeId, imageData);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build();  // Возвращаем ошибку, если не удалось скачать изображение
        }
    }
}