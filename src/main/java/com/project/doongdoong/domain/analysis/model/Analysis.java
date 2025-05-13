package com.project.doongdoong.domain.analysis.model;

import com.project.doongdoong.domain.answer.model.Answer;
import com.project.doongdoong.domain.counsel.model.Counsel;
import com.project.doongdoong.domain.question.model.Question;
import com.project.doongdoong.domain.user.model.User;
import com.project.doongdoong.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Analysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "analysis_id")
    private Long id;

    private double feelingState;

    private LocalDate analyzeTime;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "analysis")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "analysis")
    private List<Answer> answers = new ArrayList<>();

    @OneToOne(fetch = LAZY, mappedBy = "analysis")
    private Counsel counsel;

    private static final int MAX_ANSWER_COUNT = 4;

    public static Analysis of(User user, List<Question> questions) {
        return Analysis.builder()
                .user(user)
                .questions(questions)
                .build();
    }

    @Builder
    public Analysis(User user, List<Question> questions) {
        this.feelingState = 0;
        this.questions = questions;
        this.user = user;
    }

    public boolean hasAllAnswer() {
        return this.answers.size() == MAX_ANSWER_COUNT;
    }

    public boolean isMissingAnswers() {
        return !hasAllAnswer();
    }


    public void changeFeelingStateAndAnalyzeTime(double feelingState, LocalDate analyzeTime) {
        this.feelingState = feelingState;
        this.analyzeTime = analyzeTime;
    }

    public boolean equalsAnalyzeTimeTo(LocalDate time) {
        if (this.analyzeTime == null || time == null) {
            return false;
        }
        return time.equals(this.analyzeTime);
    }

    public boolean isAlreadyAnalyzed() {
        return this.analyzeTime != null;
    }

    public void addUser(User user) {
        this.user = user;
        user.getAnalysisList().add(this);
    }

}
