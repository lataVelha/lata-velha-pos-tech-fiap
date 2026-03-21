package br.com.lata.velha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class LataVelhaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LataVelhaApplication.class, args);

	}

}
