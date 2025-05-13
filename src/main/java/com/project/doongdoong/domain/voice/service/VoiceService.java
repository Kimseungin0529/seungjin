package com.project.doongdoong.domain.voice.service;

import com.project.doongdoong.domain.question.model.QuestionContent;
import com.project.doongdoong.domain.voice.dto.response.VoiceDetailResponseDto;
import com.project.doongdoong.domain.voice.model.Voice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VoiceService {

    VoiceDetailResponseDto saveVoice(MultipartFile multipartFile);

    void saveVoice(byte[] audioContent, String originName, QuestionContent questionContent);

    void deleteVoices(List<Voice> voices);

}
