package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.RealEstate;
import com.jjeong.kiwi.domain.RealEstateDto;
import com.jjeong.kiwi.service.RealEstateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/realestate")
@RequiredArgsConstructor
public class RealEstateController {
    private final RealEstateService realEstateService;

    @GetMapping("/")
    public List<RealEstate> getRealEstates(Model model) {
        List<RealEstate> realEstates = realEstateService.getAllRealEstates();
        model.addAttribute("realEstates", realEstates);
        return realEstates;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RealEstate> getRealEstateById(@PathVariable Long id) {
        RealEstate realEstate = realEstateService.getRealEstates(id);

        return new ResponseEntity<>(realEstate, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<String> createRealEstate(@ModelAttribute RealEstateDto realEstateDto) {
        try {
//            System.out.println("createRealEstate");
            realEstateService.createRealEstate(realEstateDto);
            return ResponseEntity.ok("Real estate created successfully.");
        } catch (Exception e) {
//            System.out.println("not createRealEstate");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create real estate.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRealEstate(@PathVariable Long id) {
        realEstateService.deleteRealEstate(id);
        return ResponseEntity.ok("부동산이 삭제되었습니다.");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateRealEstate(@PathVariable Long id, @RequestBody RealEstateDto realEstateDto) throws IOException {
        // DTO에서 필요한 정보 가져오기
        Long realEstateId = id;
        String title = realEstateDto.getTitle();
        String description = realEstateDto.getDescription();
        int price = realEstateDto.getPrice();
        MultipartFile image = realEstateDto.getImage();

        // 해당 ID를 가진 부동산 가져오기
        RealEstate realEstate = realEstateService.getRealEstateById(realEstateId);
        if (realEstate == null) {
            return ResponseEntity.badRequest().body("해당하는 부동산이 존재하지 않습니다.");
        }

        // 부동산 정보 업데이트
        realEstate.setTitle(title);
        realEstate.setDescription(description);
        realEstate.setPrice(price);
        realEstate.setImage(realEstateService.uploadImage(image));
        realEstateService.saveRealEstate(realEstate);
        return ResponseEntity.ok("부동산이 수정되었습니다.");
    }
}
