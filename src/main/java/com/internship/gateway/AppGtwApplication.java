package com.internship.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

//@EnableDiscoveryClient
@EnableEurekaClient
@SpringBootApplication(scanBasePackages = "org.springframework.security.oauth2.jwt")
public class AppGtwApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppGtwApplication.class, args);
	}

}
