package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.annotaion.PermitCheck;
import com.jjeong.kiwi.dto.RealEstateWithImgPathDto;
import com.jjeong.kiwi.dto.RealEstateWithoutImgDto;
import com.jjeong.kiwi.model.RealEstate;
import com.jjeong.kiwi.dto.RealEstateDto;
import com.jjeong.kiwi.service.RealEstateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
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
    public ResponseEntity<List<RealEstateWithImgPathDto>> getRealEstates() {
        List<RealEstateWithImgPathDto> realEstates = realEstateService.getAllRealEstates();
        realEstates.forEach(re -> appendsHATEOAS(re, re.getId()));
        return new ResponseEntity<>(realEstates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RealEstateWithoutImgDto> getRealEstateById(@PathVariable Long id) {
        RealEstateWithoutImgDto realEstate = realEstateService.getRealEstateWithoutImg(id);
        appendsHATEOAS(realEstate, id);
        return new ResponseEntity<>(realEstate, HttpStatus.OK);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<RealEstateWithImgPathDto> getRealEstateDetailById(@PathVariable Long id) {
        RealEstate realEstate = realEstateService.getRealEstateById(id);
        RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(realEstate);
        appendsHATEOAS(dto, id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/{id}/image/{index}")
    public ResponseEntity<Resource> getRealEstateImage(@PathVariable Long id, @PathVariable Long index) {
        try {
            Resource resource = realEstateService.getImageResource(id, index);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(resource,headers,HttpStatus.OK);
        } catch (IOException e) {
            logger.error("getRealEstateImage", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PermitCheck
    @PostMapping("/")
    public ResponseEntity<RealEstateWithImgPathDto> createRealEstate(@ModelAttribute RealEstateDto realEstateDto) {
        try {
            RealEstate realEstate = realEstateService.createRealEstate(realEstateDto);
            RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(realEstate);
            appendsHATEOAS(dto, dto.getId());
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("createRealEstate", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PermitCheck
    @DeleteMapping("/{id}/image/{index}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id, @PathVariable Long index) {
        realEstateService.deleteImage(id, index);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PermitCheck
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRealEstate(@PathVariable Long id) {
        realEstateService.deleteRealEstate(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PermitCheck
    @PatchMapping("/{id}")
    public ResponseEntity<RealEstateWithImgPathDto> updateRealEstate(@PathVariable Long id,
                                                   @ModelAttribute RealEstateDto realEstateDto){
        try {
            RealEstate realEstate = this.realEstateService.modifyRealEstate(id, realEstateDto);
            RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(realEstate);
            appendsHATEOAS(dto, dto.getId());
            return new ResponseEntity<>(dto,HttpStatus.OK);
        } catch ( Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PermitCheck
    @PatchMapping("/{id}/sequence")
    public ResponseEntity<RealEstateWithoutImgDto> updateSequence(@PathVariable Long id){
        try {
            RealEstate realEstate = this.realEstateService.modifySequenceToLatest(id);
            RealEstateWithoutImgDto dto = new RealEstateWithoutImgDto(realEstate);
            appendsHATEOAS(dto, dto.getId());
            return new ResponseEntity<>(dto,HttpStatus.OK);
        } catch ( Exception e) {
            logger.error("updateSequence", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PermitCheck
    @PatchMapping("/{id}/sold-out")
    public ResponseEntity<RealEstateWithoutImgDto> updateRealEstateIsSoldOut(@PathVariable Long id,
                                                            @RequestBody Map<String, Boolean> requestBody){
        try {
            boolean soldout = requestBody.get("soldout");
            RealEstate realEstate = this.realEstateService.modifyRealEstateIsSoldOut(id, soldout);
            RealEstateWithoutImgDto dto = new RealEstateWithoutImgDto(realEstate);
            appendsHATEOAS(dto, dto.getId());
            return new ResponseEntity<>(dto,HttpStatus.OK);
        } catch ( Exception e) {
            logger.error("updateRealEstateIsSoldOut", e);
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void appendsHATEOAS(RepresentationModel inst, Long id, Long... imageIdx){
//        Long imgIdx = imageIdx.length > 0 ? imageIdx[0] : 1;
        inst.add(linkTo(methodOn(RealEstateController.class).getRealEstateById(id)).withSelfRel());
//        inst.add(WebMvcLinkBuilder.linkTo(RealEstateController.class).slash(id).slash("image/" + imgIdx).withRel("image"));
        inst.add(linkTo(methodOn(RealEstateController.class).getRealEstates()).withRel("collection"));
    }
}
