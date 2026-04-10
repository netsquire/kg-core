package cz.netsquire.kgcore;

import cz.netsquire.kgcore.util.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class KgcoreApplication {

	public static void main(String[] args) {
		try {
			DotenvLoader.load(new File(".env"));
		} catch (Exception ignore) {}

		SpringApplication.run(KgcoreApplication.class, args);
	}

}
