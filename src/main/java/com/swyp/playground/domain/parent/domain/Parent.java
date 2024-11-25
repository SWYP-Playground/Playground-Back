package com.swyp.playground.domain.parent.domain;

import com.swyp.playground.domain.child.domain.Child;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parent")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "nickname", length = 20, nullable = false, unique = true)
    private String nickname;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ParentRoleType role; // "아빠" 또는 "엄마"

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone_number", length = 11, nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "child_count", nullable = false)
    private Integer childCount;

    @Column(name = "profile_img", length = 255)
    private String profileImg;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> children = new ArrayList<>();

    @Column(name = "introduce", length = 500)
    private String introduce;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "manner_temp", precision = 5, scale = 2, nullable = false)
    private BigDecimal mannerTemp;


    public void addChild(Child child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void removeChild(Child child) {
        this.children.remove(child);
        child.setParent(null);
    }
}
