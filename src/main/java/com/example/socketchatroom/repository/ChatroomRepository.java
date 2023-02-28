package com.example.socketchatroom.repository;

import com.example.socketchatroom.model.Chatroom;
import com.example.socketchatroom.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    Optional<Chatroom> findByChatroomName(String userName);

    Optional<Chatroom> findByChatroomNameAndStatus(String chatroomName, Status active);
}
