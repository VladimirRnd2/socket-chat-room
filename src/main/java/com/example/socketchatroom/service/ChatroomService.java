package com.example.socketchatroom.service;

import com.example.socketchatroom.model.Chatroom;

import java.util.List;

public interface ChatroomService {

    Chatroom createNewChatroom(String userName);

    List<String> getActiveUsersInChatroom(String chatroom);

    void deleteChatroomByName(String chatroomName);

    Chatroom getChatroomByName(String chatroomName);
}
