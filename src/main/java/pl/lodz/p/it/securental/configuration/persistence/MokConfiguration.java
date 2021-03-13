package pl.lodz.p.it.securental.configuration.persistence;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "pl.lodz.p.it.securental.repositories.mok",
        entityManagerFactoryRef = "mokEntityManagerFactory",
        transactionManagerRef = MokConfiguration.MOK_TRANSACTION_MANAGER)
@EnableRetry
@EnableTransactionManagement
public class MokConfiguration {

    public static final String MOK_TRANSACTION_MANAGER = "mokTransactionManager";

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "mok")
    public DataSource mokDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "mokEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mokEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(mokDataSource())
                .packages("pl.lodz.p.it.securental.entities.mok")
                .build();
    }

    @Primary
    @Bean(name = MOK_TRANSACTION_MANAGER)
    public PlatformTransactionManager mokTransactionManager(@Qualifier("mokEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(Objects.requireNonNull(factory.getObject()));
    }
}
