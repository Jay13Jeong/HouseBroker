package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.RealEstate;
import com.jjeong.kiwi.domain.RealEstateDto;
import com.jjeong.kiwi.repository.UserRepository;
import com.jjeong.kiwi.service.RealEstateService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/realestate")
@RequiredArgsConstructor
public class RealEstateController {
    private final RealEstateService realEstateService;
    private final UserService userService;
    private final int allowLevel = 10;

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
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> createRealEstate(@ModelAttribute RealEstateDto realEstateDto, HttpServletRequest request) {
        if (!userService.isAdminLevelUser(request.getCookies(), allowLevel))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("access deny");
        try {
//            System.out.println(realEstateDto);
            Long realEstateId = realEstateService.createRealEstate(realEstateDto).getId();
            return ResponseEntity.ok(realEstateId.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create real estate.");
        }
    }

    @DeleteMapping("/image/{id}/{index}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id, @PathVariable Long index, HttpServletRequest request) {
        if (!userService.isAdminLevelUser(request.getCookies(), allowLevel))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("access deny");
        realEstateService.deleteImage(id, index);
        return ResponseEntity.ok("부동산 이미지가 삭제되었습니다.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRealEstate(@PathVariable Long id, HttpServletRequest request) {
        if (!userService.isAdminLevelUser(request.getCookies(), allowLevel))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("access deny");
        realEstateService.deleteRealEstate(id);
        return ResponseEntity.ok("부동산 정보가 삭제되었습니다.");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateRealEstate(@PathVariable Long id,
                                                   @ModelAttribute RealEstateDto realEstateDto,
                                                   HttpServletRequest request){
        if (!userService.isAdminLevelUser(request.getCookies(), allowLevel))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("access deny");
        try {
            this.realEstateService.modifyRealEstate(id, realEstateDto);
            return ResponseEntity.ok("부동산이 수정되었습니다.");
        } catch ( Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed");
        }
    }

    @PatchMapping("/sequence/{id}")
    public ResponseEntity<String> updateSequence(@PathVariable Long id,
                                                   HttpServletRequest request){
        if (!userService.isAdminLevelUser(request.getCookies(), allowLevel))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("access deny");
        try {
            this.realEstateService.modifySequence(id);
            return ResponseEntity.ok("부동산이 순서가 수정되었습니다.");
        } catch ( Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("부동산 순서 Failed");
        }
    }

    @PatchMapping("/soldout/{id}")
    public ResponseEntity<String> updateRealEstateIsSoldOut(@PathVariable Long id,
                                                            @RequestBody Map<String, Boolean> requestBody,
                                                            HttpServletRequest request){
        if (!userService.isAdminLevelUser(request.getCookies(), allowLevel))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("access deny");
        try {
            boolean soldout = requestBody.get("soldout");
            this.realEstateService.modifyRealEstateIsSoldOut(id, soldout);
            return ResponseEntity.ok("거래여부가 수정되었습니다.");
        } catch ( Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed");
        }
    }

}
