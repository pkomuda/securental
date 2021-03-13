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
import pl.lodz.p.it.securental.entities.mod.Log;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(basePackages = "pl.lodz.p.it.securental.repositories.mod",
        entityManagerFactoryRef = "modEntityManagerFactory",
        transactionManagerRef= "modTransactionManager")
@EnableRetry
@EnableTransactionManagement
public class ModConfiguration {

    public static final String MOD_TRANSACTION_MANAGER = "modTransactionManager";

    @Bean
    @ConfigurationProperties(prefix = "mod")
    public DataSource modDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "modEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean modEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> properties = Map.of(
                "hibernate.hbm2ddl.auto", "update"
        );
        return builder
                .dataSource(modDataSource())
                .properties(properties)
                .packages(Log.class)
                .build();
    }

    @Bean(name = "modTransactionManager")
    public PlatformTransactionManager modTransactionManager(@Qualifier("modEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(Objects.requireNonNull(factory.getObject()));
    }
}
