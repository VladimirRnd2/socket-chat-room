package com.example.socketchatroom.repository;

import com.example.socketchatroom.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
