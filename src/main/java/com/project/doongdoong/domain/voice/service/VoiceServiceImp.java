package com.project.doongdoong.domain.voice.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.doongdoong.domain.question.model.QuestionContent;
import com.project.doongdoong.domain.voice.dto.response.VoiceDetailResponseDto;
import com.project.doongdoong.domain.voice.exception.FileUploadException;
import com.project.doongdoong.domain.voice.model.FileExtension;
import com.project.doongdoong.domain.voice.model.Voice;
import com.project.doongdoong.domain.voice.repository.VoiceRepository;
import com.project.doongdoong.global.exception.ErrorType;
import com.project.doongdoong.global.exception.servererror.ExternalApiCallException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static com.project.doongdoong.domain.voice.model.FileExtension.MP3;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoiceServiceImp implements VoiceService {

    private final AmazonS3Client amazonS3Client;
    private final VoiceRepository voiceRepository;

    @Value("${cloud.aws.bucket}")
    private String bucketName;
    private static final String VOICE_KEY = "voice/";


    @Override
    @Transactional
    public VoiceDetailResponseDto saveVoice(MultipartFile multipartFile) {
        String originalName = multipartFile.getOriginalFilename();
        Voice voice = Voice.commonBuilder()
                .originName(originalName)
                .build();

        String filename = getObjectKeyFrom(voice);
        try {
            ObjectMetadata objectMetadata = initObjectMetadata(originalName, multipartFile);
            amazonS3Client.putObject(bucketName, filename, multipartFile.getInputStream(), objectMetadata);
            voice.changeAccessUrl(amazonS3Client.getUrl(bucketName, filename).toString());

        } catch (SdkClientException | IOException e) {
            throw new FileUploadException(ErrorType.ServerError.FILE_UPLOAD_FAIL, e.getMessage());
        }
        voiceRepository.save(voice);

        return VoiceDetailResponseDto.of(voice.getAccessUrl());
    }

    @Override
    @Transactional
    public void saveVoice(byte[] audioContent, String originName, QuestionContent questionContent) {
        Voice voice = Voice.initVoiceContentBuilder()
                .originName(originName)
                .questionContent(questionContent)
                .build();
        String filename = getObjectKeyFrom(voice);

        try {
            ObjectMetadata objectMetadata = initObjectMetadata(audioContent);
            amazonS3Client.putObject(bucketName, filename, new ByteArrayInputStream(audioContent), objectMetadata);

            voice.changeAccessUrl(amazonS3Client.getUrl(bucketName, filename).toString());
            voiceRepository.save(voice);

        } catch (SdkClientException e) {
            throw new FileUploadException(ErrorType.ServerError.FILE_UPLOAD_FAIL, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteVoices(List<Voice> voices) {
        List<Long> voiceIds = getIdsFrom(voices);
        if (voiceIds.isEmpty()) {
            return;
        }

        List<DeleteObjectsRequest.KeyVersion> keys = getRequestKeysFrom(voices);
        try {
            DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName)
                    .withKeys(keys);
            amazonS3Client.deleteObjects(deleteRequest);

        } catch (SdkClientException e) {
            throw new ExternalApiCallException("S3 파일 삭제 실패 : " + e.getMessage());
        }
        voiceRepository.deleteVoicesByUrls(voiceIds);
    }

    private ObjectMetadata initObjectMetadata(String originalName, MultipartFile multipartFile) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(FileExtension.getMineTypeFrom(originalName));
        objectMetadata.setContentLength(multipartFile.getInputStream().available());
        return objectMetadata;
    }

    private ObjectMetadata initObjectMetadata(byte[] content) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(MP3.getMineType());
        objectMetadata.setContentLength(content.length);
        return objectMetadata;
    }


    private List<Long> getIdsFrom(List<Voice> voices) {
        return voices.stream().map(Voice::getVoiceId).toList();
    }

    private List<DeleteObjectsRequest.KeyVersion> getRequestKeysFrom(List<Voice> voices) {
        return voices.stream()
                .map(voice -> new DeleteObjectsRequest.KeyVersion(getObjectKeyFrom(voice)))
                .toList();
    }

    private String getObjectKeyFrom(Voice voice) {
        return VOICE_KEY + voice.getStoredName();
    }


}
