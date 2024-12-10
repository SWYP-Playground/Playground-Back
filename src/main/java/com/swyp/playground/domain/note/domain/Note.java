package com.swyp.playground.domain.note.domain;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="note")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="NOTE_ID")
    private Long noteId;

    @Column(name="TARGET_NICKNAME", nullable=false)
    private String targetNickname;

    @Column(name="CONTENT", nullable=false)
    private String content;

    @Column(name="WRITTEN_BY", nullable=false)
    private String writtenBy;

    @Column(name="SENT_AT", nullable=false)
    private Date sentAt;
}
