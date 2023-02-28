package com.example.socketchatroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SocketChatRoomApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketChatRoomApplication.class, args);
    }

}
