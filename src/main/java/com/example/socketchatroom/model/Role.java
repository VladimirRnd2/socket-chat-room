package com.example.socketchatroom.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Role {

    private Long id;
    private String name;
//    private List<Person> persons;
}