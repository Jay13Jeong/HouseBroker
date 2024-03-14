package com.jjeong.kiwi.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.jjeong.kiwi.config.QueryDSLConfiguration;
import com.jjeong.kiwi.tool.HashProvider;
import com.jjeong.kiwi.model.Image;
import com.jjeong.kiwi.model.RealEstate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({ImageQueryRepository.class, QueryDSLConfiguration.class})
public class ImageQueryRepositoryTest {

    @Autowired
    private ImageQueryRepository imageQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private HashProvider hashProvider = new HashProvider();

    private final static Set<RealEstate> realEstates = new HashSet<>();

    @Test
    @Transactional
    public void testDeleteOldImages() {
        LocalDate cutoffDate = LocalDate.now().minusDays(30);
        int testSize = 20;

        for (int i = 0; i < testSize; i++){
            String dummyData = "dummy-data-" + i;
            Image image = new Image();
            image.setLastModified(cutoffDate.minusDays(1).atStartOfDay());
            image.setPendingDeletion(true);
            image.setEtagHash(hashProvider.getMurmurHash(dummyData));
            image.setExtraHash(hashProvider.getSHA256(dummyData));
            image.setFileName(dummyData);
            image.setRealEstates(realEstates);
            if (i != testSize - 1){
                entityManager.persist(image);
            } else {
                Image recentImage = image;
                recentImage.setLastModified(LocalDate.now().atStartOfDay());
                entityManager.persist(recentImage);
            }
        }
        entityManager.flush();
        entityManager.clear();

        // When
        long deletedCount = imageQueryRepository.deleteOldImages(cutoffDate);

        // Then
        assertEquals(19, deletedCount);
    }

    @Test
    @Transactional
    void deleteOldImages_WithValidDate_DeletesSuccessfully() {
        LocalDate testDate = LocalDate.of(2022, 1, 1);
        assertDoesNotThrow(() -> imageQueryRepository.deleteOldImages(testDate));
    }

    @Test
    @Transactional
    void deleteOldImages_WithNullDate_ThrowsException() {
        assertThrows(IllegalArgumentException.class, ()
            -> imageQueryRepository.deleteOldImages(null));
    }
}