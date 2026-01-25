package com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.converter;

import com.hackathon.sus.prenatal_prontuario.domain.entities.PregnancyType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PregnancyTypeConverter implements AttributeConverter<PregnancyType, String> {

    @Override
    public String convertToDatabaseColumn(PregnancyType attr) {
        return attr == null ? null : attr.toDbValue();
    }

    @Override
    public PregnancyType convertToEntityAttribute(String db) {
        return db == null ? null : PregnancyType.fromDbValue(db);
    }
}
