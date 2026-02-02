package br.com.hackathon.sus.prenatal_alertas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrenatalAlertasApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrenatalAlertasApplication.class, args);
    }
}
