package com.itone.it_one_recognitionservice.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.springframework.web.client.RestTemplate;

public class ImageUtils {

    // Метод для скачивания изображения по URL и преобразования его в byte[]
    public static byte[] downloadImageFromURL(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        try (InputStream inputStream = url.openStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    // Опционально: можно использовать RestTemplate для скачивания
    public static byte[] downloadImageWithRestTemplate(String imageUrl) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(imageUrl, byte[].class);
    }
}
