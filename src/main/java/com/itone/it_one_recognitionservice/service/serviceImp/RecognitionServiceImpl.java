package com.itone.it_one_recognitionservice.service.serviceImp;


import com.itone.it_one_recognitionservice.entity.EmployeeFaceModel;
import com.itone.it_one_recognitionservice.repository.EmployeeFaceModelRepository;
import com.itone.it_one_recognitionservice.service.RecognitionService;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;

@Service
public class RecognitionServiceImpl implements RecognitionService {
    private final EmployeeFaceModelRepository faceModelRepository;
    private final FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
    private static final Logger LOGGER = LoggerFactory.getLogger(RecognitionServiceImpl.class);

    public RecognitionServiceImpl(EmployeeFaceModelRepository faceModelRepository) {
        this.faceModelRepository = faceModelRepository;
    }

    @Override
    public boolean recognizeFace(byte[] imageData, Long employeeId) {
        LOGGER.info("Attempting to recognize face for employee ID: {}", employeeId);

        // Загружаем изображение из данных
        Mat testImage = opencv_imgcodecs.imdecode(new Mat(imageData), opencv_imgcodecs.IMREAD_GRAYSCALE);
        if (testImage.empty()) {
            throw new IllegalArgumentException("Invalid image data provided.");
        }

        // Приводим изображение к стандартному размеру
        Mat resizedImage = new Mat();
        opencv_imgproc.resize(testImage, resizedImage, new org.bytedeco.opencv.opencv_core.Size(150, 150));

        // Проверяем наличие обученной модели для сотрудника
        Optional<EmployeeFaceModel> faceModelOpt = faceModelRepository.findById(employeeId);
        if (faceModelOpt.isEmpty()) {
            LOGGER.warn("No face model found for employee ID: {}", employeeId);
            return false;
        }

        EmployeeFaceModel faceModel = faceModelOpt.get();
        faceRecognizer.read(faceModel.getFaceModelPath());

        // Сравниваем лицо
        int[] label = new int[1];
        double[] confidence = new double[1];
        faceRecognizer.predict(resizedImage, label, confidence);

        boolean isRecognized = label[0] == employeeId.intValue() && confidence[0] < 80.0; // Чем меньше, тем лучше
        LOGGER.info("Face recognition result: {} (Confidence: {})", isRecognized, confidence[0]);
        return isRecognized;
    }

    @Override
    public void updateEmployeeFaceModel(Long employeeId, byte[] imageData) {
        LOGGER.info("Updating face model for employee ID: {}", employeeId);

        Mat trainImage = opencv_imgcodecs.imdecode(new Mat(imageData), opencv_imgcodecs.IMREAD_GRAYSCALE);
        if (trainImage.empty()) {
            throw new IllegalArgumentException("Invalid training image data.");
        }

        Mat resizedImage = new Mat();
        opencv_imgproc.resize(trainImage, resizedImage, new org.bytedeco.opencv.opencv_core.Size(150, 150));

        MatVector trainingImages = new MatVector(1);
        trainingImages.put(0, resizedImage);

        Mat labels = new Mat(1, 1, CV_32SC1);
        labels.ptr(0).putInt(employeeId.intValue());

        faceRecognizer.train(trainingImages, labels);

        String modelDirectory = "models";
        File directory = new File(modelDirectory);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                LOGGER.info("Model directory created: {}", modelDirectory);
            } else {
                LOGGER.error("Failed to create model directory: {}", modelDirectory);
                throw new RuntimeException("Failed to create model directory.");
            }
        }

        String modelPath = modelDirectory + "/employee_" + employeeId + ".xml";
        faceRecognizer.write(modelPath);

        EmployeeFaceModel faceModel = new EmployeeFaceModel(employeeId, modelPath);
        faceModelRepository.save(faceModel);
        LOGGER.info("Face model for employee ID {} updated successfully.", employeeId);
    }
}
