package com.jjeong.kiwi;

//import com.jjeong.kiwi.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.jjeong.kiwi.repository")
@EntityScan("com.jjeong.kiwi.domain")
//@EnableJpaRepositories
//@Import(JpaConfig.class)
public class KiwiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KiwiApplication.class, args);
	}

}