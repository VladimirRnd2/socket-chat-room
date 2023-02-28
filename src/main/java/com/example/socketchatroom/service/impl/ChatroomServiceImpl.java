package com.example.socketchatroom.service.impl;

import com.example.socketchatroom.model.Chatroom;
import com.example.socketchatroom.model.Status;
import com.example.socketchatroom.repository.ActivePersonRepository;
import com.example.socketchatroom.repository.ChatroomRepository;
import com.example.socketchatroom.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatroomServiceImpl implements ChatroomService {
    private final ChatroomRepository chatroomRepository;
    private final ActivePersonRepository activePersonRepository;

    @Override
    public Chatroom createNewChatroom(String userName) {
        Chatroom chatroom = new Chatroom();
        chatroom.setChatroomName(UUID.randomUUID().toString());
        chatroom.setCreatorName(userName);
        chatroom.setDateCreate(new Date());
        chatroom.setStatus(Status.ACTIVE);
        return chatroomRepository.save(chatroom);
    }

    @Override
    public List<String> getActiveUsersInChatroom(String chatroom) {
        return activePersonRepository.getAllActiveUsersInChatroom(chatroom);
    }

    @Override
    public void deleteChatroomByName(String chatroomName) {
        Optional<Chatroom> byChatroomName = chatroomRepository.findByChatroomName(chatroomName);
        if (byChatroomName.isPresent()) {
            Chatroom chatroom = byChatroomName.get();
            chatroom.setStatus(Status.NOT_ACTIVE);
            chatroomRepository.save(chatroom);
        } else {
            throw new RuntimeException("Такая комната не существует и не существовала");
        }
    }

    @Override
    public Chatroom getChatroomByName(String chatroomName) {
        return chatroomRepository.findByChatroomNameAndStatus(chatroomName, Status.ACTIVE).orElseThrow(() -> new EntityNotFoundException("Комнаты с именем " + chatroomName + " не существует"));
    }
}
