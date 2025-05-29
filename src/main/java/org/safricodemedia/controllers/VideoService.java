package org.safricodemedia.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VideoService {
    // Chemin relatif au projet (dev) ou au JAR (prod)
    private Path getVideoPath() {
        return Paths.get(System.getProperty("user.dir"), "videos");
    }

    public InputStream readFromJar(String filename) throws IOException {
        // Pour les fichiers dans le JAR
        return getClass().getResourceAsStream("/videos/" + filename);
    }

    public Path writeExternalFile(String filename, byte[] content) throws IOException {
        Path dir = getVideoPath();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        Path file = dir.resolve(filename);
        return Files.write(file, content);
    }
}
