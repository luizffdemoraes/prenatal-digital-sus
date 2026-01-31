package br.com.hackathon.sus.prenatal_ia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrenatalIaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrenatalIaApplication.class, args);
    }
}
