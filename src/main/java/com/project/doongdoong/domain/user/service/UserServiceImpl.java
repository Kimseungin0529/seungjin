package com.project.doongdoong.domain.user.service;

import com.project.doongdoong.domain.user.dto.UserInformationResponseDto;
import com.project.doongdoong.domain.user.exeception.RefreshTokenNotFoundException;
import com.project.doongdoong.domain.user.exeception.TokenInfoFobiddenException;
import com.project.doongdoong.domain.user.exeception.UserNotFoundException;
import com.project.doongdoong.domain.user.model.SocialIdentifier;
import com.project.doongdoong.domain.user.model.SocialType;
import com.project.doongdoong.domain.user.model.User;
import com.project.doongdoong.domain.user.repository.UserRepository;
import com.project.doongdoong.global.common.BlackAccessToken;
import com.project.doongdoong.global.common.RefreshToken;
import com.project.doongdoong.global.dto.request.LogoutDto;
import com.project.doongdoong.global.dto.request.OAuthTokenDto;
import com.project.doongdoong.global.dto.request.ReissueDto;
import com.project.doongdoong.global.dto.response.TokenDto;
import com.project.doongdoong.global.repositoty.BlackAccessTokenRepository;
import com.project.doongdoong.global.repositoty.RefreshTokenRepository;
import com.project.doongdoong.global.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackAccessTokenRepository blackAccessTokenRepository;
    private final JwtProvider jwtProvider;


    @Transactional
    public TokenDto checkRegistration(OAuthTokenDto oAuthTokenInfo) {

        String socialId = oAuthTokenInfo.getSocialId();
        String email = oAuthTokenInfo.getEmail();
        String nickname = oAuthTokenInfo.getNickname();
        SocialType socialType = SocialType.findSocialTypeBy(oAuthTokenInfo.getSocialType());

        User user = findOrRegisterUserBy(socialType, socialId, email, nickname);

        blackAccessTokenRepository.findById(BlackAccessToken.generateUniqueKeyWith(socialId, socialType.getDescription()))
                .ifPresent(blackAccessTokenRepository::delete);

        TokenDto tokenInfoResponse = jwtProvider.generateToken(socialId, socialType.getDescription(), user.getRoles());

        RefreshToken refresh = RefreshToken.of(user.getSocialId(), user.getSocialType().getDescription(), tokenInfoResponse.getRefreshToken());
        refreshTokenRepository.findByUniqueId(refresh.getUniqueId())
                .ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.save(refresh);

        return tokenInfoResponse;
    }

    private User findOrRegisterUserBy(SocialType socialType, String socialId, String email, String nickname) {
        User user = userRepository.findBySocialTypeAndSocialId(socialType, socialId)
                .orElse(createUser(socialId, email, nickname, socialType));
        checkChange(email, nickname, user);
        user.checkRoles();

        return userRepository.save(user);
    }

    private void checkChange(String email, String nickname, User user) {

        if (!user.isSameEmail(email)) {
            user.changeEmail(email);
        }

        if (!user.isSameNickname(nickname)) {
            user.changeNickname(nickname);
        }
    }

    private User createUser(String socialId, String email, String nickname, SocialType socialType) {
        return User
                .builder()
                .socialId(socialId)
                .email(email)
                .nickname(nickname)
                .socialType(socialType)
                .build();
    }

    @Transactional
    public TokenDto reissue(ReissueDto reissueTokenDto) {
        String refreshToken = reissueTokenDto.getRefreshToken();
        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(RefreshTokenNotFoundException::new);

        String token = findRefreshToken.getRefreshToken();
        String socialId = jwtProvider.extractSocialId(token);
        String socialType = jwtProvider.extractSocialType(token);
        List<String> roleList = jwtProvider.extractRole(token);

        TokenDto tokenDto = jwtProvider.generateToken(socialId, socialType, roleList);
        return TokenDto.of(tokenDto.getAccessToken());
    }

    @Transactional
    public void logout(LogoutDto tokenInfoDto, String accessToken) {
        String socialType = jwtProvider.extractSocialType(accessToken);
        String socialId = jwtProvider.extractSocialId(accessToken);
        refreshTokenRepository.findByRefreshToken(tokenInfoDto.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);

        String accessSocialId = jwtProvider.extractSocialId(accessToken);
        String accessSocialType = jwtProvider.extractSocialType(accessToken);
        validateTokenConsistency(accessSocialId, socialId, accessSocialType, socialType);

        BlackAccessToken blackAccessToken = BlackAccessToken.of(socialId, socialType, accessToken);
        blackAccessTokenRepository.save(blackAccessToken);
    }

    private void validateTokenConsistency(String accessSocialId, String socialId, String accessSocialType, String socialType) {
        if (!accessSocialId.equals(socialId) || !accessSocialType.equals(socialType)) {
            throw new TokenInfoFobiddenException();
        }
    }

    public UserInformationResponseDto getMyPage(String uniqueValue) {
        SocialIdentifier identifier = SocialIdentifier.from(uniqueValue);
        User findUser = userRepository.findBySocialTypeAndSocialId(identifier.getSocialType(), identifier.getSocialId())
                .orElseThrow(UserNotFoundException::new);

        return UserInformationResponseDto.of(findUser.getNickname(), findUser.getEmail(), findUser.getSocialType().getDescription(), findUser.getEmotionGrowth());
    }


}
