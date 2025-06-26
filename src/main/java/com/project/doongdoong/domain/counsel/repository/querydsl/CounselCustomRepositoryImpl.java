package com.project.doongdoong.domain.counsel.repository.querydsl;


import com.project.doongdoong.domain.counsel.model.Counsel;
import com.project.doongdoong.domain.user.model.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.project.doongdoong.domain.analysis.model.QAnalysis.analysis;
import static com.project.doongdoong.domain.counsel.model.QCounsel.counsel;

public class CounselCustomRepositoryImpl implements CounselCustomRepository {

    private final JPAQueryFactory queryFactory;

    public CounselCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Counsel> searchPageCounselList(User user, Pageable pageable) {
        List<Counsel> content = queryFactory
                .selectFrom(counsel)
                .leftJoin(counsel.analysis, analysis).fetchJoin()
                //.where(userEq(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(counsel.count())
                .from(counsel);
                //.where(userEq(user));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression userEq(User user) {
        return counsel.user.id.eq(user.getId());
    }
}
