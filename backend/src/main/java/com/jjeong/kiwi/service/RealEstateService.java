package com.jjeong.kiwi.service;

import com.jjeong.kiwi.domain.RealEstate;
import com.jjeong.kiwi.domain.RealEstateDto;
import com.jjeong.kiwi.repository.RealEstateRepository;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RealEstateService {
    private final RealEstateRepository realEstateRepository;

    @Value("${upload.path}") // 파일 업로드 경로 설정
    private String uploadPath;

    public List<RealEstate> getAllRealEstates() {
        return realEstateRepository.findAll();
    }

    public RealEstate createRealEstate(RealEstateDto realEstateDto) throws IOException {
        // Convert RealEstateDto to RealEstate entity
        RealEstate realEstate = new RealEstate();
        realEstate.setTitle(realEstateDto.getTitle());
        realEstate.setDescription(realEstateDto.getDescription());
        realEstate.setPrice(realEstateDto.getPrice());
//        System.out.println("createRealEstate-1");
//        System.out.println(realEstateDto.getImage());
        if (realEstateDto.getImage() != null)
            realEstate.setImage(uploadImage(realEstateDto.getImage()));
        else
            realEstate.setImage("NO_IMG");
//        System.out.println("createRealEstate-2");

        // Save the real estate in the database
        return realEstateRepository.save(realEstate);
    }

    public void deleteRealEstate(Long id) {
        RealEstate realEstate = this.getRealEstateById(id);
        File file = new File(uploadPath + realEstate.getImage());
        if (file.exists()) {
            file.delete();
        }
        realEstateRepository.deleteById(id);
    }

    public RealEstate getRealEstateById(Long id) {
        return realEstateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("부동산을 찾을 수 없습니다."));
    }

    public RealEstate saveRealEstate(RealEstate realEstate) {
        return realEstateRepository.save(realEstate);
    }

    public RealEstate getRealEstates(Long id) {
        return realEstateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("부동산을 찾을 수 없습니다."));
    }

    public String uploadImage(MultipartFile imageFile) throws IOException {
        // 업로드할 디렉토리 경로 생성
        File uploadDirectory = new File(uploadPath);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
        System.out.println(imageFile);
        // 파일명 중복 방지를 위한 UUID 생성
        String fileName = UUID.randomUUID().toString() + "-" + StringUtils.cleanPath(imageFile.getOriginalFilename());
        // 파일 저장 경로 생성
        Path filePath = uploadDirectory.toPath().resolve(fileName);
        try {
            // 파일을 지정된 경로에 저장
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("이미지 파일 업로드에 실패했습니다. " + e.getMessage());
        }
        // 파일의 이름 반환
        return fileName;
    }

    public Resource getImageResource(Long imgId) throws IOException {
        RealEstate realEstate = this.getRealEstateById(imgId);
        System.out.println(realEstate.getImage());
        Path imagePath = Paths.get(uploadPath, realEstate.getImage());
        Resource resource = new UrlResource(imagePath.toUri());

        if (!resource.exists()) {
            throw new IOException("Image not found for ID: " + imgId);
        }

        return resource;
    }

    public void ModifyRealEstateImage(Long id, MultipartFile img) {
        //이미지 수정하는 서비스 로직.
    }
}
