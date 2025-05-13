package com.project.doongdoong.domain.counsel.controller;

import com.project.doongdoong.domain.counsel.dto.request.CounselCreateRequest;
import com.project.doongdoong.domain.counsel.dto.response.CounselDetailResponse;
import com.project.doongdoong.domain.counsel.dto.response.CounselListResponse;
import com.project.doongdoong.domain.counsel.dto.response.CounselResponse;
import com.project.doongdoong.domain.counsel.dto.response.CounselResultResponse;
import com.project.doongdoong.module.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CounselControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("상담에 대한 결과가 나온다.")
    @WithMockUser(username = "123456_APPLE")
    void createCounsel() throws Exception {
        // given
        CounselCreateRequest request = CounselCreateRequest.builder()
                .analysisId(null)
                .question("요즘 가족이 맨날 싸워. 청소 관련 성향의 차이로 다투기 시작했어. 사소한 이유가 너무 커져서 폭언을 하기도 해. 어떻게 해결해야 할까?")
                .counselType("가족")
                .build();

        CounselResultResponse response = new CounselResultResponse(
                1L, "counselContent 입니다.", "imageUrl 입니다."
        );

        given(counselService.consult(anyString(), any(CounselCreateRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/counsel")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/counsel/1"))
                .andExpect(jsonPath("$.data.counselId").value(1L))
                .andExpect(jsonPath("$.data.counselContent").value("counselContent 입니다."))
                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl 입니다."));

    }

    @Test
    @DisplayName("상담을 하기 위해서는 상담 카테코리와 상담 질문은 필수입니다.")
    @WithMockUser(username = "123456_APPLE")
    void createCounselWithEmptyCounselType() throws Exception {
        //given
        CounselCreateRequest request = CounselCreateRequest.builder()
                .analysisId(null)
                .counselType(null)
                .question(null)
                .build();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/counsel")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(0))
                .andExpect(jsonPath("$.message").value("잘못된 입력 형식이 존재합니다."))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", containsInAnyOrder(
                                "카테코리는 필수입니다.", "상담 질문은 필수입니다.")
                        )
                );


    }

    @DisplayName("본인의 상담 내용을 조회합니다.")
    @Test
    @WithMockUser(username = "123456_APPLE")
    void findCounselContent() throws Exception {
        //given
        CounselDetailResponse response = CounselDetailResponse.builder()
                .date("2024-04-25")
                .counselId(1L)
                .question("나는 요즘 연애 고민이 있어. 어떻게 할까? 내 사랑이 이루어질 수 있을까?")
                .answer("질문에 대한 내용이 너무 적기에 명확하게 알려줄 순 없어. 다만 포기하지마. 너가 후회없이 노력했냐에 따라 달라져!")
                .imageUrl("https://test-image-url")
                .counselType("연애")
                .build();

        given(counselService.findCounselContent(anyString(), anyLong()))
                .willReturn(response);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/counsel/{id}", 1L)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.date").value(response.getDate()))
                .andExpect(jsonPath("$.data.counselId").value(response.getCounselId()))
                .andExpect(jsonPath("$.data.question").value(response.getQuestion()))
                .andExpect(jsonPath("$.data.answer").value(response.getAnswer()))
                .andExpect(jsonPath("$.data.imageUrl").value(response.getImageUrl()))
                .andExpect(jsonPath("$.data.counselType").value(response.getCounselType()));


    }

    @DisplayName("본인의 상당 정보를 페이징 조회합니다.")
    @Test
    @WithMockUser(username = "123456_APPLE")
    void findCounsels() throws Exception {
        //given
        List<CounselResponse> counselResponsesList = generateCounselResponseList();

        CounselListResponse response = CounselListResponse.builder()
                .currentPage(1)
                .numberPerPage(10)
                .totalPage(2)
                .totalElements(12)
                .counselContent(counselResponsesList)
                .build();

        given(counselService.findCounsels(anyString(), eq(response.getCurrentPage())))
                .willReturn(response);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/counsel")
                                .with(csrf())
                                .queryParam("pageNumber", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentPage").value(response.getCurrentPage()))
                .andExpect(jsonPath("$.data.numberPerPage").value(response.getNumberPerPage()))
                .andExpect(jsonPath("$.data.totalPage").value(response.getTotalPage()))
                .andExpect(jsonPath("$.data.totalElements").value(response.getTotalElements()))
                .andExpect(jsonPath("$.data.counselContent").isArray())
                .andExpect(jsonPath("$.data.counselContent.length()").value(response.getCounselContent().size()));

    }

    @DisplayName("페이지 번호는 1부터 가능합니다.")
    @Test
    @WithMockUser(username = "123456_APPLE")
    void findCounselsExceptionQueryParamOfPageNumber() throws Exception {
        //given
        List<CounselResponse> counselResponsesList = generateCounselResponseList();

        CounselListResponse response = CounselListResponse.builder()
                .currentPage(1)
                .numberPerPage(10)
                .totalPage(2)
                .totalElements(12)
                .counselContent(counselResponsesList)
                .build();

        given(counselService.findCounsels(anyString(), eq(response.getCurrentPage())))
                .willReturn(response);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/counsel")
                                .with(csrf())
                                .queryParam("pageNumber", "0")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(0))
                .andExpect(jsonPath("$.message").value("잘못된 입력 형식이 존재합니다."))
                .andExpect(jsonPath("$.details").value("페이지 시작은 최소 1입니다."));
    }

    private List<CounselResponse> generateCounselResponseList() {
        return List.of(
                createCounselResponse("2024-04-25", 1L, true, "연애"),
                createCounselResponse("2024-04-26", 2L, true, "연애"),
                createCounselResponse("2024-04-27", 3L, false, "연애"),
                createCounselResponse("2024-04-28", 4L, true, "연애"),
                createCounselResponse("2024-04-29", 5L, false, "취업진로"),
                createCounselResponse("2024-04-30", 6L, true, "연애"),
                createCounselResponse("2024-05-01", 11L, true, "취업진로"),
                createCounselResponse("2024-05-01", 12L, false, "정신건강"),
                createCounselResponse("2024-05-10", 13L, true, "대인관계"),
                createCounselResponse("2024-05-11", 14L, false, "정신건강"),
                createCounselResponse("2024-05-12", 15L, true, "가족"),
                createCounselResponse("2024-05-13", 16L, true, "연애")
        );


    }

    private CounselResponse createCounselResponse(String date, Long counselId, boolean isAnalysisUsed, String counselType) {
        return CounselResponse.builder()
                .date(date)
                .counselId(counselId)
                .isAnalysisUsed(isAnalysisUsed)
                .counselType(counselType)
                .build();
    }


}