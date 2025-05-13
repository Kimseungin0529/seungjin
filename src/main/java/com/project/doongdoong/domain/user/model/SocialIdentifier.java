package com.project.doongdoong.domain.user.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class SocialIdentifier {
    private final String socialId;
    private final SocialType socialType;

    private static final String PREFIX = "_";
    private static final int SOCIAL_ID_NUMBER = 0;
    private static final int SOCIAL_TYPE_NUMBER = 1;

    private SocialIdentifier(String socialId, SocialType socialType) {
        this.socialId = socialId;
        this.socialType = socialType;
    }

    public static SocialIdentifier from(String combinedValue) {
        String[] parts = combinedValue.split(PREFIX);
        return new SocialIdentifier(parts[SOCIAL_ID_NUMBER], SocialType.findSocialTypeBy(parts[SOCIAL_TYPE_NUMBER]));
    }

    public static SocialIdentifier of(String socialId, String socialType) {
        return new SocialIdentifier(socialId, SocialType.findSocialTypeBy(socialType));
    }

    public String toUniqueValue() {
        return socialId + PREFIX + socialType.getDescription();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialIdentifier that = (SocialIdentifier) o;
        return Objects.equals(socialId, that.socialId) && socialType == that.socialType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(socialId, socialType);
    }
}
