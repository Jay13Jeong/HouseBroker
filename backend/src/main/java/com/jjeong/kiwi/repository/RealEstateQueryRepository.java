package com.jjeong.kiwi.repository;

import static com.jjeong.kiwi.model.QRealEstate.realEstate;

import com.jjeong.kiwi.dto.RealEstateImgPathDto;
import com.jjeong.kiwi.model.RealEstate;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.criterion.Projection;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RealEstateQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<RealEstateImgPathDto> findRealEstateImgPathDtoById(Long realEstateId) {
        return queryFactory
            .select(Projections.fields(RealEstateImgPathDto.class,
                Expressions.asNumber(realEstateId).as("id"),
                realEstate.image,
                realEstate.image2,
                realEstate.image3,
                realEstate.image4,
                realEstate.image5,
                realEstate.image6,
                realEstate.image7,
                realEstate.image8,
                realEstate.image9,
                realEstate.image10
                ))
            .from(realEstate)
            .where(realEstate.id.eq(realEstateId))
            .fetch();
    }

}
