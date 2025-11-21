package cz.netsquire.kgcore;

import org.springframework.boot.SpringApplication;

public class TestKgcoreApplication {

	public static void main(String[] args) {
		SpringApplication.from(KgcoreApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
