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
import pl.lodz.p.it.securental.entities.mop.Car;
import pl.lodz.p.it.securental.entities.mor.Reservation;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(basePackages = "pl.lodz.p.it.securental.repositories.mor",
        entityManagerFactoryRef = "morEntityManagerFactory",
        transactionManagerRef= "morTransactionManager")
@EnableRetry
@EnableTransactionManagement
public class MorConfiguration {

    public static final String MOR_TRANSACTION_MANAGER = "morTransactionManager";

    @Bean
    @ConfigurationProperties(prefix = "mor")
    public DataSource morDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "morEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean morEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> properties = Map.of(
                "hibernate.hbm2ddl.auto", "update"
        );
        return builder
                .dataSource(morDataSource())
                .properties(properties)
                .packages(Reservation.class, Car.class, Client.class)
                .build();
    }

    @Bean(name = "morTransactionManager")
    public PlatformTransactionManager morTransactionManager(@Qualifier("morEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(Objects.requireNonNull(factory.getObject()));
    }
}
