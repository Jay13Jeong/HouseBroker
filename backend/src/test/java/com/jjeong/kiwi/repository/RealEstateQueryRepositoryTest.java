package com.jjeong.kiwi.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.jjeong.kiwi.aop.NullCheckAspect;
import com.jjeong.kiwi.config.QueryDSLConfiguration;
import com.jjeong.kiwi.dto.RealEstateImgPathDto;
import com.jjeong.kiwi.dto.RealEstateWithoutImgDto;
import com.jjeong.kiwi.model.RealEstate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({
    RealEstateQueryRepository.class,
    QueryDSLConfiguration.class,
    AnnotationAwareAspectJAutoProxyCreator.class,
    NullCheckAspect.class})
class RealEstateQueryRepositoryTest {
    @Autowired
    RealEstateQueryRepository realEstateQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void findReImgPathDtoByIdAndIdxList_WithValidDate_FindSuccessfully() {
        Long[] idxList = new Long[]{3L};
        Long realEstateId = 13L;
        assertDoesNotThrow(()
            -> realEstateQueryRepository.findReImgPathDtoByIdAndIdxList(realEstateId, idxList));
    }

    @Test
    @Transactional
    void findReImgPathDtoByIdAndIdxList_WithNullDate_ThrowsException() {
        Long[] idxList = new Long[]{3L};
        assertThrows(IllegalArgumentException.class, ()
            -> realEstateQueryRepository.findReImgPathDtoByIdAndIdxList(null, idxList));
        Long realEstateId = 13L;
        assertThrows(IllegalArgumentException.class, ()
            -> realEstateQueryRepository.findReImgPathDtoByIdAndIdxList(realEstateId, null));
    }

    @Test
    @Transactional
    void findReImgPathDtoByIdAndIdxList_ReturnNullData(){
        Long[] idxList = new Long[]{3L};
        Long realEstateId = 13L;
        RealEstateImgPathDto dto =
            realEstateQueryRepository.findReImgPathDtoByIdAndIdxList(realEstateId, idxList);
        realEstateId = 0L;
        RealEstateImgPathDto dto2 =
            realEstateQueryRepository.findReImgPathDtoByIdAndIdxList(realEstateId, idxList);
        realEstateId = -1L;
        RealEstateImgPathDto dto3 =
            realEstateQueryRepository.findReImgPathDtoByIdAndIdxList(realEstateId, idxList);
        assertEquals(null, dto);
        assertEquals(null, dto2);
        assertEquals(null, dto3);
    }

    @Test
    @Transactional
    void findReImgPathDtoByIdAndIdxList_ReturnData(){
        int testSize = 15;
        Long lastId = 0L;
        String fileName = "test-img.jpg";
        for (int i = 1; i <= testSize; i++) {
            RealEstate testData = new RealEstate();
            testData.setImage3(fileName);
            lastId = entityManager.persist(testData).getId();
        }
        entityManager.flush();
        entityManager.clear();
        Long[] idxList = new Long[]{3L};
        RealEstateImgPathDto dto =
            realEstateQueryRepository.findReImgPathDtoByIdAndIdxList(lastId, idxList);

        assertEquals(lastId, dto.getId());
        assertEquals(fileName, dto.getImage3());
    }

    @Test
    @Transactional
    void findByIdWithoutImg_WithValidDate_FindSuccessfully() {
        Long realEstateId = 13L;
        assertDoesNotThrow(()
            -> realEstateQueryRepository.findByIdWithoutImg(realEstateId));
    }

    @Test
    @Transactional
    void findByIdWithoutImg_WithNullDate_ThrowsException() {
        assertThrows(IllegalArgumentException.class, ()
            -> realEstateQueryRepository.findByIdWithoutImg(null));
    }

    @Test
    @Transactional
    void findByIdWithoutImg_ReturnNullData(){
        Long realEstateId = 13L;
        RealEstateWithoutImgDto dto =
            realEstateQueryRepository.findByIdWithoutImg(realEstateId);
        realEstateId = 0L;
        RealEstateWithoutImgDto dto2 =
            realEstateQueryRepository.findByIdWithoutImg(realEstateId);
        realEstateId = -1L;
        RealEstateWithoutImgDto dto3 =
            realEstateQueryRepository.findByIdWithoutImg(realEstateId);
        assertEquals(null, dto);
        assertEquals(null, dto2);
        assertEquals(null, dto3);
    }

    @Test
    @Transactional
    void findByIdWithoutImg_ReturnData(){
        int testSize = 15;
        Long lastId = 0L;
        for (int i = 1; i <= testSize; i++) {
            RealEstate testData = new RealEstate();
            lastId = entityManager.persist(testData).getId();
        }
        entityManager.flush();
        entityManager.clear();
        RealEstateWithoutImgDto dto =
            realEstateQueryRepository.findByIdWithoutImg(lastId);
        assertEquals(lastId, dto.getId());
    }

