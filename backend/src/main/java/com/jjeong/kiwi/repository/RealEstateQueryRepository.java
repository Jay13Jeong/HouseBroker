package com.jjeong.kiwi.repository;

import static com.jjeong.kiwi.model.QRealEstate.realEstate;

import com.jjeong.kiwi.dto.RealEstateImgPathDto;
import com.jjeong.kiwi.dto.RealEstateWithoutImgDto;
import com.jjeong.kiwi.model.RealEstate;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RealEstateQueryRepository {
    private final JPAQueryFactory queryFactory;
    private static final Logger logger = LoggerFactory.getLogger(RealEstateQueryRepository.class);

    final static Expression[] IMG_EXPRESSIONS = new Expression[]{
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
    };

    public RealEstateImgPathDto findReImgPathDtoById(Long realEstateId) {
        RealEstateImgPathDto result =
            queryFactory
                .select(Projections.fields(RealEstateImgPathDto.class,
                    IMG_EXPRESSIONS
                ))
                .from(realEstate)
                .where(realEstate.id.eq(realEstateId))
                .fetchOne();

        if (result != null) result.setId(realEstateId);

        return result;
    }

    public RealEstateImgPathDto findReImgPathDtoByIdAndIdxList(Long realEstateId, Long[] idxList) {

        Expression[] expressions =
            Arrays.stream(idxList).map(idx -> selectImagePath(idx)).toArray(Expression[]::new);

        RealEstateImgPathDto result =
            queryFactory
            .select(Projections.fields(RealEstateImgPathDto.class,
                expressions
            ))
            .from(realEstate)
            .where(realEstate.id.eq(realEstateId))
            .fetchOne();

        if (true) result.setId(realEstateId); //exists 추가 필요.

        return result;
    }

    private Expression selectImagePath(Long idx) {
        logger.debug(IMG_EXPRESSIONS[idx.intValue()].toString(), idx);
        return IMG_EXPRESSIONS[idx.intValue() - 1];
    }

    public RealEstateWithoutImgDto findByIdWithoutImg(Long realEstateId) {
        return queryFactory
            .select(Projections.fields(RealEstateWithoutImgDto.class,
                Expressions.asNumber(realEstateId).as("id"),
                realEstate.administration_cost,
                realEstate.administration_cost2,
                realEstate.administrative_agency_approval_date,
                realEstate.area,
                realEstate.transaction_type,
                realEstate.title,
                realEstate.soldout,
                realEstate.residence_availability_date,
                realEstate.relay_object_type,
                realEstate.price,
                realEstate.number_of_cars_parked,
                realEstate.longitude,
                realEstate.location,
                realEstate.latitude,
                realEstate.direction,
                realEstate.description
                ))
            .from(realEstate)
            .where(realEstate.id.eq(realEstateId))
            .fetchOne();
    }

    public List<RealEstate> findByOffset(Long offset, Long limit) {
        return queryFactory
            .selectFrom(realEstate)
            .from(realEstate)
            .offset(offset)
            .limit(limit)
            .fetch();
    }

    public List<RealEstate> findByKeySetRange(Long begin, Long end) {
        return queryFactory
            .selectFrom(realEstate)
            .from(realEstate)
            .where(realEstate.id.between(begin, end))
            .fetch();
    }

}
