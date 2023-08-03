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

const RealestateModal: React.FC = () => {
  const showModal = useRecoilValue(realestateModalState);
  const setModalState = useSetRecoilState(realestateModalState);
  const resetState = useResetRecoilState(realestateModalState);
  const setEditModalState = useSetRecoilState(realestateEditModalState);
  const updateChecker = useRecoilValue(mainUpdateChecker);
  const setMainUpdateChecker = useSetRecoilState(mainUpdateChecker);
  const [realEstateInfo, setRealEstateInfo] = useState<types.RealEstate | null>(null);
  const [images, setImages] = useState<any[]>([require("../../../assets/sampleroom.png"),]);

  useEffect(() => {
    if (showModal.show) {
      fetchRealEstateInfo();
    } else {
      setRealEstateInfo(null);
    }
  }, [showModal]);

  const fetchRealEstateInfo = async () => {
    try {
      const res = await axios.get<types.RealEstate>(
        `/api/realestate/${showModal.realestateId}`,
        { withCredentials: true }
      );
      setRealEstateInfo(res.data);
      setImages([await getImageData(res.data.id)])
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
              <h1>게시물 삭제</h1>
              <p>게시물을 삭제하시겠습니까?</p>
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

  const getImageData = async (id: number) => {
    try {
      const imgDataRes = await axios.get('/api/realestate/image/' + id, {
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
          <Grid container columns={2} columnSpacing={2}>
            {/* 수정버튼 */}
            <Grid item xs={1}>
              <DefaultButton
                onClick={handleModifyModal}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정
              </DefaultButton>
            </Grid>
            {/* 삭제버튼 */}
            <Grid item xs={1}>
              <DefaultButton
                onClick={handleDeleteSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                삭제
              </DefaultButton>
            </Grid>
            {/* 매물정보 */}
            <Grid item xs={2}>
              <Typography variant="body1" gutterBottom>
                <center>
                <Avatar src={images[0]} alt="estate_image" variant="rounded" sx={{ width: 500, height: 350 }} />
                </center>
              </Typography>
              <Typography variant="body1" gutterBottom>
                ID: {realEstateInfo.id}
              </Typography>
              <Typography variant="body1" gutterBottom>
                제목: {realEstateInfo.title.length > 20 ? <div style={{ wordBreak: "break-word" }}>{realEstateInfo.title}</div> : realEstateInfo.title}
              </Typography>
              <Typography variant="body1" gutterBottom>
                설명: {realEstateInfo.description.length > 20 ? <div style={{ wordBreak: "break-word" }}>{realEstateInfo.description}</div> : realEstateInfo.description}
              </Typography>
              <Typography variant="body1" gutterBottom>
                가격: {realEstateInfo.price}
              </Typography>
              <Typography variant="body1" gutterBottom>
              중계대상물종류: {getRelayObjectType()}
              </Typography>
              <Typography variant="body1" gutterBottom>
              소재지: {realEstateInfo.location}
              </Typography>
              <Typography variant="body1" gutterBottom>
              면적: {realEstateInfo.area}
              </Typography>
              <Typography variant="body1" gutterBottom>
              거래형태: {realEstateInfo.transaction_type}
              </Typography>
              <Typography variant="body1" gutterBottom>
              입주가능일: {realEstateInfo.residence_availability_date}
              </Typography>
              <Typography variant="body1" gutterBottom>
              행정기관승인날짜: {realEstateInfo.administrative_agency_approval_date}
              </Typography>
              <Typography variant="body1" gutterBottom>
              주차대수: {realEstateInfo.number_of_cars_parked}
              </Typography>
              <Typography variant="body1" gutterBottom>
              방향: {realEstateInfo.direction}
              </Typography>
              <Typography variant="body1" gutterBottom>
              관리비: {realEstateInfo.administration_cost}
              </Typography>
            </Grid>

          </Grid>
        )}
        <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
      </Stack>
      </ModalScrollableWrapper>
    </ModalBase>
  );
};

export default RealestateModal;