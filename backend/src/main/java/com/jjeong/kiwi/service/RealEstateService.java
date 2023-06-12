package com.jjeong.kiwi.service;

import com.jjeong.kiwi.domain.RealEstate;
import com.jjeong.kiwi.domain.RealEstateDto;
import com.jjeong.kiwi.repository.RealEstateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RealEstateService {
    private final RealEstateRepository realEstateRepository;

    public List<RealEstate> getAllRealEstates() {
        return realEstateRepository.findAll();
    }

    public void createRealEstate(RealEstateDto realEstateDto) {
        // Convert RealEstateDto to RealEstate entity
        RealEstate realEstate = new RealEstate();
        realEstate.setTitle(realEstateDto.getTitle());
        realEstate.setDescription(realEstateDto.getDescription());
        realEstate.setPrice(realEstateDto.getPrice());
        realEstate.setImage(realEstateDto.getImage());

        // Save the real estate in the database
        realEstateRepository.save(realEstate);
    }

    public void deleteRealEstate(Long id) {
        realEstateRepository.deleteById(id);
    }

    public RealEstate getRealEstateById(Long id) {
        return realEstateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("부동산을 찾을 수 없습니다."));
    }

    public RealEstate saveRealEstate(RealEstate realEstate) {
        return realEstateRepository.save(realEstate);
    }
}
