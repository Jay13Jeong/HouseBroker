package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.annotaion.PermitCheck;
import com.jjeong.kiwi.model.RealEstate;
import com.jjeong.kiwi.dto.RealEstateDto;
import com.jjeong.kiwi.service.RealEstateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/realestate")
@RequiredArgsConstructor
public class RealEstateController {
    private final RealEstateService realEstateService;
    private static final Logger logger = LoggerFactory.getLogger(RealEstateController.class);

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

    @GetMapping("/image/{id}/{index}")
    public ResponseEntity<Resource> getRealEstateImage(@PathVariable Long id, @PathVariable Long index) {
        try {
            Resource resource = realEstateService.getImageResource(id, index);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException e) {
            logger.error("getRealEstateImage", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PermitCheck
    @PostMapping("/")
    public ResponseEntity<String> createRealEstate(@ModelAttribute RealEstateDto realEstateDto) {
        try {
            Long realEstateId = realEstateService.createRealEstate(realEstateDto).getId();
            return ResponseEntity.ok(realEstateId.toString());
        } catch (Exception e) {
            logger.error("createRealEstate", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create real estate.");
        }
    }

    @PermitCheck
    @DeleteMapping("/image/{id}/{index}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id, @PathVariable Long index) {
        realEstateService.deleteImage(id, index);
        return ResponseEntity.ok("부동산 이미지가 삭제되었습니다.");
    }

    @PermitCheck
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRealEstate(@PathVariable Long id) {
        realEstateService.deleteRealEstate(id);
        return ResponseEntity.ok("부동산 정보가 삭제되었습니다.");
    }

    @PermitCheck
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateRealEstate(@PathVariable Long id,
                                                   @ModelAttribute RealEstateDto realEstateDto){
        try {
            this.realEstateService.modifyRealEstate(id, realEstateDto);
            return ResponseEntity.ok("부동산이 수정되었습니다.");
        } catch ( Exception e) {
            logger.error("updateRealEstate", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed");
        }
    }

    @PermitCheck
    @PatchMapping("/sequence/{id}")
    public ResponseEntity<String> updateSequence(@PathVariable Long id){
        try {
            this.realEstateService.modifySequence(id);
            return ResponseEntity.ok("부동산이 순서가 수정되었습니다.");
        } catch ( Exception e) {
            logger.error("updateSequence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("부동산 순서 Failed");
        }
    }

    @PermitCheck
    @PatchMapping("/soldout/{id}")
    public ResponseEntity<String> updateRealEstateIsSoldOut(@PathVariable Long id,
                                                            @RequestBody Map<String, Boolean> requestBody){
        try {
            boolean soldout = requestBody.get("soldout");
            this.realEstateService.modifyRealEstateIsSoldOut(id, soldout);
            return ResponseEntity.ok("거래여부가 수정되었습니다.");
        } catch ( Exception e) {
            logger.error("updateRealEstateIsSoldOut", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed");
        }
    }

}
