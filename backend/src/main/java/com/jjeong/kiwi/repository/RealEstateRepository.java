package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.domain.RealEstate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {
    boolean existsByTitle(String title);

    void deleteById(Long id);

    RealEstate findRealEstateById(Long id);
}
