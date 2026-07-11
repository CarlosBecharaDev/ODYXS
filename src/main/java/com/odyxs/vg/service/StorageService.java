package com.odyxs.vg.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class StorageService {

    /** Solo se aceptan estos tipos de imagen. */
    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    @Value("${odyxs.upload.dir:${user.home}/odyxs-uploads}")
    private String uploadBaseDir;

    public String guardarImagen(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) return null;

        String contentType = file.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType.toLowerCase())) return null;

        try {
            Path directory = Paths.get(uploadBaseDir, subDir);
            Files.createDirectories(directory);

            // La extensión se deriva del tipo de contenido validado, nunca del
            // nombre original del archivo (evita guardar cosas como "evil.jsp").
            String extension = extensionPara(contentType);
            String fileName = UUID.randomUUID().toString() + extension;
            Path targetLocation = directory.resolve(fileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subDir + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo", e);
        }
    }

    public void borrarImagen(String url) {
        if (url == null || url.isBlank() || !url.startsWith("/uploads/")) return;

        try {
            String relativePath = url.replace("/uploads/", "");
            Path filePath = Paths.get(uploadBaseDir, relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error but don't stop execution
            System.err.println("Error al borrar archivo: " + url);
        }
    }

    private String extensionPara(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/png"  -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif"  -> ".gif";
            default            -> ".jpg";
        };
    }
}
