package com.jjeong.kiwi.service;

import com.jjeong.kiwi.dto.RealEstateImgPathDto;
import com.jjeong.kiwi.dto.RealEstateWithImgPathDto;
import com.jjeong.kiwi.dto.RealEstateWithoutImgDto;
import com.jjeong.kiwi.model.RealEstate;
import com.jjeong.kiwi.dto.RealEstateDto;
import com.jjeong.kiwi.repository.RealEstateQueryRepository;
import com.jjeong.kiwi.repository.RealEstateRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RealEstateService {
    private final RealEstateRepository realEstateRepository;
    private final RealEstateQueryRepository realEstateQueryRepository;
    private final String NO_IMG = "NO_IMG";
    private static final Map<String, Boolean> allowedMimeTypes = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(RealEstateService.class);
    private final RedisTemplate<String, Object> redisTemp;
//    private final String REAL_ESTATES_REDIS_KEY = "realEstates";

    static { // 한번만 실행하도록.
        allowedMimeTypes.put("image/jpeg", true);
        allowedMimeTypes.put("image/png", true);
        allowedMimeTypes.put("image/gif", true);
    }

    private static String UPLOAD_DIR_PATH = System.getenv("UPLOAD_DIR");

//    @Cacheable(cacheNames = "getAllRealEst ates", key = "#root.target + #root.methodName", sync = false, cacheManager = "redisCacheMgr")
    public List<RealEstateWithImgPathDto> getAllRealEstates() {

//        List<RealEstateWithImgPathDto> realEstatesCached = (List<RealEstateWithImgPathDto>) redisTemp.opsForValue().get(REAL_ESTATES_REDIS_KEY);
//        if (realEstatesCached != null)
//            return realEstatesCached;
        List<RealEstate> realEstatesModel = realEstateRepository.findAll();
        if (realEstatesModel == null) {
            throw new RuntimeException("getAllRealEstates:not found");
        }
        List<RealEstateWithImgPathDto> realEstates = new ArrayList<>();
        realEstatesModel.forEach(model -> realEstates.add(new RealEstateWithImgPathDto(model)));
//        redisTemp.opsForValue().set(REAL_ESTATES_REDIS_KEY, realEstates);
        return realEstates;

    }

    @Transactional
    public RealEstate createRealEstate(RealEstateDto realEstateDto) throws IOException {
        if (!checkMimeType(realEstateDto)) {
            logger.error("허용하지 않은 이미지 Mime 파일");
            throw new RuntimeException();
        }

        RealEstate realEstate = mapDtoToEntity(realEstateDto);
        List<String> images = uploadImages(realEstateDto);
        setImageNames(realEstate, images);

//        redisTemp.delete(REAL_ESTATES_REDIS_KEY);
        realEstate = realEstateRepository.save(realEstate);
        redisTemp.delete("realEstate:id:" + realEstate.getId());

        return realEstate;
    }

    private RealEstate mapDtoToEntity(RealEstateDto realEstateDto) {
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

        return realEstate;
    }

    private List<String> uploadImages(RealEstateDto realEstateDto) throws IOException {
        List<String> images = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            MultipartFile image = getImageByIndex(realEstateDto, i);
            if (image != null) {
                images.add(uploadImage(image));
            } else {
                images.add(NO_IMG);
            }
        }
        return images;
    }

    private MultipartFile getImageByIndex(RealEstateDto realEstateDto, int index) {
        switch (index) {
            case 1: return realEstateDto.getImage();
            case 2: return realEstateDto.getImage2();
            case 3: return realEstateDto.getImage3();
            case 4: return realEstateDto.getImage4();
            case 5: return realEstateDto.getImage5();
            case 6: return realEstateDto.getImage6();
            case 7: return realEstateDto.getImage7();
            case 8: return realEstateDto.getImage8();
            case 9: return realEstateDto.getImage9();
            case 10: return realEstateDto.getImage10();
            default: return null;
        }
    }

    private void setImageNames(RealEstate realEstate, List<String> images) {
        for (int i = 1; i <= 10; i++) {
            setRealEstateImageByIndex(realEstate, i, images.get(i - 1));
        }
    }

    private void setRealEstateImageByIndex(RealEstate realEstate, int index, String imageName) {
        switch (index) {
            case 1: realEstate.setImage(imageName); break;
            case 2: realEstate.setImage2(imageName); break;
            case 3: realEstate.setImage3(imageName); break;
            case 4: realEstate.setImage4(imageName); break;
            case 5: realEstate.setImage5(imageName); break;
            case 6: realEstate.setImage6(imageName); break;
            case 7: realEstate.setImage7(imageName); break;
            case 8: realEstate.setImage8(imageName); break;
            case 9: realEstate.setImage9(imageName); break;
            case 10: realEstate.setImage10(imageName); break;
        }
    }

    public void deleteRealEstate(Long id) {
        RealEstateImgPathDto realEstateImgPathDto = this.getRealEstateImgPathDtoById(id);
        for (Long i = 1L; i <= 10L; i++){
            this.deleteImageByIndex(realEstateImgPathDto, i);
        }

//        redisTemp.delete(REAL_ESTATES_REDIS_KEY);
        redisTemp.delete("realEstate:id:" + id);

        realEstateRepository.deleteById(id);
    }

    public RealEstate getRealEstateById(Long id) {
        String redisKey = "realEstate:id:" + id;
//        redisTemp.delete(redisKey);
        RealEstate realEstateCached = (RealEstate) redisTemp.opsForValue().get(redisKey);
        if (realEstateCached != null)
            return realEstateCached;
        Optional<RealEstate> realEstateOptional = realEstateRepository.findById(id);
        if (realEstateOptional == null){
            throw new RuntimeException("getRealEstateById:부동산을 찾을 수 없습니다.");
        }
        RealEstate realEstate = realEstateOptional.get();
        redisTemp.opsForValue().set(redisKey, realEstate);
        return realEstateOptional.get();
    }

    public RealEstateWithoutImgDto getRealEstateWithoutImg(Long id) {
        String redisKey = "ealEstateWithoutImgDto:id:" + id;
//        redisTemp.delete(redisKey);
        RealEstateWithoutImgDto cachedDto = (RealEstateWithoutImgDto) redisTemp.opsForValue().get(redisKey);
        if (cachedDto != null)
            return cachedDto;
        RealEstateWithoutImgDto result = this.realEstateQueryRepository.findByIdWithoutImg(id);
        if (result == null){
            throw new RuntimeException("getRealEstates:notfound");
        }
        redisTemp.opsForValue().set(redisKey, result);
        return result;
    }

    public String uploadImage(MultipartFile imageFile) {
        // 업로드할 디렉토리 경로 생성
        File uploadDirectory = new File(UPLOAD_DIR_PATH);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
//        System.out.println(imageFile);
        // 파일명 중복 방지를 위한 UUID 생성
        String fileName = UUID.randomUUID().toString() + "-" + StringUtils.cleanPath(imageFile.getOriginalFilename());
        // 파일 저장 경로 생성
        Path filePath = uploadDirectory.toPath().resolve(fileName);
        try {
            // 파일을 지정된 경로에 저장
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("uploadImage:이미지 파일 업로드에 실패했습니다.", e);
            throw new RuntimeException();
        }
        // 파일의 이름 반환
        return fileName;
    }

    public Resource getImageResource(Long realEstateId, Long index) throws IOException {
        RealEstateImgPathDto realEstateImgPathDto = this.getRealEstateImgPathDtoById(realEstateId, index);
        Path imagePath = Paths.get(UPLOAD_DIR_PATH, this.getImgNameByIndex(realEstateImgPathDto, index));
        if (imagePath == null){
            logger.error("getImageResource:Image Path not found:" + realEstateId + ":idx:" + index);
            throw new RuntimeException();
        }
        Resource resource = new UrlResource(imagePath.toUri());
        if (!resource.exists()) {
            logger.error("getImageResource:Image not found for ID:" + realEstateId + ":idx:" + index);
            throw new IOException();
        }

        return resource;
    }

    private RealEstateImgPathDto getRealEstateImgPathDtoById(Long realEstateId, Long... idxList) {
        RealEstateImgPathDto result = realEstateQueryRepository.findReImgPathDtoByIdAndIdxList(realEstateId, idxList);
        if (false){ //exists 추가 필요.
            logger.error("getRealEstateImgPathDtoById:not found");
            throw new RuntimeException("getRealEstateImgPathDtoById:not found") {};
        }
        return result;
    }

    public RealEstate modifyRealEstate(Long id, RealEstateDto realEstateDto) throws IOException {
        if (!checkMimeType(realEstateDto)) {
            throw new IOException("허용하지않은 이미지 Mime파일");
        }
        // 해당 ID를 가진 부동산 가져오기
        RealEstate realEstate = this.getRealEstateById(id);
        if (realEstate == null) {
            throw new IOException("게시글 id로 찾기 실패");
        }

        // 부동산 이미지 정보 업데이트
        updateImages(realEstate, realEstateDto);
        // 부동산 정보 업데이트
        realEstate.setTitle(realEstateDto.getTitle());
        realEstate.setDescription(realEstateDto.getDescription());
        realEstate.setPrice(realEstateDto.getPrice());
        realEstate.setRelay_object_type(realEstateDto.getRelay_object_type());
        realEstate.setLocation(realEstateDto.getLocation());
        realEstate.setArea(realEstateDto.getArea());
        realEstate.setTransaction_type(realEstateDto.getTransaction_type());
        realEstate.setResidence_availability_date(realEstateDto.getResidence_availability_date());
        realEstate.setAdministrative_agency_approval_date(realEstateDto.getAdministrative_agency_approval_date());
        realEstate.setNumber_of_cars_parked(realEstateDto.getNumber_of_cars_parked());
        realEstate.setDirection(realEstateDto.getDirection());
        realEstate.setAdministration_cost(realEstateDto.getAdministration_cost());
        realEstate.setAdministration_cost2(realEstateDto.getAdministration_cost2());
        realEstate.setLatitude(realEstateDto.getLatitude());
        realEstate.setLongitude(realEstateDto.getLongitude());

        realEstate = realEstateRepository.save(realEstate);
//        redisTemp.delete(REAL_ESTATES_REDIS_KEY);
        redisTemp.opsForValue().set("realEstate:id:" + realEstate.getId(), realEstate);

        return realEstate;
    }

    private void updateImages(RealEstate realEstate, RealEstateDto realEstateDto) throws IOException {
        List<MultipartFile> imageList = Arrays.asList(
                realEstateDto.getImage(), realEstateDto.getImage2(), realEstateDto.getImage3(),
                realEstateDto.getImage4(), realEstateDto.getImage5(), realEstateDto.getImage6(),
                realEstateDto.getImage7(), realEstateDto.getImage8(), realEstateDto.getImage9(),
                realEstateDto.getImage10()
        );

        for (int i = 0; i < imageList.size(); i++) {
            MultipartFile image = imageList.get(i);
            String existingImage = getImageByIndex(realEstate, i);

            if (existingImage != null) {
                File file = new File(UPLOAD_DIR_PATH + existingImage);
                if (file.exists()) {
                    file.delete();
                }
            }

            if (!(image == null || image.isEmpty() || image.getSize() == 0)) {
                setImageByIndex(realEstate, i, uploadImage(image));
            }
        }
    }

    private String getImageByIndex(RealEstate realEstate, int index) {
        switch (index) {
            case 0: return realEstate.getImage();
            case 1: return realEstate.getImage2();
            case 2: return realEstate.getImage3();
            case 3: return realEstate.getImage4();
            case 4: return realEstate.getImage5();
            case 5: return realEstate.getImage6();
            case 6: return realEstate.getImage7();
            case 7: return realEstate.getImage8();
            case 8: return realEstate.getImage9();
            case 9: return realEstate.getImage10();
            default: return null;
        }
    }

    private void setImageByIndex(RealEstate realEstate, int index, String imageName) {
        switch (index) {
            case 0: realEstate.setImage(imageName); break;
            case 1: realEstate.setImage2(imageName); break;
            case 2: realEstate.setImage3(imageName); break;
            case 3: realEstate.setImage4(imageName); break;
            case 4: realEstate.setImage5(imageName); break;
            case 5: realEstate.setImage6(imageName); break;
            case 6: realEstate.setImage7(imageName); break;
            case 7: realEstate.setImage8(imageName); break;
            case 8: realEstate.setImage9(imageName); break;
            case 9: realEstate.setImage10(imageName); break;
        }
    }

    public RealEstate modifyRealEstateIsSoldOut(Long id, boolean soldout) throws IOException {
        RealEstate realEstate = this.getRealEstateById(id);
        if (realEstate == null) {
            throw new IOException("게시글 id로 찾기 실패");
        }
        realEstate.setSoldout(soldout);
        realEstate = realEstateRepository.save(realEstate);
//        redisTemp.delete(REAL_ESTATES_REDIS_KEY);
        redisTemp.opsForValue().set("realEstate:id:" + realEstate.getId(), realEstate);

        return realEstate;
    }

    public void deleteImage(Long id, Long index) {
        RealEstateImgPathDto realEstateImgPathDto = this.getRealEstateImgPathDtoById(id);
        this.deleteImageByIndex(realEstateImgPathDto, index);
    }

    private void deleteImageByIndex(RealEstateImgPathDto realEstate, Long index) {
        String imgName = getImgNameByIndex(realEstate, index);
        File file = new File(UPLOAD_DIR_PATH + imgName);
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

    private String getImgNameByIndex(RealEstateImgPathDto realEstate, Long index){
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

    private boolean checkMimeType(RealEstateDto rDto){
        if (rDto.getImage() != null && allowedMimeTypes.get(rDto.getImage().getContentType()) == null)
            return false;
        if (rDto.getImage2() != null && allowedMimeTypes.get(rDto.getImage2().getContentType()) == null)
            return false;
        if (rDto.getImage3() != null && allowedMimeTypes.get(rDto.getImage3().getContentType()) == null)
            return false;
        if (rDto.getImage4() != null && allowedMimeTypes.get(rDto.getImage4().getContentType()) == null)
            return false;
        if (rDto.getImage5() != null && allowedMimeTypes.get(rDto.getImage5().getContentType()) == null)
            return false;
        if (rDto.getImage6() != null && allowedMimeTypes.get(rDto.getImage6().getContentType()) == null)
            return false;
        if (rDto.getImage7() != null && allowedMimeTypes.get(rDto.getImage7().getContentType()) == null)
            return false;
        if (rDto.getImage8() != null && allowedMimeTypes.get(rDto.getImage8().getContentType()) == null)
            return false;
        if (rDto.getImage9() != null && allowedMimeTypes.get(rDto.getImage9().getContentType()) == null)
            return false;
        if (rDto.getImage10() != null && allowedMimeTypes.get(rDto.getImage10().getContentType()) == null)
            return false;
        return  true;
    }

    public RealEstate modifySequenceToLatest(Long id) throws IOException {
        RealEstate realEstate = this.getRealEstateById(id);
        if (realEstate == null) {
            throw new IOException("게시글 id로 찾기 실패");
        }
        RealEstate newEstate = new RealEstate();
        newEstate = realEstateRepository.save(newEstate);
        Long newId = newEstate.getId();
        realEstateRepository.delete(newEstate);
        realEstateRepository.delete(realEstate);
        realEstate.setId(newId);

        realEstate = realEstateRepository.save(realEstate);
//        redisTemp.delete(REAL_ESTATES_REDIS_KEY);
        redisTemp.opsForValue().set("realEstate:id:" + realEstate.getId(), realEstate);

        return realEstate;
    }

}
