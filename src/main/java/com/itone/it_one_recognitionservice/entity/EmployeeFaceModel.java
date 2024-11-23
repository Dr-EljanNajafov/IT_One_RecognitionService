package com.itone.it_one_recognitionservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee_face_models")
public class EmployeeFaceModel {
    @Id
    private Long employeeId;

    @Column(name = "face_model_path", nullable = false)
    private String faceModelPath;
}