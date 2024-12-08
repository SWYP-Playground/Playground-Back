package com.swyp.playground.domain.parent.domain;

import com.swyp.playground.domain.child.domain.Child;
import com.swyp.playground.domain.findfriend.domain.FindFriend;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private ParentRoleType role;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone_number", length = 11, nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "child_count", nullable = false)
    private Integer childCount;

    @Column(name = "profile_img", length = 255)
    private String profileImg;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> children = new ArrayList<>(); // 리스트 초기화

    @Column(name = "introduce", length = 5000)
    private String introduce;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "manner_temp", precision = 5, scale = 2, nullable = false)
    private BigDecimal mannerTemp;

    @Column(name = "manner_temp_count", nullable = false)
    private int mannerTempCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "find_friend_id")
    private FindFriend findFriend;

    public void addChild(Child child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        child.setParent(this);
        this.children.add(child);
    }

    public void removeChild(Child child) {
        if (this.children != null) {
            this.children.remove(child);
            child.setParent(null);
        }
    }
    public BigDecimal getAverageMannerTemp() {
        if (mannerTempCount == 0) {
            return BigDecimal.ZERO;
        }
        return mannerTemp.divide(BigDecimal.valueOf(mannerTempCount), 1, RoundingMode.HALF_UP);
    }

}
