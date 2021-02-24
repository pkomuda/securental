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
@EnableJpaRepositories(basePackages = "pl.lodz.p.it.securental.repositories",
        entityManagerFactoryRef = "mokEntityManagerFactory",
        transactionManagerRef= "mokTransactionManager")
@EnableRetry
@EnableTransactionManagement
public class MokConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "mok")
    public DataSource mokDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "mokEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> properties = Map.of(
            "hibernate.hbm2ddl.auto", "update"
        );
        return builder
                .dataSource(mokDataSource())
                .properties(properties)
                .packages("pl.lodz.p.it.securental.entities")
                .build();
    }

    @Primary
    @Bean(name = "mokTransactionManager")
    public PlatformTransactionManager usersTransactionManager(@Qualifier("mokEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(Objects.requireNonNull(factory.getObject()));
    }
}
