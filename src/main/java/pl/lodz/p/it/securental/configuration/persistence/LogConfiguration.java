package pl.lodz.p.it.securental.configuration.persistence;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pl.lodz.p.it.securental.entities.log.Log;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(basePackages = "pl.lodz.p.it.securental.repositories.log",
        entityManagerFactoryRef = "logEntityManagerFactory",
        transactionManagerRef= "logTransactionManager")
@EnableRetry
@EnableTransactionManagement
public class LogConfiguration {

    public static final String LOG_TRANSACTION_MANAGER = "logTransactionManager";

    @Bean
    @ConfigurationProperties(prefix = "log")
    public DataSource logDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "logEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean logEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> properties = Map.of(
                "hibernate.hbm2ddl.auto", "update"
        );
        return builder
                .dataSource(logDataSource())
                .properties(properties)
                .packages(Log.class)
                .build();
    }

    @Bean(name = "logTransactionManager")
    public PlatformTransactionManager logTransactionManager(@Qualifier("logEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(Objects.requireNonNull(factory.getObject()));
    }
}
