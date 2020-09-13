package pl.lodz.p.it.securental;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static pl.lodz.p.it.securental.utils.StringUtils.getInteger;

@AllArgsConstructor
@SpringBootApplication
public class SecurentalApplication {

	private final Environment env;

	public static void main(String[] args) {
		SpringApplication.run(SecurentalApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoderBean() {
		return new BCryptPasswordEncoder(getInteger(env, "PASSWORD_ENCODER_STRENGTH"));
	}
}
