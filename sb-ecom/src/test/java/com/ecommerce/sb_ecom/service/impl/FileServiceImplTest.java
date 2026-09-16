package com.ecommerce.sb_ecom.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileServiceImplTest {

    private final FileServiceImpl fileService = new FileServiceImpl();

    @TempDir
    Path tempDir;

    @Test
    void shouldUploadImageWithSuccess() throws IOException {
        String content = "conteudo fake da image";

        MockMultipartFile file = new MockMultipartFile("file", "imagem.png", "image/png", content.getBytes());

        String filename = fileService.uploadImage(tempDir.toString(), file);

        assertNotNull(filename);
        assertTrue(filename.endsWith(".png"));

        Path savedFilePath = tempDir.resolve(filename);

        assertTrue(Files.exists(savedFilePath));
        assertEquals(content, Files.readString(savedFilePath));
    }


    @Test
    void shouldCreateFolderWhenItDoesNotExist() throws IOException {
        Path uploadPath = tempDir.resolve("uploads");

        assertFalse(Files.exists(uploadPath));

        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("imagem.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("conteudo-fake".getBytes()));

        String filename = fileService.uploadImage(uploadPath.toString(), file);

        assertTrue(Files.exists(uploadPath));
        assertTrue(Files.isDirectory(uploadPath));

        Path savedFile = uploadPath.resolve(filename);

        assertTrue(Files.exists(savedFile));
        assertTrue(filename.endsWith(".png"));
    }

}