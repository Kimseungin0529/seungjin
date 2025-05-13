package com.project.doongdoong.domain.counsel.service;

import com.project.doongdoong.domain.analysis.exception.AnalysisAccessDeny;
import com.project.doongdoong.domain.analysis.model.Analysis;
import com.project.doongdoong.domain.analysis.repository.AnalysisRepository;
import com.project.doongdoong.domain.answer.model.Answer;
import com.project.doongdoong.domain.counsel.dto.request.CounselCreateRequest;
import com.project.doongdoong.domain.counsel.dto.response.CounselDetailResponse;
import com.project.doongdoong.domain.counsel.dto.response.CounselListResponse;
import com.project.doongdoong.domain.counsel.dto.response.CounselResultResponse;
import com.project.doongdoong.domain.counsel.exception.CounselNotExistPageException;
import com.project.doongdoong.domain.counsel.exception.UnAuthorizedForCounselException;
import com.project.doongdoong.domain.counsel.model.Counsel;
import com.project.doongdoong.domain.counsel.model.CounselType;
import com.project.doongdoong.domain.counsel.repository.CounselRepository;
import com.project.doongdoong.domain.user.model.SocialType;
import com.project.doongdoong.domain.user.model.User;
import com.project.doongdoong.domain.user.repository.UserRepository;
import com.project.doongdoong.global.dto.response.CounselAiResponse;
import com.project.doongdoong.module.IntegrationSupportTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class CounselServiceImplTest extends IntegrationSupportTest {
    @Autowired
    CounselServiceImpl counselService;
    @Autowired
    AnalysisRepository analysisRepository;
    @Autowired
    CounselRepository counselRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("회원의 질문에 대한 상담 답변을 제공한다.")
    void consult() {
        //given
        String socialId = "123456";
        User user = createUser(socialId, SocialType.APPLE);
        User savedUser = userRepository.save(user);

        CounselCreateRequest request = new CounselCreateRequest(
                "취업진로",
                "나는 취업에 대한 고민이 있어. 내가 개발자가 될 수 있을까? 어떤 노력이 필요해?"
        );
        CounselAiResponse mockResponse = new CounselAiResponse(
                "개발자가 되기 위해서는 꾸준한 학습과 프로젝트 경험이 필요합니다.",
                "http://example.com/image.jpg"
        );
        String uniqueValue = savedUser.getSocialId() + "_" + savedUser.getSocialType().getDescription();

        when(webClientUtil.callConsult(any(HashMap.class)))
                .thenReturn(mockResponse);
        //when
        CounselResultResponse result = counselService.consult(uniqueValue, request);

        //then
        assertThat(result).isNotNull()
                .extracting("counselContent", "imageUrl")
                .containsExactly(mockResponse.getAnswer(), mockResponse.getImageUrl());
        assertThat(result.getCounselId()).isNotNull();
    }

    @Test
    @DisplayName("분석 답변과 함께 회원의 질문에 대한 상담 답변을 제공한다.")
    void consultWithAnalysis() {
        //given
        String socialId = "123456";
        User user = User.builder()
                .socialId(socialId)
                .socialType(SocialType.APPLE)
                .build();
        User savedUser = userRepository.save(user);

        Analysis analysis = createAnalysis(savedUser);
        Analysis savedAnalysis = analysisRepository.save(analysis);

        Answer answer1 = createAnswer();
        Answer answer2 = createAnswer();
        Answer answer3 = createAnswer();
        Answer answer4 = createAnswer();
        answer1.connectAnalysis(savedAnalysis);
        answer2.connectAnalysis(savedAnalysis);
        answer3.connectAnalysis(savedAnalysis);
        answer4.connectAnalysis(savedAnalysis);

        CounselCreateRequest request = new CounselCreateRequest(
                savedAnalysis.getId(),
                "취업진로",
                "나는 취업에 대한 고민이 있어. 내가 개발자가 될 수 있을까? 어떤 노력이 필요해?"
        );
        CounselAiResponse mockResponse = new CounselAiResponse(
                "개발자가 되기 위해서는 꾸준한 학습과 프로젝트 경험이 필요합니다.",
                "http://example.com/image.jpg"
        );
        String uniqueValue = savedUser.getSocialId() + "_" + savedUser.getSocialType().getDescription();

        when(webClientUtil.callConsult(any(HashMap.class)))
                .thenReturn(mockResponse);
        //when
        CounselResultResponse result = counselService.consult(uniqueValue, request);

        //then
        assertThat(result).isNotNull()
                .extracting("counselContent", "imageUrl")
                .containsExactly(mockResponse.getAnswer(), mockResponse.getImageUrl());
        assertThat(result.getCounselId()).isNotNull();

    }


    @Test
    @DisplayName("다른 사용자의 분석 정보를 통해 상담할 수 없습니다.")
    void exceptionWhenAnalysisDoesNotBelongToUser() {
        //given
        String socialId = "123456";
        User user = User.builder()
                .socialId(socialId)
                .socialType(SocialType.APPLE)
                .build();
        String socialId2 = "455678";
        User otherUser = createUser(socialId2, SocialType.APPLE);
        User savedUser = userRepository.save(user);
        User savedOtherUser = userRepository.save(otherUser);

        Analysis analysis = Analysis.builder()
                .user(savedUser)
                .build();
        Analysis savedAnalysis = analysisRepository.save(analysis);
        Answer answer1 = createAnswer();
        Answer answer2 = createAnswer();
        Answer answer3 = createAnswer();
        Answer answer4 = createAnswer();
        answer1.connectAnalysis(savedAnalysis);
        answer2.connectAnalysis(savedAnalysis);
        answer3.connectAnalysis(savedAnalysis);
        answer4.connectAnalysis(savedAnalysis);

        CounselCreateRequest request = new CounselCreateRequest(
                savedAnalysis.getId(),
                "취업진로",
                "나는 취업에 대한 고민이 있어. 내가 개발자가 될 수 있을까? 어떤 노력이 필요해?"
        );
        CounselAiResponse mockResponse = new CounselAiResponse(
                "개발자가 되기 위해서는 꾸준한 학습과 프로젝트 경험이 필요합니다.",
                "http://example.com/image.jpg"
        );
        String otherUniqueValue = savedOtherUser.getSocialId() + "_" + savedOtherUser.getSocialType().getDescription();

        when(webClientUtil.callConsult(any(HashMap.class)))
                .thenReturn(mockResponse);

        //when & then
        assertThatThrownBy(() -> counselService.consult(otherUniqueValue, request))
                .isInstanceOf(AnalysisAccessDeny.class)
                .hasMessageContaining("해당 사용자의 분석이 아니거나 존재하지 않는 분석입니다."); // 예외 메시지는 비즈니스 로직에 맞게 설정


    }


    @DisplayName("상담 내용을 조회하는 시나리오")
    @TestFactory
    Collection<DynamicTest> findCounselContent() {
        // given
        String socialId1 = "123456";
        String socialId2 = "542156";
        User user1 = createUser(socialId1, SocialType.APPLE);
        User user2 = createUser(socialId2, SocialType.APPLE);

        Counsel counsel = createCounsel(user1, "상담 질문 내용", CounselType.LOVE);
        userRepository.saveAll(List.of(user1, user2));
        Counsel savedCounsel = counselRepository.save(counsel);

        // when & then
        return List.of(
                DynamicTest.dynamicTest("상담 내용을 조회합니다.", () -> {
                            //given
                            String uniqueValue = user1.getSocialId() + "_" + user1.getSocialType().getDescription();
                            //when
                            CounselDetailResponse result = counselService.findCounselContent(uniqueValue, savedCounsel.getId());
                            //then
                            assertThat(result)
                                    .extracting("counselId", "question", "answer", "counselType")
                                    .containsExactly(savedCounsel.getId(), "상담 질문 내용", savedCounsel.getAnswer(), CounselType.LOVE.getDescription());
                        }

                ),
                DynamicTest.dynamicTest("남의 상담 내용을 조회할 수 없습니다.", () -> {
                    // given
                    String otherUniqueValue = user2.getSocialId() + "_" + user2.getSocialType().getDescription();
                    // when & then
                    assertThatThrownBy(() ->
                            counselService.findCounselContent(otherUniqueValue, savedCounsel.getId()))
                            .isInstanceOf(UnAuthorizedForCounselException.class)
                            .hasMessage("다른 사용자의 상담입니다.");

                })
        );

    }

    @DisplayName("상담 목록을 조회 시나리오")
    @TestFactory
    Collection<DynamicTest> findCounsels_시나리오() {
        // given
        String socialId = "123456";
        SocialType socialType = SocialType.APPLE;
        User user = createUser(socialId, socialType);
        userRepository.save(user);

        Counsel counsel1 = createCounsel(user, "상담 질문 내용1", CounselType.LOVE);
        Counsel counsel2 = createCounsel(user, "상담 질문 내용2", CounselType.JOB);
        Counsel counsel3 = createCounsel(user, "상담 질문 내용3", CounselType.LOVE);
        Counsel counsel4 = createCounsel(user, "상담 질문 내용4", CounselType.LOVE);
        Counsel counsel5 = createCounsel(user, "상담 질문 내용5", CounselType.MENTAL_HEALTH);
        Counsel counsel6 = createCounsel(user, "상담 질문 내용6", CounselType.LOVE);
        Counsel counsel7 = createCounsel(user, "상담 질문 내용7", CounselType.LOVE);
        Counsel counsel8 = createCounsel(user, "상담 질문 내용8", CounselType.LOVE);
        Counsel counsel9 = createCounsel(user, "상담 질문 내용9", CounselType.LOVE);
        Counsel counsel10 = createCounsel(user, "상담 질문 내용10", CounselType.LOVE);
        Counsel counsel11 = createCounsel(user, "상담 질문 내용12", CounselType.LOVE);

        counselRepository.saveAll(List.of(counsel1, counsel2, counsel3, counsel4, counsel5
                , counsel6, counsel7, counsel8, counsel9, counsel10, counsel11));

        String uniqueValue = socialId + "_" + socialType;
        DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        //when & then
        return List.of(
                DynamicTest.dynamicTest("상담 목록을 조회합니다.", () -> {
                    //given
                    int validPageNumber = 1;
                    //when
                    CounselListResponse result = counselService.findCounsels(uniqueValue, validPageNumber);
                    // then
                    assertThat(result)
                            .extracting("currentPage", "numberPerPage", "totalPage", "totalElements")
                            .containsExactly(1, 10, 2, 11L);
                    assertThat(result.getCounselContent())
                            .hasSize(10)
                            .extracting("date", "counselId", "isAnalysisUsed", "counselType")
                            .containsExactly(
                                    tuple(getDateFormatBy(counsel1, datePattern), counsel1.getId(), false, counsel1.getCounselType().getDescription()),
                                    tuple(getDateFormatBy(counsel2, datePattern), counsel2.getId(), false, counsel2.getCounselType().getDescription()),
                                    tuple(getDateFormatBy(counsel3, datePattern), counsel3.getId(), false, counsel3.getCounselType().getDescription()),
                                    tuple(getDateFormatBy(counsel4, datePattern), counsel4.getId(), false, counsel4.getCounselType().getDescription()),
                                    tuple(getDateFormatBy(counsel5, datePattern), counsel5.getId(), false, counsel5.getCounselType().getDescription()),
                                    tuple(getDateFormatBy(counsel6, datePattern), counsel6.getId(), false, counsel6.getCounselType().getDescription()),
                                    tuple(getDateFormatBy(counsel7, datePattern), counsel7.getId(), false, counsel7.getCounselType().getDescription()),
                                    tuple(getDateFormatBy(counsel8, datePattern), counsel8.getId(), false, counsel8.getCounselType().getDescription()),
                                    tuple(getDateFormatBy(counsel9, datePattern), counsel9.getId(), false, counsel9.getCounselType().getDescription()),
                                    tuple(getDateFormatBy(counsel10, datePattern), counsel10.getId(), false, counsel10.getCounselType().getDescription())
                            );
                }),
                DynamicTest.dynamicTest("존재하지 않는 페이지에 접근할 수 없습니다.", () -> {
                    //given
                    int unValidPageNumber = 3;
                    //when & then
                    assertThatThrownBy(() ->
                            counselService.findCounsels(uniqueValue, unValidPageNumber))
                            .isInstanceOf(CounselNotExistPageException.class)
                            .hasMessage("존재하지 않는 페이지입니다.");
                })
        );
    }

    private String getDateFormatBy(Counsel counsel, DateTimeFormatter datePattern) {
        return counsel.getCreatedTime().format(datePattern);
    }


    private Answer createAnswer() {
        return Answer.builder()
                .build();
    }

    private Analysis createAnalysis(User savedUser) {
        return Analysis.builder()
                .user(savedUser)
                .build();
    }

    private User createUser(String socialId, SocialType socialType) {
        return User.builder()
                .socialId(socialId)
                .socialType(socialType)
                .build();
    }

    private Counsel createCounsel(User user, String question, CounselType counselType) {
        return Counsel.builder()
                .counselType(counselType)
                .question(question)
                .user(user)
                .build();
    }

}