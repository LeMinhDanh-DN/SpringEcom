package com.example.springecom.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map<String, String> uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return Map.of();
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "ecom_products",
                        "resource_type", "auto"));

        String secureUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        return Map.of(
                "url", secureUrl != null ? secureUrl : "",
                "publicId", publicId != null ? publicId : "");
    }

    public void deleteImage(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return;
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            System.err.println("Failed to delete image from Cloudinary (" + publicId + "): " + e.getMessage());
        }
    }

    public Map<String, String> uploadImageFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return Map.of();
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    imageUrl,
                    ObjectUtils.asMap(
                            "folder", "ecom_products",
                            "resource_type", "auto"));

            String secureUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            return Map.of(
                    "url", secureUrl != null ? secureUrl : "",
                    "publicId", publicId != null ? publicId : "");
        } catch (Exception e) {
            System.err.println("Failed to upload image from URL (" + imageUrl + "): " + e.getMessage());
            return Map.of("url", imageUrl, "publicId", "");
        }
    }
}
