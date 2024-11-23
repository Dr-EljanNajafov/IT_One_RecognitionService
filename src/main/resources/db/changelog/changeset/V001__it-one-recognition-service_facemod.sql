CREATE TABLE IF NOT EXISTS employee_face_models
(
    employee_id     BIGINT PRIMARY KEY,
    face_model_path VARCHAR(255) NOT NULL
);