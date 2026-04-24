package com.library.fine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibraryFineApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryFineApplication.class, args);
    }
}
