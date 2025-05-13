package com.project.doongdoong.domain.voice.model;

import com.project.doongdoong.domain.voice.exception.NotFoundFileExtension;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FileExtension {

    MP3("audio/mpeg"),
    M4A("audio/mp4"),
    WAV("audio/wav"),
    ;
    private final String mineType;

    public static String getMineTypeFrom(String filename) {
        String findExtension = FilenameUtils.getExtension(filename).toUpperCase();

        return Arrays.stream(values())
                .filter(extension -> extension.name().equals(findExtension))
                .findFirst()
                .map(FileExtension::getMineType)
                .orElseThrow(() -> new NotFoundFileExtension(findExtension));
    }
}
