package com.jjeong.kiwi.service;

import static org.junit.jupiter.api.Assertions.*;

import com.jjeong.kiwi.dto.RealEstateDto;
import com.jjeong.kiwi.dto.RealEstateWithImgPathDto;
import com.jjeong.kiwi.dto.RealEstateWithoutImgDto;
import com.jjeong.kiwi.model.RealEstate;
import com.jjeong.kiwi.repository.RealEstateQueryRepository;
import com.jjeong.kiwi.repository.RealEstateRepository;
import com.jjeong.kiwi.tool.DeepCopyViaSerialization;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealEstateServiceTest {
    @Mock
    private RealEstateQueryRepository realEstateQueryRepository;

    @Mock
    private RealEstateRepository realEstateRepository;

    @InjectMocks
    private RealEstateService realEstateService;

    private static RealEstate defaultData = new RealEstate();
    static  {
        defaultData.setId(1L);
        defaultData.setArea(33L);
        defaultData.setDescription("it is a sample");
        defaultData.setDirection("S");
        defaultData.setAdministration_cost(12345L);
        defaultData.setAdministration_cost2(23456L);
        defaultData.setAdministrative_agency_approval_date(LocalDateTime.now().toString());
        defaultData.setLatitude(127.123);
        defaultData.setLongitude(36.123);
        defaultData.setTransaction_type("매매");
        defaultData.setTitle("sample apartment");
        defaultData.setSoldout(false);
        defaultData.setResidence_availability_date(LocalDateTime.now().toString());
        defaultData.setRelay_object_type("apartment");
        defaultData.setPrice(987654321L);
        defaultData.setNumber_of_cars_parked(2L);
        defaultData.setLocation("seoul city");
        defaultData.setImage("img1.jpg");
        defaultData.setImage2("img2.jpg");
        defaultData.setImage3("img3.jpg");
        defaultData.setImage4("img4.jpg");
        defaultData.setImage5("img5.jpg");
        defaultData.setImage6("img6.jpg");
        defaultData.setImage7("img7.jpg");
        defaultData.setImage8("img8.jpg");
        defaultData.setImage9("img9.jpg");
        defaultData.setImage10("img10.jpg");
    }

    @Test
    void getRealEstatesByOffset_WithValidDate_FindSuccessfully() {
        List<RealEstate> result = Collections.emptyList();
        when(realEstateQueryRepository.findByOffset(anyLong(),anyLong())).thenReturn(result);
        assertDoesNotThrow(()
            -> realEstateService.getRealEstatesByOffset(0L, 40L));
    }

    @Test
    void getRealEstatesByOffset_NullModel() {
        when(realEstateQueryRepository.findByOffset(anyLong(),anyLong())).thenReturn(null);
        ResponseStatusException exceptResult = assertThrows(ResponseStatusException.class, ()
            -> realEstateService.getRealEstatesByOffset(0L, 40L));
        assertEquals(HttpStatus.NOT_FOUND, exceptResult.getStatus());
    }

    @Test
    void getRealEstatesByOffset_Return_Data() {
        List<RealEstate> repoResult = new ArrayList<>();
        int testSize = 40;
        for (int i = 0; i < testSize; i++){
            RealEstate data = DeepCopyViaSerialization.deepCopy(defaultData);
            data.setId((long)i);
            repoResult.add(data);
        }
        when(realEstateQueryRepository.findByOffset(anyLong(),anyLong())).thenReturn(repoResult);
        List<RealEstateWithImgPathDto> serviceResult =
            realEstateService.getRealEstatesByOffset(0L, 40L);
        assertEquals(40, serviceResult.size());
        List<RealEstateWithImgPathDto> diffResult = new ArrayList<>();
        repoResult.forEach(result -> diffResult.add(new RealEstateWithImgPathDto(result)));
        assertEquals(diffResult, serviceResult);
    }

    @Test
    void getRealEstatesByKeySet_WithValidData_DoesNotThrowException() {
        // Mock repository to return an empty list to simulate the valid case
        when(realEstateQueryRepository.findByOffsetWidthKeySet(anyLong(), anyLong(), anyLong()))
            .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> realEstateService.getRealEstatesByKeySet(0L, 40L));
    }

    @Test
    void getRealEstatesByKeySet_WhenRepositoryReturnsNull_ThrowsResponseStatusException() {
        // Mock repository to return null
        when(realEstateQueryRepository.findByOffsetWidthKeySet(anyLong(), anyLong(), anyLong()))
            .thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            realEstateService.getRealEstatesByKeySet(0L, 40L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getRealEstatesByKeySet_WithValidData_ReturnsCorrectData() {
        List<RealEstate> mockedList = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            RealEstate realEstate = DeepCopyViaSerialization.deepCopy(defaultData);
            defaultData.setId((long)i);
            mockedList.add(realEstate);
        }
        when(realEstateQueryRepository.findByOffsetWidthKeySet(anyLong(), anyLong(), anyLong()))
            .thenReturn(mockedList);
        List<RealEstateWithImgPathDto> result =
            realEstateService.getRealEstatesByKeySet(0L, 10L);
        assertEquals(10, result.size());
        List<RealEstateWithImgPathDto> diffList = new ArrayList<>();
        mockedList.forEach(mockedData -> diffList.add(new RealEstateWithImgPathDto(mockedData)));
        assertEquals(diffList, result);
    }

    @Test
    void getRealEstateById_WhenRealEstateExists_ReturnsRealEstate() {
        Long realEstateId = 1L;
        RealEstate expectedRealEstate = new RealEstate();
        expectedRealEstate.setId(realEstateId);
        when(realEstateRepository.findById(realEstateId)).thenReturn(Optional.of(expectedRealEstate));

        RealEstate result = realEstateService.getRealEstateById(realEstateId);

        assertEquals(expectedRealEstate, result);
    }

    @Test
    void getRealEstateById_WhenRealEstateDoesNotExist_ThrowsResponseStatusException() {
        Long realEstateId = 1L;
        when(realEstateRepository.findById(realEstateId)).thenReturn(Optional.empty());

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () ->
            realEstateService.getRealEstateById(realEstateId));

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    void getRealEstateWithoutImg_ReturnsDtoSuccessfully() {
        Long id = 1L;
        RealEstateWithoutImgDto mockDto = new RealEstateWithoutImgDto();
        when(realEstateQueryRepository.findByIdWithoutImg(id)).thenReturn(mockDto);

        RealEstateWithoutImgDto result = realEstateService.getRealEstateWithoutImg(id);

        assertNotNull(result);
        assertEquals(mockDto, result);
    }

    @Test
    void getRealEstateWithoutImg_ThrowsNotFoundException() {
        Long id = 1L;
        when(realEstateQueryRepository.findByIdWithoutImg(id)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> realEstateService.getRealEstateWithoutImg(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void modifyRealEstate_WhenMimeTypeIsInvalid_ThrowsForbiddenException() {
        RealEstateDto dtoWithInvalidMime = new RealEstateDto();
        assertThrows(ResponseStatusException.class, () -> realEstateService.modifyRealEstate(1L, dtoWithInvalidMime),
            "Expected modifyRealEstate to throw due to invalid mime type, but it didn't");
    }

    @Test
    void modifyRealEstate_WhenRealEstateNotFound_ThrowsNotFoundException() {
        Long nonExistentId = 999L;
        RealEstateDto dummyDto = new RealEstateDto();
        assertThrows(ResponseStatusException.class, () -> realEstateService.modifyRealEstate(nonExistentId, dummyDto),
            "Expected modifyRealEstate to throw due to non-existent real estate, but it didn't");
    }

}
