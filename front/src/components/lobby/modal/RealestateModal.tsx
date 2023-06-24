import React, { useEffect, useState } from "react";
import { useRecoilValue, useSetRecoilState, useResetRecoilState } from "recoil";
import { realestateModalState, realestateEditModalState } from "../../../common/states/recoilModalState";
import * as types from "../../../common/types/User";
import ModalBase from "../../modal/ModalBase";
import axios from "axios";
import { toast } from "react-toastify";
import { confirmAlert } from "react-confirm-alert";
import "react-confirm-alert/src/react-confirm-alert.css";
import "./../../../assets/confirm-alert.css";

import { Typography, Stack, Grid, TextField } from "@mui/material";
import { DefaultButton } from "../../common";

const RealestateModal: React.FC = () => {
  const showModal = useRecoilValue(realestateModalState);
  const setModalState = useSetRecoilState(realestateModalState);
  const resetState = useResetRecoilState(realestateModalState);
  const setEditModalState = useSetRecoilState(realestateEditModalState);
  const [realEstateInfo, setRealEstateInfo] = useState<types.RealEstate | null>(null);

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

  return (
    <ModalBase open={showModal.show} onClose={handleCloseModal} closeButton>
      <Stack justifyContent="center" alignItems="center">
        <Typography variant="h2" gutterBottom>
          👥 상세정보 👥
        </Typography>
        {realEstateInfo && (
          <Grid container columns={4} columnSpacing={2}>
            <Grid>
              <DefaultButton
                onClick={handleModifyModal}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정
              </DefaultButton>
            </Grid>
            <Grid>
              <DefaultButton
                onClick={handleDeleteSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                삭제
              </DefaultButton>
            </Grid>
            <Grid item xs={12}>
              <Typography variant="h4" gutterBottom>
                매물 정보:
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
                이미지: {realEstateInfo.image}
              </Typography>
            </Grid>
          </Grid>
        )}
      </Stack>
    </ModalBase>
  );
};

export default RealestateModal;