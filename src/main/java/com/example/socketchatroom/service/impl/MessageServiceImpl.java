package com.example.socketchatroom.service.impl;

import com.example.socketchatroom.dto.FrameMessage;
import com.example.socketchatroom.model.Message;
import com.example.socketchatroom.repository.MessageRepository;
import com.example.socketchatroom.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static com.example.socketchatroom.utils.Constants.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final HashOperations<String, String, String> opsForHash;
    private final ObjectMapper objectMapper;

    @Override
    public Message saveMessage(Message message) {
        message.setDateCreate(new Date());
        return messageRepository.save(message);
    }

    @Override
    public Message sendMessage(Message message) throws JsonProcessingException {
        if (message.getRecipientNames() == null) {
            message.setRecipientNames("all");
            Message saveMessage = saveMessage(message);
            messagingTemplate.convertAndSend(WEB_SOCKET_SUBSCRIBE_TOPIC_MESSAGE_URL + saveMessage.getChatroomName(),
                    FrameMessage.builder().messageType("message").payload(saveMessage).build());
        } else {
            String[] users = message.getRecipientNames().split(",");
            String chatroom = opsForHash.get("chatroom", message.getChatroomName());
            List<String> userList = objectMapper.readValue(chatroom, typeReference);
            if (users.length == 1) {
                if (userList.contains(users[0])) {
                    Message saveMessage = saveMessage(message);
                    messagingTemplate.convertAndSend("/user/" + message.getRecipientNames().trim() + WEB_SOCKET_SUBSCRIBE_USER_MESSAGE_PRIVATE_URL + message.getChatroomName(),
                            FrameMessage.builder()
                                    .messageType("message")
                                    .payload(saveMessage)
                                    .build());
                } else {
                    throw new RuntimeException("User not found in chatroom");
                }
            }

            if (users.length >= 2) {
                for (String user : users) {
                    if (userList.contains(user)) {
                        Message saveMessage = saveMessage(message);
                        messagingTemplate.convertAndSend("/user/" + user.trim() + WEB_SOCKET_SUBSCRIBE_USER_MESSAGE_PRIVATE_URL + message.getChatroomName(),
                                FrameMessage.builder()
                                        .messageType("message")
                                        .payload(saveMessage)
                                        .build());
                    } else {
                        throw new RuntimeException("User not found in chatroom");
                    }
                }
            }
        }
        return message;
    }
}
