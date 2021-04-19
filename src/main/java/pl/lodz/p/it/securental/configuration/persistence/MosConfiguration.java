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
import pl.lodz.p.it.securental.entities.mok.Client;
import pl.lodz.p.it.securental.entities.mor.Reservation;
import pl.lodz.p.it.securental.entities.mos.Car;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(basePackages = "pl.lodz.p.it.securental.repositories.mos",
        entityManagerFactoryRef = "mosEntityManagerFactory",
        transactionManagerRef= "mosTransactionManager")
@EnableRetry
@EnableTransactionManagement
public class MosConfiguration {

    public static final String MOS_TRANSACTION_MANAGER = "mosTransactionManager";

    @Bean
    @ConfigurationProperties(prefix = "mos")
    public DataSource mosDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mosEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> properties = Map.of(
                "hibernate.hbm2ddl.auto", "update"
        );
        return builder
                .dataSource(mosDataSource())
                .properties(properties)
                .packages(Car.class, Reservation.class, Client.class)
                .build();
    }

    @Bean(name = "mosTransactionManager")
    public PlatformTransactionManager mosTransactionManager(@Qualifier("mosEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(Objects.requireNonNull(factory.getObject()));
    }
}
