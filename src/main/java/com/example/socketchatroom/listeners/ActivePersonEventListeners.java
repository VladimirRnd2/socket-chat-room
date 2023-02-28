package com.example.socketchatroom.listeners;

import com.example.socketchatroom.dto.FrameMessage;
import com.example.socketchatroom.events.LoginEvent;
import com.example.socketchatroom.events.LogoutEvent;
import com.example.socketchatroom.service.ChatroomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.socketchatroom.utils.Constants.WEB_SOCKET_SUBSCRIBE_PERSON_EVENT_URL;
import static com.example.socketchatroom.utils.Constants.typeReference;

@Component
@RequiredArgsConstructor
public class ActivePersonEventListeners {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatroomService chatroomService;
    private final HashOperations<String, String, String> hashOperations;
    private final ObjectMapper objectMapper;

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) throws JsonProcessingException {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatroomName = headers.getFirstNativeHeader("room-name");
        Principal user = headers.getUser();
        System.out.println("User: " + Objects.requireNonNull(user).getName() + " Chatroom Name: " + chatroomName);

        LoginEvent loginEvent = new LoginEvent(user.getName(), chatroomName);
        messagingTemplate.convertAndSend(WEB_SOCKET_SUBSCRIBE_PERSON_EVENT_URL + chatroomName,
                FrameMessage.builder().messageType("login").payload(loginEvent).build());

        List<String> userList;
        if (hashOperations.hasKey("chatroom", Objects.requireNonNull(chatroomName))) {
            userList = objectMapper.readValue(hashOperations.get("chatroom", chatroomName), typeReference);
            if (!userList.contains(user.getName())) {
                userList.add(user.getName());
            }
        } else {
            userList = new ArrayList<>();
            userList.add(user.getName());
        }
        hashOperations.put("chatroom", chatroomName, objectMapper.writeValueAsString(userList));
        System.out.println("User with username : " + user.getName() + " was added to chatroom with chatroomName: " + chatroomName);
        hashOperations.put("activeSessions", Objects.requireNonNull(headers.getSessionId()), objectMapper.writeValueAsString(loginEvent));
        System.out.println("User with username: " + loginEvent.getUsername() + " was connected to chatroom : " + chatroomName + " at time: " + loginEvent.getTime());
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) throws JsonProcessingException {
        LoginEvent currentLoginEvent = objectMapper.readValue(hashOperations.get("activeSessions", event.getSessionId()), LoginEvent.class);
        String username = currentLoginEvent.getUsername();
        String chatroomName = currentLoginEvent.getChatroomName();

        LogoutEvent logoutEvent = new LogoutEvent(username);

        System.out.println("User with username: " + username + " was disconnect chatroom " + chatroomName);

        messagingTemplate.convertAndSend(WEB_SOCKET_SUBSCRIBE_PERSON_EVENT_URL + chatroomName,
                FrameMessage.builder().messageType("logout").payload(logoutEvent).build());

        List<String> userList = objectMapper.readValue(hashOperations.get("chatroom", chatroomName), typeReference);
        userList.remove(username);

        if (userList.size() == 0) {
            hashOperations.delete("chatroom", chatroomName);
            System.out.println("ChatRoom with chatroomName: " + chatroomName + " is empty");
            chatroomService.deleteChatroomByName(chatroomName);
            System.out.println("ChatRoom with chatroomName: " + chatroomName + " was deleted");
        }
        hashOperations.put("chatroom", chatroomName, objectMapper.writeValueAsString(userList));
        hashOperations.delete("activeSessions", event.getSessionId());
        System.out.println(userList.size() + " chatroom with " + chatroomName + " userlist size");
    }


}
