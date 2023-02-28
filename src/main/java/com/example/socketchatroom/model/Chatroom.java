package com.example.socketchatroom.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.boot.actuate.audit.listener.AuditListener;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "chatrooms")
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chatroom_name", nullable = false, unique = true, updatable = false)
    private String chatroomName;

    @Column(name = "creator_name", nullable = false, updatable = false)
    private String creatorName;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_create", nullable = false)
    private Date dateCreate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Chatroom chatroom = (Chatroom) o;
        return id != null && Objects.equals(id, chatroom.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
