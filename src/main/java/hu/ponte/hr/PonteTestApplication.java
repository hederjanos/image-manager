package hu.ponte.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PonteTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PonteTestApplication.class, args);
    }

}

