package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.dto.RealEstateImgPathDto;
import com.jjeong.kiwi.model.RealEstate;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {
    void deleteById(Long id);

}
