package com.example.cabbooking;

import com.example.cabbooking.config.DispatchProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DispatchProperties.class)
public class CabBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CabBookingApplication.class, args);
    }
}
