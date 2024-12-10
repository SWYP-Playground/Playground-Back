package com.swyp.playground.domain.report.domain;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long reportId;

    @Column(nullable = false)
    private String targetNickname;

    @Column(nullable = false)
    private Long findFriendId;

    @Column
    private String cause;

    @Column(nullable = false)
    private String writtenBy;

    @Column(nullable = false)
    private Date sentAt;
}
