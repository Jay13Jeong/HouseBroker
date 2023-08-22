package com.jjeong.kiwi.service;

import com.jjeong.kiwi.domain.RealEstate;
import com.jjeong.kiwi.domain.RealEstateDto;
import com.jjeong.kiwi.repository.RealEstateRepository;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
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
    private final String NO_IMG = "NO_IMG";

    @Value("${upload.path}") // 파일 업로드 경로 설정
    private String uploadPath;

    public List<RealEstate> getAllRealEstates() {
        return realEstateRepository.findAll();
    }

    public RealEstate createRealEstate(RealEstateDto realEstateDto) throws IOException {
        RealEstate realEstate = new RealEstate();
        realEstate.setTitle(realEstateDto.getTitle());
        realEstate.setDescription(realEstateDto.getDescription());
        realEstate.setPrice(realEstateDto.getPrice());
        realEstate.setSoldout(realEstateDto.isSoldout());
        realEstate.setArea(realEstateDto.getArea());
        realEstate.setAdministration_cost(realEstateDto.getAdministration_cost());
        realEstate.setDirection(realEstateDto.getDirection());
        realEstate.setLocation(realEstateDto.getLocation());
        realEstate.setNumber_of_cars_parked(realEstateDto.getNumber_of_cars_parked());
        realEstate.setRelay_object_type(realEstateDto.getRelay_object_type());
        realEstate.setTransaction_type(realEstateDto.getTransaction_type());
        realEstate.setResidence_availability_date(realEstateDto.getResidence_availability_date());
        realEstate.setAdministrative_agency_approval_date(realEstateDto.getAdministrative_agency_approval_date());
        realEstate.setAdministration_cost2(realEstateDto.getAdministration_cost2());
        realEstate.setLatitude(realEstateDto.getLatitude());
        realEstate.setLongitude(realEstateDto.getLongitude());
        if (realEstateDto.getImage() != null)
            realEstate.setImage(uploadImage(realEstateDto.getImage()));
        else
            realEstate.setImage(NO_IMG);
        if (realEstateDto.getImage2() != null)
            realEstate.setImage2(uploadImage(realEstateDto.getImage2()));
        else
            realEstate.setImage2(NO_IMG);
        if (realEstateDto.getImage3() != null)
            realEstate.setImage3(uploadImage(realEstateDto.getImage3()));
        else
            realEstate.setImage3(NO_IMG);
        if (realEstateDto.getImage4() != null)
            realEstate.setImage4(uploadImage(realEstateDto.getImage4()));
        else
            realEstate.setImage4(NO_IMG);
        if (realEstateDto.getImage5() != null)
            realEstate.setImage5(uploadImage(realEstateDto.getImage5()));
        else
            realEstate.setImage5(NO_IMG);
        if (realEstateDto.getImage6() != null)
            realEstate.setImage6(uploadImage(realEstateDto.getImage6()));
        else
            realEstate.setImage6(NO_IMG);
        if (realEstateDto.getImage7() != null)
            realEstate.setImage7(uploadImage(realEstateDto.getImage7()));
        else
            realEstate.setImage7(NO_IMG);
        if (realEstateDto.getImage8() != null)
            realEstate.setImage8(uploadImage(realEstateDto.getImage8()));
        else
            realEstate.setImage8(NO_IMG);
        if (realEstateDto.getImage9() != null)
            realEstate.setImage9(uploadImage(realEstateDto.getImage9()));
        else
            realEstate.setImage9(NO_IMG);
        if (realEstateDto.getImage10() != null)
            realEstate.setImage10(uploadImage(realEstateDto.getImage10()));
        else
            realEstate.setImage10(NO_IMG);
        return realEstateRepository.save(realEstate);
    }


    public void deleteRealEstate(Long id) {
        RealEstate realEstate = this.getRealEstateById(id);
        for (Long i = 1L; i <= 10L; i++){
            this.deleteImageByIndex(realEstate, i);
        }
        realEstateRepository.deleteById(id);
    }

    public RealEstate getRealEstateById(Long id) {
        return realEstateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("부동산을 찾을 수 없습니다."));
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

    public Resource getImageResource(Long imgId, Long index) throws IOException {
        RealEstate realEstate = this.getRealEstateById(imgId);
        Path imagePath = Paths.get(uploadPath, this.getImgNameByIndex(realEstate, index));
        Resource resource = new UrlResource(imagePath.toUri());

        if (!resource.exists()) {
            throw new IOException("Image not found for ID: " + imgId);
        }

        return resource;
    }


    public void modifyRealEstate(Long id, RealEstateDto realEstateDto) throws IOException {
        // 해당 ID를 가진 부동산 가져오기
        RealEstate realEstate = this.getRealEstateById(id);
        if (realEstate == null) {
            throw new IOException("게시글 id로 찾기 실패");
        }
        // 부동산 정보 업데이트
        if (!(realEstateDto.getImage() == null)) {
            //기존 이미지 삭제 및 새로운 이미지 저장.
            File file = new File(uploadPath + realEstate.getImage());
            if (file.exists()) file.delete();
            if (!(realEstateDto.getImage().isEmpty() || realEstateDto.getImage().getSize() == 0))
                realEstate.setImage(uploadImage(realEstateDto.getImage()));
        }
        if (!(realEstateDto.getImage2() == null)) {
            File file = new File(uploadPath + realEstate.getImage2());
            if (file.exists()) file.delete();
            if (!(realEstateDto.getImage2().isEmpty() || realEstateDto.getImage2().getSize() == 0))
                realEstate.setImage2(uploadImage(realEstateDto.getImage2()));
        }
        if (!(realEstateDto.getImage3() == null)) {
            File file = new File(uploadPath + realEstate.getImage3());
            if (file.exists()) file.delete();
            if (!(realEstateDto.getImage3().isEmpty() || realEstateDto.getImage3().getSize() == 0))
                realEstate.setImage3(uploadImage(realEstateDto.getImage3()));
        }
        if (!(realEstateDto.getImage4() == null)) {
            File file = new File(uploadPath + realEstate.getImage4());
            if (file.exists()) file.delete();
            if (!(realEstateDto.getImage4().isEmpty() || realEstateDto.getImage4().getSize() == 0))
                realEstate.setImage4(uploadImage(realEstateDto.getImage4()));
        }
        if (!(realEstateDto.getImage5() == null)) {
            File file = new File(uploadPath + realEstate.getImage5());
            if (file.exists()) file.delete();
            if (!(realEstateDto.getImage5().isEmpty() || realEstateDto.getImage5().getSize() == 0))
                realEstate.setImage5(uploadImage(realEstateDto.getImage5()));
        }
        if (!(realEstateDto.getImage6() == null)) {
            File file = new File(uploadPath + realEstate.getImage6());
            if (file.exists()) file.delete();
            if (!(realEstateDto.getImage6().isEmpty() || realEstateDto.getImage6().getSize() == 0))
                realEstate.setImage6(uploadImage(realEstateDto.getImage6()));
        }
        if (!(realEstateDto.getImage7() == null)) {
            File file = new File(uploadPath + realEstate.getImage7());
            if (file.exists()) file.delete();
            if (!(realEstateDto.getImage7().isEmpty() || realEstateDto.getImage7().getSize() == 0))
                realEstate.setImage7(uploadImage(realEstateDto.getImage7()));
        }
        if (!(realEstateDto.getImage8() == null)) {
            File file = new File(uploadPath + realEstate.getImage8());
            if (file.exists()) file.delete();
            if (!(realEstateDto.getImage8().isEmpty() || realEstateDto.getImage8().getSize() == 0))
                realEstate.setImage8(uploadImage(realEstateDto.getImage8()));
        }
        if (!(realEstateDto.getImage9() == null)) {
            File file = new File(uploadPath + realEstate.getImage9());
            if (file.exists()) file.delete();
            if (!(realEstateDto.getImage9().isEmpty() || realEstateDto.getImage9().getSize() == 0))
                realEstate.setImage9(uploadImage(realEstateDto.getImage9()));
        }
        if (!(realEstateDto.getImage10() == null)) {
            File file = new File(uploadPath + realEstate.getImage10());
            if (file.exists()) file.delete();
            if (!(realEstateDto.getImage10().isEmpty() || realEstateDto.getImage10().getSize() == 0))
                realEstate.setImage10(uploadImage(realEstateDto.getImage10()));
        }
        if (!(realEstateDto.getTitle() == null)) {
            realEstate.setTitle(realEstateDto.getTitle());
        }
        if (!(realEstateDto.getDescription() == null)) {
            realEstate.setDescription(realEstateDto.getDescription());
        }
        if (!(realEstateDto.getPrice() == null)) {
            realEstate.setPrice(realEstateDto.getPrice());
        }
        if (!(realEstateDto.getRelay_object_type() == null)) {
            realEstate.setRelay_object_type(realEstateDto.getRelay_object_type());
        }
        if (!(realEstateDto.getLocation() == null)) {
            realEstate.setLocation(realEstateDto.getLocation());
        }
        if (!(realEstateDto.getArea() == null)) {
            realEstate.setArea(realEstateDto.getArea());
        }
        if (!(realEstateDto.getTransaction_type() == null)) {
            realEstate.setTransaction_type(realEstateDto.getTransaction_type());
        }
        if (!(realEstateDto.getResidence_availability_date() == null)) {
            realEstate.setResidence_availability_date(realEstateDto.getResidence_availability_date());
        }
        if (!(realEstateDto.getAdministrative_agency_approval_date() == null)) {
            realEstate.setAdministrative_agency_approval_date(realEstateDto.getAdministrative_agency_approval_date());
        }
        if (!(realEstateDto.getNumber_of_cars_parked() == null)) {
            realEstate.setNumber_of_cars_parked(realEstateDto.getNumber_of_cars_parked());
        }
        if (!(realEstateDto.getDirection() == null)) {
            realEstate.setDirection(realEstateDto.getDirection());
        }
        if (!(realEstateDto.getAdministration_cost() == null)) {
            realEstate.setAdministration_cost(realEstateDto.getAdministration_cost());
        }
        if (!(realEstateDto.getAdministration_cost2() == null)) {
            realEstate.setAdministration_cost2(realEstateDto.getAdministration_cost2());
        }
        if (!(realEstateDto.getLatitude() == null)) {
            realEstate.setLatitude(realEstateDto.getLatitude());
        }
        if (!(realEstateDto.getLongitude() == null)) {
            realEstate.setLongitude(realEstateDto.getLongitude());
        }
        realEstateRepository.save(realEstate);
    }

    public void modifyRealEstateIsSoldOut(Long id, boolean soldout) throws IOException {
        RealEstate realEstate = this.getRealEstateById(id);
        if (realEstate == null) {
            throw new IOException("게시글 id로 찾기 실패");
        }
        realEstate.setSoldout(soldout);
        realEstateRepository.save(realEstate);
    }

    public void deleteImage(Long id, Long index) {
        RealEstate realEstate = this.getRealEstateById(id);
        this.deleteImageByIndex(realEstate, index);
    }

    private void deleteImageByIndex(RealEstate realEstate, Long index) {
        String imgName = getImgNameByIndex(realEstate, index);
        File file = new File(uploadPath + imgName);
        if (file.exists()) {
            file.delete();
        }
        switch (index.intValue()) {
            case 2:
                realEstate.setImage2(NO_IMG); break;
            case 3:
                realEstate.setImage3(NO_IMG); break;
            case 4:
                realEstate.setImage4(NO_IMG); break;
            case 5:
                realEstate.setImage5(NO_IMG); break;
            case 6:
                realEstate.setImage6(NO_IMG); break;
            case 7:
                realEstate.setImage7(NO_IMG); break;
            case 8:
                realEstate.setImage8(NO_IMG); break;
            case 9:
                realEstate.setImage9(NO_IMG); break;
            case 10:
                realEstate.setImage10(NO_IMG); break;
            default:
                realEstate.setImage(NO_IMG); break;
        }
    }

    private String getImgNameByIndex(RealEstate realEstate, Long index){
        String imgName = "";
        switch (index.intValue()) {
            case 2:
                imgName = realEstate.getImage2(); break;
            case 3:
                imgName = realEstate.getImage3(); break;
            case 4:
                imgName = realEstate.getImage4(); break;
            case 5:
                imgName = realEstate.getImage5(); break;
            case 6:
                imgName = realEstate.getImage6(); break;
            case 7:
                imgName = realEstate.getImage7(); break;
            case 8:
                imgName = realEstate.getImage8(); break;
            case 9:
                imgName = realEstate.getImage9(); break;
            case 10:
                imgName = realEstate.getImage10(); break;
            default:
                imgName = realEstate.getImage(); break;
        }
        return  imgName;
    }
}
