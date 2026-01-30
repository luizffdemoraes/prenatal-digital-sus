package br.com.hackathon.sus.prenatal_documento.infrastructure.gateways;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do S3StorageGateway")
class S3StorageGatewayTest {

    @Mock
    private S3Client s3Client;

    private S3StorageGateway gateway;

    private static final String BUCKET_NAME = "prenatal-bucket";
    private static final String STORAGE_PATH = "prenatal-records/12345678900/exame.pdf";
    private static final String CONTENT_TYPE = "application/pdf";
    private static final byte[] FILE_CONTENT = "PDF content".getBytes();

    @BeforeEach
    void setUp() {
        gateway = new S3StorageGateway(s3Client, BUCKET_NAME);
    }

    @Test
    @DisplayName("Deve fazer upload com sucesso")
    void shouldUploadSuccessfully() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);
        String result = gateway.upload(STORAGE_PATH, inputStream, CONTENT_TYPE, FILE_CONTENT.length);

        assertEquals(STORAGE_PATH, result);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando upload falha")
    void shouldThrowExceptionWhenUploadFails() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("Erro S3"));

        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> gateway.upload(STORAGE_PATH, inputStream, CONTENT_TYPE, FILE_CONTENT.length));

        assertEquals("Erro ao fazer upload do arquivo", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    @Test
    @DisplayName("Deve fazer download com sucesso")
    void shouldDownloadSuccessfully() {
        ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(
                GetObjectResponse.builder().build(),
                FILE_CONTENT
        );
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

        byte[] result = gateway.download(STORAGE_PATH);

        assertArrayEquals(FILE_CONTENT, result);
        verify(s3Client, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando arquivo não existe no download")
    void shouldThrowExceptionWhenFileNotFoundOnDownload() {
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("Not found").build());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> gateway.download(STORAGE_PATH));

        assertEquals("Arquivo não encontrado", ex.getMessage());
        verify(s3Client, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando download falha por outro erro")
    void shouldThrowExceptionWhenDownloadFailsWithOtherError() {
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("Connection error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> gateway.download(STORAGE_PATH));

        assertEquals("Erro ao fazer download do arquivo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve deletar arquivo com sucesso")
    void shouldDeleteSuccessfully() {
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        gateway.delete(STORAGE_PATH);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando deleção falha")
    void shouldThrowExceptionWhenDeleteFails() {
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(new RuntimeException("Erro ao deletar"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> gateway.delete(STORAGE_PATH));

        assertEquals("Erro ao deletar arquivo", ex.getMessage());
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("Deve retornar true quando arquivo existe")
    void shouldReturnTrueWhenFileExists() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());

        boolean result = gateway.exists(STORAGE_PATH);

        assertTrue(result);
        verify(s3Client, times(1)).headObject(any(HeadObjectRequest.class));
    }

    @Test
    @DisplayName("Deve retornar false quando arquivo não existe")
    void shouldReturnFalseWhenFileDoesNotExist() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("Not found").build());

        boolean result = gateway.exists(STORAGE_PATH);

        assertFalse(result);
        verify(s3Client, times(1)).headObject(any(HeadObjectRequest.class));
    }

    @Test
    @DisplayName("Deve retornar false quando headObject falha por outro erro")
    void shouldReturnFalseWhenHeadObjectFailsWithOtherError() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(new RuntimeException("Connection error"));

        boolean result = gateway.exists(STORAGE_PATH);

        assertFalse(result);
    }
}
