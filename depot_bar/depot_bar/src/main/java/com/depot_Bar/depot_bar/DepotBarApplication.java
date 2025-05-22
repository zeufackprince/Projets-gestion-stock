package com.depot_Bar.depot_bar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RequestMapping("/api/test")
@RestController
public class DepotBarApplication {

	public static void main(String[] args) {
		SpringApplication.run(DepotBarApplication.class, args);
	}

	@GetMapping("hello")
	public String test(){

		return "Bonjour";
	}

}
