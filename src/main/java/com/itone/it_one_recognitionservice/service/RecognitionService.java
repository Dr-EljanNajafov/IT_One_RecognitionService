package com.itone.it_one_recognitionservice.service;

public interface RecognitionService {
    boolean recognizeFace(byte[] imageData, Long employeeId);
    void updateEmployeeFaceModel(Long employeeId, byte[] imageData);
}
