package com.hackathon.sus.prenatal_prontuario.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MedicalRecordTest {

    @Test
    @DisplayName("Deve criar prontuário a partir da primeira consulta")
    void shouldCreateRecordFromFirstAppointment() {
        // Arrange
        String cpf = "12345678901";
        LocalDate lastMenstrualPeriod = LocalDate.now().minusWeeks(20);

        // Act
        MedicalRecord record = MedicalRecord.fromFirstAppointment(
                cpf,
                "Maria Silva",
                LocalDate.of(1990, 1, 1),
                lastMenstrualPeriod,
                PregnancyType.SINGLETON,
                1,
                1,
                0,
                false,
                null,
                List.of(RiskFactor.HYPERTENSION),
                true,
                false,
                "Observações",
                DeliveryType.NATURAL,
                null
        );

        // Assert
        assertNotNull(record);
        assertEquals(cpf, record.getCpf());
        assertEquals("Maria Silva", record.getFullName());
        assertEquals(lastMenstrualPeriod, record.getLastMenstrualPeriod());
        assertEquals(20, record.getGestationalAgeWeeks());
        assertEquals(PregnancyType.SINGLETON, record.getPregnancyType());
        assertEquals(1, record.getPreviousPregnancies());
        assertEquals(1, record.getPreviousDeliveries());
        assertEquals(0, record.getPreviousAbortions());
        assertEquals(false, record.getHighRiskPregnancy());
        assertEquals(1, record.getRiskFactors().size());
        assertTrue(record.getRiskFactors().contains(RiskFactor.HYPERTENSION));
        assertEquals(true, record.getVitaminUse());
        assertEquals(false, record.getAspirinUse());
        assertEquals("Observações", record.getNotes());
        assertEquals(DeliveryType.NATURAL, record.getDeliveryType());
        assertNotNull(record.getCreatedAt());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF é nulo")
    void shouldThrowExceptionWhenCpfIsNull() {
        // Arrange
        LocalDate lastMenstrualPeriod = LocalDate.now().minusWeeks(20);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> MedicalRecord.fromFirstAppointment(
                    null,
                    "Maria Silva",
                    LocalDate.of(1990, 1, 1),
                    lastMenstrualPeriod,
                    PregnancyType.SINGLETON,
                    1,
                    1,
                    0,
                    false,
                    null,
                    null,
                    false,
                    false,
                    null,
                    null,
                    null
            ));

        assertEquals("CPF é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF é vazio")
    void shouldThrowExceptionWhenCpfIsEmpty() {
        // Arrange
        LocalDate lastMenstrualPeriod = LocalDate.now().minusWeeks(20);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> MedicalRecord.fromFirstAppointment(
                    "",
                    "Maria Silva",
                    LocalDate.of(1990, 1, 1),
                    lastMenstrualPeriod,
                    PregnancyType.SINGLETON,
                    1,
                    1,
                    0,
                    false,
                    null,
                    null,
                    false,
                    false,
                    null,
                    null,
                    null
            ));

        assertEquals("CPF é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando data da última menstruação é nula")
    void shouldThrowExceptionWhenLastMenstrualPeriodIsNull() {
        // Arrange
        String cpf = "12345678901";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> MedicalRecord.fromFirstAppointment(
                    cpf,
                    "Maria Silva",
                    LocalDate.of(1990, 1, 1),
                    null,
                    PregnancyType.SINGLETON,
                    1,
                    1,
                    0,
                    false,
                    null,
                    null,
                    false,
                    false,
                    null,
                    null,
                    null
            ));

        assertEquals("dataUltimaMenstruacao é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando idade gestacional é menor que 1")
    void shouldThrowExceptionWhenGestationalAgeIsLessThan1() {
        // Arrange
        String cpf = "12345678901";
        LocalDate lastMenstrualPeriod = LocalDate.now().minusWeeks(50);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> MedicalRecord.fromFirstAppointment(
                    cpf,
                    "Maria Silva",
                    LocalDate.of(1990, 1, 1),
                    lastMenstrualPeriod,
                    PregnancyType.SINGLETON,
                    1,
                    1,
                    0,
                    false,
                    null,
                    null,
                    false,
                    false,
                    null,
                    null,
                    null
            ));

        assertTrue(exception.getMessage().contains("Idade gestacional calculada"));
        assertTrue(exception.getMessage().contains("Deve ser entre 1 e 44"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando idade gestacional é maior que 44")
    void shouldThrowExceptionWhenGestationalAgeIsGreaterThan44() {
        // Arrange
        String cpf = "12345678901";
        LocalDate lastMenstrualPeriod = LocalDate.now().minusDays(5);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> MedicalRecord.fromFirstAppointment(
                    cpf,
                    "Maria Silva",
                    LocalDate.of(1990, 1, 1),
                    lastMenstrualPeriod,
                    PregnancyType.SINGLETON,
                    1,
                    1,
                    0,
                    false,
                    null,
                    null,
                    false,
                    false,
                    null,
                    null,
                    null
            ));

        assertTrue(exception.getMessage().contains("Idade gestacional calculada"));
        assertTrue(exception.getMessage().contains("Deve ser entre 1 e 44"));
    }

    @Test
    @DisplayName("Deve usar valores padrão quando parâmetros opcionais são nulos")
    void shouldUseDefaultValuesWhenOptionalParamsAreNull() {
        // Arrange
        String cpf = "12345678901";
        LocalDate lastMenstrualPeriod = LocalDate.now().minusWeeks(20);

        // Act
        MedicalRecord record = MedicalRecord.fromFirstAppointment(
                cpf,
                "Maria Silva",
                LocalDate.of(1990, 1, 1),
                lastMenstrualPeriod,
                null, // pregnancyType
                null, // previousPregnancies
                null, // previousDeliveries
                null, // previousAbortions
                null, // highRiskPregnancy
                null,
                null, // riskFactors
                null, // vitaminUse
                null, // aspirinUse
                null,
                null, // deliveryType
                null
        );

        // Assert
        assertEquals(PregnancyType.SINGLETON, record.getPregnancyType());
        assertEquals(0, record.getPreviousPregnancies());
        assertEquals(0, record.getPreviousDeliveries());
        assertEquals(0, record.getPreviousAbortions());
        assertEquals(false, record.getHighRiskPregnancy());
        assertTrue(record.getRiskFactors().isEmpty());
        assertEquals(false, record.getVitaminUse());
        assertEquals(false, record.getAspirinUse());
    }

    @Test
    @DisplayName("Deve atualizar dados clínicos")
    void shouldUpdateClinicalData() {
        // Arrange
        MedicalRecord record = new MedicalRecord(
                UUID.randomUUID(),
                "12345678901",
                "Maria Silva",
                LocalDate.of(1990, 1, 1),
                null,
                null,
                LocalDate.now().minusWeeks(20),
                20,
                PregnancyType.SINGLETON,
                1,
                1,
                0,
                false,
                null,
                null,
                false,
                false,
                "Observações antigas",
                DeliveryType.NATURAL,
                null
        );

        // Act
        record.updateClinicalData(true, true, "Observações novas", DeliveryType.CESAREAN);

        // Assert
        assertEquals(true, record.getVitaminUse());
        assertEquals(true, record.getAspirinUse());
        assertEquals("Observações novas", record.getNotes());
        assertEquals(DeliveryType.CESAREAN, record.getDeliveryType());
    }

    @Test
    @DisplayName("Deve atualizar apenas campos informados")
    void shouldUpdateOnlyInformedFields() {
        // Arrange
        MedicalRecord record = new MedicalRecord(
                UUID.randomUUID(),
                "12345678901",
                "Maria Silva",
                LocalDate.of(1990, 1, 1),
                null,
                null,
                LocalDate.now().minusWeeks(20),
                20,
                PregnancyType.SINGLETON,
                1,
                1,
                0,
                false,
                null,
                null,
                false,
                false,
                "Observações antigas",
                DeliveryType.NATURAL,
                null
        );

        // Act
        record.updateClinicalData(true, null, null, null);

        // Assert
        assertEquals(true, record.getVitaminUse());
        assertEquals(false, record.getAspirinUse()); // mantém valor anterior
        assertEquals("Observações antigas", record.getNotes()); // mantém valor anterior
        assertEquals(DeliveryType.NATURAL, record.getDeliveryType()); // mantém valor anterior
    }

    @Test
    void deveAtualizarFatoresDeRisco() {
        // Arrange
        MedicalRecord record = new MedicalRecord(
                UUID.randomUUID(),
                "12345678901",
                "Maria Silva",
                LocalDate.of(1990, 1, 1),
                null,
                null,
                LocalDate.now().minusWeeks(20),
                20,
                PregnancyType.SINGLETON,
                1,
                1,
                0,
                false,
                null,
                List.of(RiskFactor.HYPERTENSION),
                false,
                false,
                "Observações",
                DeliveryType.NATURAL,
                null
        );

        // Act
        record.updateRiskFactors(List.of(RiskFactor.GESTATIONAL_DIABETES, RiskFactor.OBESITY));

        // Assert
        assertEquals(2, record.getRiskFactors().size());
        assertTrue(record.getRiskFactors().contains(RiskFactor.GESTATIONAL_DIABETES));
        assertTrue(record.getRiskFactors().contains(RiskFactor.OBESITY));
        assertFalse(record.getRiskFactors().contains(RiskFactor.HYPERTENSION));
    }

    @Test
    @DisplayName("Deve limpar fatores de risco quando lista vazia")
    void shouldClearRiskFactorsWhenListIsEmpty() {
        // Arrange
        MedicalRecord record = new MedicalRecord(
                UUID.randomUUID(),
                "12345678901",
                "Maria Silva",
                LocalDate.of(1990, 1, 1),
                null,
                null,
                LocalDate.now().minusWeeks(20),
                20,
                PregnancyType.SINGLETON,
                1,
                1,
                0,
                false,
                null,
                List.of(RiskFactor.HYPERTENSION),
                false,
                false,
                "Observações",
                DeliveryType.NATURAL,
                null
        );

        // Act
        record.updateRiskFactors(List.of());

        // Assert
        assertTrue(record.getRiskFactors().isEmpty());
    }

    @Test
    @DisplayName("Deve retornar cópia imutável dos fatores de risco")
    void shouldReturnImmutableCopyOfRiskFactors() {
        // Arrange
        MedicalRecord record = new MedicalRecord(
                UUID.randomUUID(),
                "12345678901",
                "Maria Silva",
                LocalDate.of(1990, 1, 1),
                null,
                null,
                LocalDate.now().minusWeeks(20),
                20,
                PregnancyType.SINGLETON,
                1,
                1,
                0,
                false,
                null,
                List.of(RiskFactor.HYPERTENSION),
                false,
                false,
                "Observações",
                DeliveryType.NATURAL,
                null
        );

        // Act
        List<RiskFactor> factors = record.getRiskFactors();
        
        // Assert
        assertThrows(UnsupportedOperationException.class, () -> factors.add(RiskFactor.OBESITY));
    }

    @Test
    @DisplayName("Deve usar data de referência quando informada")
    void shouldUseReferenceDateWhenProvided() {
        // Arrange
        String cpf = "12345678901";
        LocalDate referenceDate = LocalDate.of(2024, 6, 1);
        LocalDate lastMenstrualPeriod = referenceDate.minusWeeks(20);

        // Act
        MedicalRecord record = MedicalRecord.fromFirstAppointment(
                cpf,
                "Maria Silva",
                LocalDate.of(1990, 1, 1),
                lastMenstrualPeriod,
                PregnancyType.SINGLETON,
                1,
                1,
                0,
                false,
                null,
                null,
                false,
                false,
                null,
                null,
                referenceDate
        );

        // Assert
        assertEquals(20, record.getGestationalAgeWeeks());
    }

    @Test
    @DisplayName("Deve lançar exceção quando idade gestacional é inválida no construtor")
    void shouldThrowExceptionWhenGestationalAgeIsInvalidInConstructor() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new MedicalRecord(
                    id,
                    "12345678901",
                    "Maria Silva",
                    LocalDate.of(1990, 1, 1),
                    null,
                    null,
                    LocalDate.now().minusWeeks(20),
                    50, // idade gestacional inválida
                    PregnancyType.SINGLETON,
                    1,
                    1,
                    0,
                    false,
                    null,
                    null,
                    false,
                    false,
                    null,
                    null,
                    null
            ));

        assertEquals("Idade gestacional em semanas deve ser entre 1 e 44", exception.getMessage());
    }
}
