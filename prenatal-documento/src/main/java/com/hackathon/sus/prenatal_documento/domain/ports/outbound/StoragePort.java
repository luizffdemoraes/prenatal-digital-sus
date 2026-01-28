package com.hackathon.sus.prenatal_documento.domain.ports.outbound;

import java.io.InputStream;

public interface StoragePort {
    String upload(String key, InputStream inputStream, String contentType, long contentLength);
    byte[] download(String storagePath);
    void delete(String storagePath);
    boolean exists(String storagePath);
}
