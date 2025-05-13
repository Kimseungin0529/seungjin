package com.project.doongdoong.domain.user.model;

import com.project.doongdoong.domain.analysis.model.Analysis;
import com.project.doongdoong.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String socialId;

    private String nickname;

    private String email;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE

    private long emotionGrowth = 0L;

    @ElementCollection(fetch = FetchType.EAGER) // security와 같이 사용할 권한 역할
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Analysis> analysisList = new ArrayList<>();

    // 권한 추가

    @Builder
    public User(String socialId, String nickname, String email, SocialType socialType) {
        this.socialId = socialId;
        this.nickname = nickname;
        this.email = email;
        this.socialType = socialType;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void checkRoles() {
        this.roles = roles.isEmpty() ? Collections.singletonList(Role.ROLE_USER.toString()) : roles;

    }

    public boolean isSameEmail(String email) {
        return this.email.equals(email);
    }

    public boolean isSameNickname(String nickname) {
        return this.nickname.equals(nickname);
    }


    public void growUp() {
        this.emotionGrowth++;
        checkGrowth();
    }

    private void checkGrowth() {
        if (getEmotionGrowth() == 101L)
            this.emotionGrowth %= 101;
    }


}
