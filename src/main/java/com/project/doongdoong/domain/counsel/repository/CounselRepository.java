package com.project.doongdoong.domain.counsel.repository;


import com.project.doongdoong.domain.counsel.model.Counsel;
import com.project.doongdoong.domain.counsel.repository.querydsl.CounselCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CounselRepository extends JpaRepository<Counsel, Long>, CounselCustomRepository {

    @Query("select c from Counsel c left outer join fetch c.analysis where c.id = :counselId")
    Optional<Counsel> findWithAnalysisById(@Param("counselId") Long counselId);

    @Query(value = """
            SELECT DATE(created_time) AS date, counsel_type, COUNT(*) AS count
            FROM counsel
            GROUP BY DATE(created_time), counsel_type
            """, nativeQuery = true)
    List<Object[]> countCounselGroupByDateAndType();

}
