package com.hackathon.sus.prenatal_prontuario.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
public class FlywayBeforeJpaConfig implements BeanFactoryPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(FlywayBeforeJpaConfig.class);
    private static final String FLYWAY_RUNNER = "flywayRunner";
    private static final String ENTITY_MANAGER_FACTORY = "entityManagerFactory";

    @Bean(name = FLYWAY_RUNNER)
    @Profile("!test")
    public FlywayRunner flywayRunner(
            DataSource dataSource,
            @Value("${spring.flyway.locations:classpath:db.migration}") String locations,
            @Value("${spring.flyway.schemas:prontuario}") String schemas,
            @Value("${spring.flyway.baseline-on-migrate:true}") boolean baselineOnMigrate,
            @Value("${spring.flyway.baseline-version:0}") String baselineVersion,
            @Value("${spring.flyway.validate-on-migrate:true}") boolean validateOnMigrate
    ) {
        return new FlywayRunner(dataSource, locations, schemas, baselineOnMigrate, baselineVersion, validateOnMigrate);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (!beanFactory.containsBeanDefinition(FLYWAY_RUNNER)) return;
        if (!beanFactory.containsBeanDefinition(ENTITY_MANAGER_FACTORY)) return;
        BeanDefinition emf = beanFactory.getBeanDefinition(ENTITY_MANAGER_FACTORY);
        String[] existing = emf.getDependsOn();
        if (existing != null && Arrays.asList(existing).contains(FLYWAY_RUNNER)) return;
        String[] updated = existing == null ? new String[]{FLYWAY_RUNNER} : Arrays.copyOf(existing, existing.length + 1);
        updated[updated.length - 1] = FLYWAY_RUNNER;
        emf.setDependsOn(updated);
    }

    public static final class FlywayRunner implements InitializingBean {
        private final DataSource dataSource;
        private final String locations;
        private final String schemas;
        private final boolean baselineOnMigrate;
        private final String baselineVersion;
        private final boolean validateOnMigrate;

        FlywayRunner(DataSource dataSource, String locations, String schemas,
                     boolean baselineOnMigrate, String baselineVersion, boolean validateOnMigrate) {
            this.dataSource = dataSource;
            this.locations = locations;
            this.schemas = schemas != null ? schemas : "prontuario";
            this.baselineOnMigrate = baselineOnMigrate;
            this.baselineVersion = baselineVersion;
            this.validateOnMigrate = validateOnMigrate;
        }

        @Override
        public void afterPropertiesSet() {
            log.info("Executando migrações Flyway (prontuario) antes do JPA...");
            Flyway.configure()
                    .dataSource(dataSource)
                    .locations(locations)
                    .schemas(schemas.trim().split(",\\s*"))
                    .baselineOnMigrate(baselineOnMigrate)
                    .baselineVersion(baselineVersion)
                    .validateOnMigrate(validateOnMigrate)
                    .load()
                    .migrate();
        }
    }
}
