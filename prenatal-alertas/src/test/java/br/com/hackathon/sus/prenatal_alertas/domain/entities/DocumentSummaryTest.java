package br.com.hackathon.sus.prenatal_alertas.domain.entities;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes do DocumentSummary")
class DocumentSummaryTest {

    @Test
    @DisplayName("construtor com todos os campos preenche corretamente")
    void construtorPreencheCampos() {
        LocalDateTime criadoEm = LocalDateTime.of(2025, 2, 1, 10, 0);
        DocumentSummary summary = new DocumentSummary("doc-1", "EXAM", "MORPHOLOGICAL_ULTRASOUND", criadoEm);

        assertEquals("doc-1", summary.getId());
        assertEquals("EXAM", summary.getDocumentType());
        assertEquals("MORPHOLOGICAL_ULTRASOUND", summary.getExamSubType());
        assertEquals(criadoEm, summary.getCreatedAt());
    }

    @Test
    @DisplayName("setters atualizam os valores")
    void settersAtualizamValores() {
        DocumentSummary summary = new DocumentSummary();
        LocalDateTime criadoEm = LocalDateTime.now();

        summary.setId("doc-2");
        summary.setDocumentType("ULTRASOUND");
        summary.setExamSubType("NUCHAL_TRANSLUCENCY");
        summary.setCreatedAt(criadoEm);

        assertEquals("doc-2", summary.getId());
        assertEquals("ULTRASOUND", summary.getDocumentType());
        assertEquals("NUCHAL_TRANSLUCENCY", summary.getExamSubType());
        assertEquals(criadoEm, summary.getCreatedAt());
    }

    @Test
    @DisplayName("construtor vazio permite instanciação")
    void construtorVazioPermiteInstanciacao() {
        DocumentSummary summary = new DocumentSummary();
        assertNotNull(summary);
        assertNull(summary.getId());
        assertNull(summary.getDocumentType());
        assertNull(summary.getExamSubType());
        assertNull(summary.getCreatedAt());
    }
}
