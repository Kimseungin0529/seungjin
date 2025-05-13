package com.project.doongdoong.domain.user.repository.spring;

import com.project.doongdoong.domain.analysis.model.Analysis;
import com.project.doongdoong.domain.question.model.Question;
import com.project.doongdoong.domain.user.model.SocialType;
import com.project.doongdoong.domain.user.model.User;
import com.project.doongdoong.domain.user.repository.UserRepository;
import com.project.doongdoong.module.IntegrationSupportTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.project.doongdoong.domain.question.model.QuestionContent.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


class UserRepositoryTest extends IntegrationSupportTest {
    @Autowired
    UserRepository userRepository;

    @DisplayName("감정 분석 리스트를 포함한 사용자 정보를 조회합니다.")
    @Test
    void findUserWithAnalysisBySocialTypeAndSocialId() {
        // given
        String socialId = "12345";
        User user = User.builder()
                .socialId(socialId)
                .socialType(SocialType.APPLE)
                .build();
        List<Question> questionList1 = List.of(Question.of(UNFIXED_QUESTION1), Question.of(UNFIXED_QUESTION2),
                Question.of(UNFIXED_QUESTION2), Question.of(UNFIXED_QUESTION4));
        List<Question> questionList2 = List.of(Question.of(FIXED_QUESTION1), Question.of(FIXED_QUESTION2),
                Question.of(FIXED_QUESTION3), Question.of(FIXED_QUESTION4));
        List<Question> questionList3 = List.of(Question.of(UNFIXED_QUESTION1), Question.of(UNFIXED_QUESTION2),
                Question.of(FIXED_QUESTION3), Question.of(FIXED_QUESTION4));

        Analysis analysis1 = Analysis.of(user, questionList1);
        Analysis analysis2 = Analysis.of(user, questionList2);
        Analysis analysis3 = Analysis.of(user, questionList3);
        analysis1.addUser(user);
        analysis2.addUser(user);
        analysis3.addUser(user);

        LocalDateTime now = LocalDateTime.now();
        LocalDate pastDay = now.minusDays(1L).toLocalDate();
        analysis1.changeFeelingStateAndAnalyzeTime(15, pastDay);
        analysis2.changeFeelingStateAndAnalyzeTime(20, now.toLocalDate());

        userRepository.save(user);


        // when
        Optional<User> OptionalResult = userRepository.findUserWithAnalysisBySocialTypeAndSocialId(SocialType.APPLE, socialId);

        // then
        assertThat(OptionalResult).isPresent();
        User result = OptionalResult.get();

        assertThat(result)
                .extracting(User::getSocialId, User::getSocialType)
                .containsExactly(socialId, SocialType.APPLE);

        assertThat(result.getAnalysisList())
                .hasSize(3)
                .extracting(Analysis::getAnalyzeTime, Analysis::getFeelingState)
                .containsExactlyInAnyOrder(
                        tuple(analysis1.getAnalyzeTime(), analysis1.getFeelingState()),
                        tuple(analysis2.getAnalyzeTime(), analysis2.getFeelingState()),
                        tuple(analysis3.getAnalyzeTime(), analysis3.getFeelingState())
                );
    }

}