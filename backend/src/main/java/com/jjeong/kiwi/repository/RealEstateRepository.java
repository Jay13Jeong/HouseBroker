package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.dto.RealEstateImgPathDto;
import com.jjeong.kiwi.model.RealEstate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {
    void deleteById(Long id);

    @Query("SELECT re FROM RealEstate re LEFT join fetch re.imageSlotState WHERE re.id = :id")
    Optional<RealEstate> findByIdWithImageSlotStates(Long id);

    @Query("SELECT DISTINCT re FROM RealEstate re LEFT JOIN FETCH re.imageSlotState")
    List<RealEstate> findAllWithImageSlotStates();

}
