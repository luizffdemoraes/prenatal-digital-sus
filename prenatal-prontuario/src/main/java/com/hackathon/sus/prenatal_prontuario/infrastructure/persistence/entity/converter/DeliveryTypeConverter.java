package com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.converter;

import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class DeliveryTypeConverter implements AttributeConverter<DeliveryType, String> {

    @Override
    public String convertToDatabaseColumn(DeliveryType attr) {
        return attr == null ? null : attr.toDbValue();
    }

    @Override
    public DeliveryType convertToEntityAttribute(String db) {
        return db == null ? null : DeliveryType.fromDbValue(db);
    }
}
