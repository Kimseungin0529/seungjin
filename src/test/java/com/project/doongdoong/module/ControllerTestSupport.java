package com.project.doongdoong.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.doongdoong.domain.analysis.controller.AnalysisController;
import com.project.doongdoong.domain.analysis.service.AnalysisService;
import com.project.doongdoong.domain.answer.service.AnswerService;
import com.project.doongdoong.domain.counsel.controller.CounselController;
import com.project.doongdoong.domain.counsel.service.CounselService;
import com.project.doongdoong.domain.user.controller.UserController;
import com.project.doongdoong.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

    @WebMvcTest(controllers = {CounselController.class, AnalysisController.class, UserController.class} /* 추가로 필요한 컨트롤러 클래스 지정 */)
    public abstract class ControllerTestSupport {
        @Autowired
        protected MockMvc mockMvc;
        @MockBean
        protected CounselService counselService;
        @MockBean
        protected AnalysisService analysisService;
        @MockBean
        protected AnswerService answerService;
        @MockBean
        protected UserService userService;
        @Autowired
        protected ObjectMapper objectMapper;
    }