    ////
    @Test
    @Transactional
    void findByOffset_WithValidDate_FindSuccessfully() {
        assertDoesNotThrow(()
            -> realEstateQueryRepository.findByOffset(0L, 40L));
    }

    @Test
    @Transactional
    void findByOffset_WithNullDate_ThrowsException() {
        assertThrows(IllegalArgumentException.class, ()
            -> realEstateQueryRepository.findByOffset(0L, null));
        assertThrows(IllegalArgumentException.class, ()
            -> realEstateQueryRepository.findByOffset(null, 40L));
    }

    @Test
    @Transactional
    void findByOffset_ReturnNullData(){
        List<RealEstate> result =
            realEstateQueryRepository.findByOffset(0L, 40L);
        List<RealEstate> result2 =
            realEstateQueryRepository.findByOffset(40L, 1L);
        List<RealEstate> result3 =
            realEstateQueryRepository.findByOffset(-1L, 0L);
        List<RealEstate> emptyList = Collections.emptyList();
        assertEquals(emptyList, result);
        assertEquals(emptyList, result2);
        assertEquals(emptyList, result3);
    }

    @Test
    @Transactional
    void findByOffset_ReturnData(){
        int testSize = 80;
        Long offset = 40L;
        Long lastId = 0L;
        for (int i = 0; i < testSize; i++) {
            RealEstate testData = new RealEstate();
            lastId = entityManager.persist(testData).getId();
        }
        entityManager.flush();
        entityManager.clear();
        List<RealEstate> result =
            realEstateQueryRepository.findByOffset(offset, lastId);
        List<RealEstate> result2 =
            realEstateQueryRepository.findByOffset((long) (testSize - 1), lastId);
        List<RealEstate> result3 =
            realEstateQueryRepository.findByOffset((long) testSize, lastId);
        List<RealEstate> emptyList = new ArrayList<>();
        assertEquals(offset, result.size());
        assertEquals(lastId - 39, result.get(0).getId());
        assertEquals(lastId, result.get(39).getId());
        assertEquals(1, result2.size());
        assertEquals(lastId, result2.get(0).getId());
        assertEquals(0, result3.size());
    }
    ////
    @Test
    @Transactional
    void findByOffsetWidthKeySet_WithValidDate_FindSuccessfully() {
        assertDoesNotThrow(()
            -> realEstateQueryRepository.findByOffsetWidthKeySet(1000L, 0L, 40L));
    }

    @Test
    @Transactional
    void findByOffsetWidthKeySet_WithNullDate_ThrowsException() {
        assertThrows(IllegalArgumentException.class, ()
            -> realEstateQueryRepository.findByOffsetWidthKeySet(100L, 0L, null));
        assertThrows(IllegalArgumentException.class, ()
            -> realEstateQueryRepository.findByOffsetWidthKeySet(10L, null, 40L));
        assertThrows(IllegalArgumentException.class, ()
            -> realEstateQueryRepository.findByOffsetWidthKeySet(null, 0L, 40L));
    }

    @Test
    @Transactional
    void findByOffsetWidthKeySet_ReturnNullData(){
        List<RealEstate> result =
            realEstateQueryRepository.findByOffsetWidthKeySet(10000000L, 0L, 40L);
        List<RealEstate> result2 =
            realEstateQueryRepository.findByOffsetWidthKeySet(10000000L, 40L, 1L);
        List<RealEstate> result3 =
            realEstateQueryRepository.findByOffsetWidthKeySet(10000000L, 9999L, 1L);
        List<RealEstate> emptyList = new ArrayList<>();
        assertEquals(emptyList, result);
        assertEquals(emptyList, result2);
        assertEquals(emptyList, result3);
    }

    @Test
    @Transactional
    void findByOffsetWidthKeySet_ReturnData(){
        int testSize = 80;
        Long lastId = 0L;
        for (int i = 0; i < testSize; i++) {
            RealEstate testData = new RealEstate();
            lastId = entityManager.persist(testData).getId();
        }
        entityManager.flush();
        entityManager.clear();
        List<RealEstate> result =
            realEstateQueryRepository.findByOffsetWidthKeySet(lastId - 39, 0L, 40L);
        List<RealEstate> result2 =
            realEstateQueryRepository.findByOffsetWidthKeySet(lastId - 10, 1L, 1L);
        List<RealEstate> result3 =
            realEstateQueryRepository.findByOffsetWidthKeySet(lastId, 1L,40L);
        List<RealEstate> emptyList = new ArrayList<>();
        assertEquals(40, result.size());
        assertEquals(lastId - 39, result.get(0).getId());
        assertEquals(lastId, result.get(39).getId());
        assertEquals(1, result2.size());
        assertEquals(lastId - 9, result2.get(0).getId());
        assertEquals(0, result3.size());
    }
}