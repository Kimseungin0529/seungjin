package com.project.doongdoong.domain.user.repository;

import com.project.doongdoong.domain.user.model.SocialType;
import com.project.doongdoong.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId); // OAuth2 로그인 시, 사용하는 메소드


    @Query("select u from User u left join u.analysisList al left join al.answers an " +
            "where u.socialType = :socialType and u.socialId = :socialId")
    Optional<User> findUserWithAnalysisBySocialTypeAndSocialId(@Param("socialType") SocialType socialType, @Param("socialId") String socialId);
}
