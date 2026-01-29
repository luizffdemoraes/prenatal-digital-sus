package br.com.hackathon.sus.prenatal_documento.domain.gateways;

import java.io.InputStream;

public interface StorageGateway {
    String upload(String key, InputStream inputStream, String contentType, long contentLength);
    byte[] download(String storagePath);
    void delete(String storagePath);
    boolean exists(String storagePath);
}

