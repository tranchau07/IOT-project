package com.example.Iot_Project;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("com.example.Iot_Project.repository.jpa")
@EnableMongoRepositories("com.example.Iot_Project.repository.mongo")
@OpenAPIDefinition(
    info = @Info(
        title = "IoT Project API",
        version = "1.0.0",
        description = "Tài liệu API cho dự án IoT quản lý phòng học."
    )
)
public class IotProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotProjectApplication.class, args);
	}

}
