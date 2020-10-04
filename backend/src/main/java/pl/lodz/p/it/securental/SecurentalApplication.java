package pl.lodz.p.it.securental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.PASSWORD_ENCODER_STRENGTH;

@SpringBootApplication
public class SecurentalApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurentalApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoderBean() {
		return new BCryptPasswordEncoder(PASSWORD_ENCODER_STRENGTH);
	}
}
