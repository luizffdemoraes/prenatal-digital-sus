package br.com.hackathon.sus.prenatal_auth.infrastructure.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueValueValidator.class)
@Documented
public @interface UniqueValue {
    //mensagem default que será aplicada
    String message() default "{unique.value.violation}";

    // aplicar validacao apenas para grupos especificos
    Class<?>[] groups() default {};

    // mandar informacao a mais para a validacao...
    Class<? extends Payload>[] payload() default {};

    // aqui vem basicamente o que a anotacao vai precisar para validar
    String fieldName();

    Class<?> domainClass();
}