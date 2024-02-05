package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.model.RealEstate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {
    void deleteById(Long id);

}
