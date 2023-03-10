package com.example.socketchatroom.controller;

import com.example.socketchatroom.dto.ChatAndUserDTO;
import com.example.socketchatroom.dto.FrameMessage;
import com.example.socketchatroom.dto.MessageRequest;
import com.example.socketchatroom.model.Chatroom;
import com.example.socketchatroom.model.Message;
import com.example.socketchatroom.security.JwtProvider;
import com.example.socketchatroom.service.ChatroomService;
import com.example.socketchatroom.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

import static com.example.socketchatroom.utils.Constants.*;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final JwtProvider jwtProvider;
    private final ChatroomService chatroomService;
    private final MessageService messageService;
    private final HashOperations<String, String, String> opsForHash;
    private final ObjectMapper objectMapper;

    @MessageMapping("message")
    public void sendMessage(@Payload String messageRequest, Principal principal) throws Exception {
        System.out.println(principal);
        Message message = getMessageFromJson(messageRequest, principal.getName());
        Message resultMessage = messageService.sendMessage(message);
        System.out.println("Message : [Id: "
                + resultMessage.getId()
                + " Sender: "
                + resultMessage.getSenderName()
                + " Recipients: "
                + resultMessage.getRecipientNames()
                + " Chatroom: "
                + resultMessage.getChatroomName()
                + " Message: "
                + resultMessage.getContent()
                + " Date: "
                + resultMessage.getDateCreate()
                + "]");
    }

    @SubscribeMapping("persons.change.{roomName}")
    public FrameMessage getAllUsersInChatRoom(@DestinationVariable String roomName) throws JsonProcessingException {
        List<String> activeUsersInChatroom = objectMapper.readValue(opsForHash.get("chatroom", roomName), typeReference);
        return FrameMessage.builder()
                .messageType("users")
                .payload(activeUsersInChatroom)
                .build();
    }

    @GetMapping("/chat/create")
    public ChatAndUserDTO createChatRoom(HttpServletRequest request) {
        Chatroom chatroomOrGet = chatroomService.createNewChatroom(jwtProvider.getLoginFromAccessToken(jwtProvider.getTokenFromRequest(request)));
        return ChatAndUserDTO.builder()
                .chatroomName(chatroomOrGet.getChatroomName())
                .username(chatroomOrGet.getCreatorName())
                .build();
    }

    @GetMapping("/username")
    public String getUsernameFromJwt(HttpServletRequest request) {
        return jwtProvider.getLoginFromAccessToken(jwtProvider.getTokenFromRequest(request));
    }

    private Message getMessageFromJson(String request, String username) throws JsonProcessingException {
        MessageRequest messageRequest = objectMapper.readValue(request, MessageRequest.class);
        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setRecipientNames(messageRequest.getRecipientsName());
        message.setChatroomName(messageRequest.getChatroomName());
        message.setSenderName(username);
        return message;
    }
}
