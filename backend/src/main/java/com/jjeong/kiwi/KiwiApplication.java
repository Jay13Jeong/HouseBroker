package com.jjeong.kiwi;

//import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableAdminServer
public class KiwiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KiwiApplication.class, args);
	}

}
