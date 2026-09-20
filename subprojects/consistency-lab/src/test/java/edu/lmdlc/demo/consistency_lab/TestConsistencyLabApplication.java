package edu.lmdlc.demo.consistency_lab;

import org.springframework.boot.SpringApplication;

public class TestConsistencyLabApplication {

	public static void main(String[] args) {
		SpringApplication.from(ConsistencyLabApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
