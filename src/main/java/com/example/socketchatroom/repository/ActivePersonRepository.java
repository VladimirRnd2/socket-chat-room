package com.example.socketchatroom.repository;

import com.example.socketchatroom.events.LoginEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ActivePersonRepository {

    private Map<String, LoginEvent> activeSessions = new HashMap<>();
    private Map<String, List<String>> activePersonsInActiveChatroom = new HashMap<>();

    public List<String> getAllActiveUsersInChatroom(String chatroom) {
        return activePersonsInActiveChatroom.get(chatroom);
    }

    public void add(String chatroomName, LoginEvent event) {
        activeSessions.put(chatroomName, event);
    }

    public LoginEvent getActivePersons(String chatroomName) {
        return activeSessions.get(chatroomName);
    }

    public void removeActivePerson(String sessionId) {
        activeSessions.remove(sessionId);
    }

    public Map<String, LoginEvent> getActiveSessions() {
        return activeSessions;
    }

    public void setActiveSessions(Map<String, LoginEvent> activeSessions) {
        this.activeSessions = activeSessions;
    }

    public Map<String, List<String>> getActivePersonsInActiveChatroom() {
        return activePersonsInActiveChatroom;
    }

    public void setActivePersonsInActiveChatroom(Map<String, List<String>> activePersonsInActiveChatroom) {
        this.activePersonsInActiveChatroom = activePersonsInActiveChatroom;
    }
}
