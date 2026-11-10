package com.example.demo.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseStorageService {

    private final RestClient restClient;

    private final String supabaseUrl;
    private final String bucket;
    private final String serviceRoleKey;

    public SupabaseStorageService(
        @Value("${SUPABASE_URL}") String supabaseUrl,
        @Value("${SUPABASE_BUCKET}") String bucket,
        @Value("${SUPABASE_SERVICE_ROLE_KEY}") String serviceRoleKey) {

    this.restClient = RestClient.create();
    this.supabaseUrl = supabaseUrl.replaceAll("/+$", "");
    this.bucket = bucket;
    this.serviceRoleKey = serviceRoleKey;
}

    // Upload a medical bill and return its storage path
    public String upload(MultipartFile file, Long patientId)
            throws IOException {

        String objectPath = "patient-" + patientId + "/"
                + UUID.randomUUID();

        String contentType = file.getContentType();

        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        restClient.post()
                .uri(supabaseUrl + "/storage/v1/object/"
                        + bucket + "/" + objectPath)
                .header("apikey", serviceRoleKey)
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + serviceRoleKey)
                .header("x-upsert", "false")
                .contentType(MediaType.parseMediaType(contentType))
                .body(file.getBytes())
                .retrieve()
                .toBodilessEntity();

        return objectPath;
    }

    // Download a medical bill from the private bucket
    public StoredFile download(String objectPath) {

        ResponseEntity<byte[]> response = restClient.get()
                .uri(supabaseUrl + "/storage/v1/object/authenticated/"
                        + bucket + "/" + objectPath)
                .header("apikey", serviceRoleKey)
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + serviceRoleKey)
                .retrieve()
                .toEntity(byte[].class);

        MediaType contentType = response.getHeaders().getContentType();

        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return new StoredFile(response.getBody(), contentType);
    }

    // Delete a medical bill from Storage
    public void delete(String objectPath) {

        restClient.delete()
                .uri(supabaseUrl + "/storage/v1/object/"
                        + bucket + "/" + objectPath)
                .header("apikey", serviceRoleKey)
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + serviceRoleKey)
                .retrieve()
                .toBodilessEntity();
    }

    public record StoredFile(
            byte[] content,
            MediaType contentType) {
    }
}