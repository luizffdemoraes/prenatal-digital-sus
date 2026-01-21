package br.com.hackathon.sus.prenatal_auth.infrastructure.validations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueValueValidator implements ConstraintValidator<UniqueValue, Object> {
    private static final String EXISTENCE_QUERY = "SELECT 1 FROM %s WHERE %s = :value";
    private static final String VALUE_PARAM = "value";

    private String targetField;
    private Class<?> entityClass;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Initializes the validator with UniqueValue annotation parameters.
     *
     * @param annotation contains the validation configuration
     */
    @Override
    public void initialize(UniqueValue annotation) {
        this.targetField = annotation.fieldName();
        this.entityClass = annotation.domainClass();
    }

    /**
     * Validates if the value is unique in the specified entity.
     *
     * @param value   the value to be validated
     * @param context validation context
     * @return true if the value is unique, false otherwise
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String queryString = buildQuery();
        Query query = entityManager.createQuery(queryString)
                .setParameter(VALUE_PARAM, value);

        return query.getResultList().isEmpty();
    }

    /**
     * Builds the JPQL query for uniqueness verification.
     *
     * @return formatted JPQL query string
     */
    private String buildQuery() {
        return String.format(
                EXISTENCE_QUERY,
                entityClass.getName(),
                targetField
        );
    }
}