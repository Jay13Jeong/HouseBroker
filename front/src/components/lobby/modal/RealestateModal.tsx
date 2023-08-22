import React, { useEffect, useState } from "react";
import { useRecoilValue, useSetRecoilState, useResetRecoilState } from "recoil";
import { realestateModalState, realestateEditModalState, mainUpdateChecker } from "../../../common/states/recoilModalState";
import * as types from "../../../common/types/User";
import ModalBase from "../../modal/ModalBase";
import axios from "axios";
import { toast } from "react-toastify";
import { confirmAlert } from "react-confirm-alert";
import "react-confirm-alert/src/react-confirm-alert.css";
import "./../../../assets/confirm-alert.css";
import { Avatar } from '@mui/material';
import { Typography, Stack, Grid, TextField } from "@mui/material";
import { DefaultButton } from "../../common";
import { ModalScrollableWrapper } from "../../realestate/ScrollableWrapper.style";
import { Map, MapMarker, ZoomControl } from "react-kakao-maps-sdk";
import { REACT_APP_NAME, REACT_APP_MY_LOCATE_X, REACT_APP_MY_LOCATE_Y } from '../../../common/configData';
import { CustomGrid, CustomTextWrapper } from "../../modal/Modal.style";

const RealestateModal: React.FC = () => {
  const showModal = useRecoilValue(realestateModalState);
  const setModalState = useSetRecoilState(realestateModalState);
  const resetState = useResetRecoilState(realestateModalState);
  const setEditModalState = useSetRecoilState(realestateEditModalState);
  const updateChecker = useRecoilValue(mainUpdateChecker);
  const setMainUpdateChecker = useSetRecoilState(mainUpdateChecker);
  const [realEstateInfo, setRealEstateInfo] = useState<types.RealEstate | null>(null);
  const [images, setImageFile] = useState<any[]>([require("../../../assets/sampleroom.png"),]);
  const [zoomable, setZoomable] = useState<boolean>(false);

  useEffect(() => {
    if (showModal.show) {
      fetchRealEstateInfo();
    } else {
      setRealEstateInfo(null);
    }
    setZoomable(false);
  }, [showModal]);

  const fetchRealEstateInfo = async () => {
    try {
      const res = await axios.get<types.RealEstate>(
        `/api/realestate/${showModal.realestateId}`,
        { withCredentials: true }
      );
      setRealEstateInfo(res.data);
      setImageFile([]);
      for (let i= 1; i <= 10; i++){
        const imgData = await getImageData(res.data.id, i)
        setImageFile((prevState) => [...prevState, imgData]);
      }
    } catch (err: any) {
      toast.error(err.response.data.message);
    }
  };

  const handleCloseModal = () => {
    resetState();
  };

  const handleModifyModal = () => {
    setEditModalState({ realestateId: showModal.realestateId, show: true });
    resetState();
  };

  const pageUpdateChecker = () => {
    if (updateChecker.updateCount >= 9999){
        setMainUpdateChecker({updateCount:0});
        return;
    }
    setMainUpdateChecker({updateCount:(updateChecker.updateCount + 1)});
  }

  const handleDeleteSubmit = () => {
    confirmAlert({
      customUI: ({ onClose }) => {
        return (
          <div className="react-confirm-alert-overlay">
            <div className="react-confirm-alert">
              <h1>게시물을 삭제하시겠습니까?</h1>
              <div className="react-confirm-alert-button-group">
                <button onClick={onClose}>아니오</button>
                <button
                  onClick={() => {
                    onClose();
                    deleteRealEstate();
                    pageUpdateChecker();
                  }}
                >
                  예
                </button>
              </div>
            </div>
          </div>
        );
      },
    });
  };

  const deleteRealEstate = async () => {
    try {
      const res = await axios.delete(
        `/api/realestate/${showModal.realestateId}`,
        { withCredentials: true }
      );
      toast.success("삭제 요청 완료");
    } catch (err: any) {
      toast.error(err.response.data.message);
    }
  };

  const getImageData = async (id: number, index: number) => {
    try {
      const imgDataRes = await axios.get('/api/realestate/image/' + id + '/' + index, {
        withCredentials: true,
        responseType: 'blob'
      });
      return URL.createObjectURL(imgDataRes.data);
    } catch (error) {
      return require("../../../assets/sampleroom.png");
    }
  };

  const getRelayObjectType = () => {
    if (realEstateInfo === null)
      return "";
    switch (realEstateInfo.relay_object_type) {
      case 'office':
        return "아파트 / 오피스텔";
      case 'house':
        return "주택";
      case 'room':
        return "원룸 / 투룸";
      case 'mall':
        return "상가";
      case 'land':
        return "토지";
      default:
        return realEstateInfo.relay_object_type;
    }
  }

  return (
    <ModalBase open={showModal.show} onClose={handleCloseModal} closeButton>
      <ModalScrollableWrapper>
      <Stack justifyContent="center" alignItems="center">
        {realEstateInfo && (
          <CustomGrid container columns={2} columnSpacing={2} className="estateInfoes">
            {/* 사진정보 */}
            <Grid item xs={2}>
            <CustomTextWrapper>
                <center>
                <Avatar src={images[0]} alt="estate_image" variant="rounded" sx={{ width: 500, height: 350 }} />
                </center>
              </CustomTextWrapper>
            </Grid>
            {/* 매물정보 */}
            <Grid item xs={1}>
              <CustomTextWrapper>
              <h4>매물명</h4>{realEstateInfo.title}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>설명</h4>{realEstateInfo.description}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>가격</h4>{realEstateInfo.price}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>중계대상물종류</h4>{getRelayObjectType()}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>소재지</h4>{realEstateInfo.location}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>면적</h4>{realEstateInfo.area}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>주차대수</h4>{realEstateInfo.number_of_cars_parked}
              </CustomTextWrapper>
            </Grid>
            <Grid item xs={1}>
              
              <CustomTextWrapper>
              <h4>거래형태</h4>{realEstateInfo.transaction_type}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>입주가능일</h4>{realEstateInfo.residence_availability_date}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>행정기관승인날짜</h4>{realEstateInfo.administrative_agency_approval_date}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>방향</h4>{realEstateInfo.direction}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>관리비</h4>{realEstateInfo.administration_cost}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>사용료</h4>{realEstateInfo.administration_cost2}
              </CustomTextWrapper>
              <CustomTextWrapper>
              <h4>매물관리번호</h4>{realEstateInfo.id}
              </CustomTextWrapper>
            </Grid>
            {/* 지도정보 */}
            {realEstateInfo.latitude && realEstateInfo.longitude &&
            <Grid item xs={2}>
              <br/>
            <Map
              style={{ width: "100%", height: "40vh", }}
              center={{ lat: realEstateInfo.latitude, lng: realEstateInfo.longitude }}
              level={3}
              onClick={() => setZoomable(true)}
              zoomable={zoomable}
            >
            <ZoomControl />
            <MapMarker position={{ lat: Number(REACT_APP_MY_LOCATE_Y), lng: Number(REACT_APP_MY_LOCATE_X) }}>
              <div style={{textAlign:"center", width:"15vh"}}>{REACT_APP_NAME}</div>
            </MapMarker>
            {
                realEstateInfo.latitude && realEstateInfo.longitude &&
                <MapMarker
                  position={{ lat: realEstateInfo.latitude, lng: realEstateInfo.longitude }}
                >
                  <div
                    style={{textAlign:"center", width:"15vh"}}
                  >
                    {realEstateInfo.title}
                  </div>
                </MapMarker>
              }
            </Map>
          </Grid>
          }
          {/* 수정버튼 */}
          <Grid item xs={1}>
            <DefaultButton
              onClick={handleModifyModal}
              sx={{ width: "100%" }}
            >
              수정
            </DefaultButton>
          </Grid>
          {/* 삭제버튼 */}
          <Grid item xs={1}>
            <DefaultButton
              onClick={handleDeleteSubmit}
              sx={{ width: "100%" }}
            >
              삭제
            </DefaultButton>
          </Grid>
          </CustomGrid>
        )}
        <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
      </Stack>
      </ModalScrollableWrapper>
    </ModalBase>
  );
};

export default RealestateModal;