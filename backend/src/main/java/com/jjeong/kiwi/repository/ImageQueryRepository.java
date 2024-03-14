package com.jjeong.kiwi.repository;

import static com.jjeong.kiwi.model.QImage.image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImageQueryRepository {
    private final JPAQueryFactory queryFactory;
    private static final Logger logger = LoggerFactory.getLogger(ImageQueryRepository.class);

    public long deleteOldImages(LocalDate localDate) {
        return queryFactory
            .delete(image)
            .where(image.lastModified.before(localDate.atStartOfDay()))
            .execute();
    }

}
