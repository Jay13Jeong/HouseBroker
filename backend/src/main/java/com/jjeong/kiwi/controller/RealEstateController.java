package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.annotaion.CommonResponseHeader;
import com.jjeong.kiwi.annotaion.ExceptionHandling;
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
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/realestate")
@RequiredArgsConstructor
public class RealEstateController {

    private final RealEstateService realEstateService;
    private static final Logger logger = LoggerFactory.getLogger(RealEstateController.class);
    private final Long defaultPageLimit = 40L;

    @GetMapping("/")
    @ExceptionHandling
    @CommonResponseHeader
    public ResponseEntity<List<RealEstateWithImgPathDto>> getRealEstates() {
        List<RealEstateWithImgPathDto> realEstates =
            realEstateService.getRealEstatesByOffset(0L, defaultPageLimit);
        realEstates.forEach(re -> appendsHATEOAS(re, re.getId()));
        return new ResponseEntity<>(realEstates, HttpStatus.OK);
    }

    @GetMapping("/key-set/{nextStartRealEstateId}")
    @ExceptionHandling
    @CommonResponseHeader
    public ResponseEntity<List<RealEstateWithImgPathDto>> getRealEstatesByKeySet(
        @PathVariable Long nextStartRealEstateId) {
        List<RealEstateWithImgPathDto> realEstates =
            realEstateService.getRealEstatesByKeySet(nextStartRealEstateId, defaultPageLimit);
        realEstates.forEach(re -> appendsHATEOAS(re, re.getId()));
        return new ResponseEntity<>(realEstates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ExceptionHandling
    @CommonResponseHeader
    public ResponseEntity<RealEstateWithoutImgDto> getRealEstateById(@PathVariable Long id) {
        RealEstateWithoutImgDto realEstate = realEstateService.getRealEstateWithoutImg(id);
        appendsHATEOAS(realEstate, id);
        return new ResponseEntity<>(realEstate, HttpStatus.OK);
    }

    @GetMapping("/{id}/detail")
    @ExceptionHandling
    @CommonResponseHeader
    public ResponseEntity<RealEstateWithImgPathDto> getRealEstateDetailById(@PathVariable Long id) {
        RealEstate realEstate = realEstateService.getRealEstateById(id);
        RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(realEstate);
        appendsHATEOAS(dto, id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/{id}/image/{index}")
    @ExceptionHandling
    @CommonResponseHeader
    public ResponseEntity<Resource> getRealEstateImage(
        @PathVariable Long id,
        @PathVariable Long index) {
        Resource resource = realEstateService.getImageResource(id, index);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @PermitCheck
    @PostMapping("/")
    @ExceptionHandling
    @CommonResponseHeader
    public ResponseEntity<RealEstateWithImgPathDto> createRealEstate(
        @ModelAttribute RealEstateDto realEstateDto) {
        RealEstate realEstate = realEstateService.createRealEstate(realEstateDto);
        RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(realEstate);
        appendsHATEOAS(dto, dto.getId());
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PermitCheck
    @DeleteMapping("/{id}/image/{index}")
    @ExceptionHandling
    @CommonResponseHeader
    public ResponseEntity<Void> deleteImage(@PathVariable Long id, @PathVariable Long index) {
        realEstateService.deleteImage(id, index);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PermitCheck
    @DeleteMapping("/{id}")
    @ExceptionHandling
    @CommonResponseHeader
    public ResponseEntity<Void> deleteRealEstate(@PathVariable Long id) {
        realEstateService.deleteRealEstate(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PermitCheck
    @PatchMapping("/{id}")
    @ExceptionHandling
    @CommonResponseHeader
    public ResponseEntity<RealEstateWithImgPathDto> updateRealEstate(@PathVariable Long id,
        @ModelAttribute RealEstateDto realEstateDto) {
        RealEstate realEstate = this.realEstateService.modifyRealEstate(id, realEstateDto);
        RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(realEstate);
        appendsHATEOAS(dto, dto.getId());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PermitCheck
    @PatchMapping("/{id}/sequence")
    @ExceptionHandling
    @CommonResponseHeader
    public ResponseEntity<RealEstateWithoutImgDto> updateSequence(@PathVariable Long id) {
        RealEstate realEstate = this.realEstateService.modifySequenceToLatest(id);
        RealEstateWithoutImgDto dto = new RealEstateWithoutImgDto(realEstate);
        appendsHATEOAS(dto, dto.getId());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PermitCheck
    @PatchMapping("/{id}/sold-out")
    @ExceptionHandling
    @CommonResponseHeader
    public ResponseEntity<RealEstateWithoutImgDto> updateRealEstateIsSoldOut(
        @PathVariable Long id,
        @RequestBody Map<String, Boolean> requestBody) {
        boolean soldOut = requestBody.get("soldout");
        RealEstate realEstate = this.realEstateService.modifyRealEstateIsSoldOut(id, soldOut);
        RealEstateWithoutImgDto dto = new RealEstateWithoutImgDto(realEstate);
        appendsHATEOAS(dto, dto.getId());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private void appendsHATEOAS(RepresentationModel inst, Long id) {
        inst.add(linkTo(methodOn(RealEstateController.class)
            .getRealEstateById(id)).withSelfRel());
        inst.add(linkTo(methodOn(RealEstateController.class)
            .getRealEstates()).withRel("collection"));
        inst.add(linkTo(methodOn(RealEstateController.class)
            .getRealEstatesByKeySet(id + 1)).withRel("key-set"));
    }
}
