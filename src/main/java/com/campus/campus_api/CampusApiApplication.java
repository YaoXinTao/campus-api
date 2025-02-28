package com.campus.campus_api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(basePackages = {"com.campus.api"})
@MapperScan("com.campus.api.mapper")
public class CampusApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusApiApplication.class, args);
	}

}
