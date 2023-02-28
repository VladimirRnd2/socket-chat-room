package com.example.socketchatroom.service;

import com.example.socketchatroom.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface MessageService {

    Message saveMessage(Message message);

    Message sendMessage(Message message) throws JsonProcessingException;
}
