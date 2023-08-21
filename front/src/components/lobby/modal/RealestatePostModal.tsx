import React, { useEffect, useState } from "react";
import { useRecoilValue, useSetRecoilState, useResetRecoilState } from "recoil";
import { realestatePostModalState } from "../../../common/states/recoilModalState";
import UserCardButtonList from "../../card/user/UserCardButtonList";
import * as types from "../../../common/types/User";
import ModalBase from "../../modal/ModalBase";
import axios from "axios";
import { REACT_APP_HOST } from "../../../common/configData";
import useGetData from "../../../util/useGetData";
import { toast } from "react-toastify";
import { confirmAlert } from "react-confirm-alert";
import "react-confirm-alert/src/react-confirm-alert.css";
import "./../../../assets/confirm-alert.css";

import { Typography, Stack, Grid, TextField } from "@mui/material";
import { DefaultButton } from "../../common";

const RealestatePostModal: React.FC = () => {
  const [isChange, setIsChange] = useState<number>(0);
  const showModal = useRecoilValue(realestatePostModalState);
  const setModalState = useSetRecoilState(realestatePostModalState);
  const resetState = useResetRecoilState(realestatePostModalState);
  const [realEstateInfo, setRealEstateInfo] = useState<types.RealEstate | null>(null);
  const [realEstatePreInfo, setRealEstatePreInfo] = useState<types.RealEstate | null>(null);

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
      setRealEstatePreInfo(res.data);
    } catch (err: any) {
      toast.error(err.response.data.message);
    }
  };

  const handleModifyTitleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (realEstateInfo === null) return;
    try {
      const response = await axios.patch<types.RealEstate>(
        `/api/realestate/${showModal.realestateId}`,
        {
          title: realEstateInfo.title,
          description: realEstateInfo.description,
          price: realEstateInfo.price,
          image: realEstateInfo.image,
        },
        { withCredentials: true }
      );
      toast.success("제목 변경 성공");
    } catch (err: any) {
      toast.error(err.response.data.message);
    }
  };

  const handleModifyDescriptionSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (realEstateInfo === null) return;
    try {
      const response = await axios.patch<types.RealEstate>(
        `/api/realestate/${showModal.realestateId}`,
        {
          title: realEstateInfo.title,
          description: realEstateInfo.description,
          price: realEstateInfo.price,
          image: realEstateInfo.image,
        },
        { withCredentials: true }
      );
      toast.success("설명 변경 성공");
    } catch (err: any) {
      toast.error(err.response.data.message);
    }
  };

  const handleModifyPriceSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (realEstateInfo === null) return;
    try {
      const response = await axios.patch<types.RealEstate>(
        `/api/realestate/${showModal.realestateId}`,
        {
          title: realEstateInfo.title,
          description: realEstateInfo.description,
          price: realEstateInfo.price,
          image: realEstateInfo.image,
        },
        { withCredentials: true }
      );
      toast.success("가격 변경 성공");
    } catch (err: any) {
      toast.error(err.response.data.message);
    }
  };

  const handleModifyImageSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (realEstateInfo === null) return;
    try {
      const response = await axios.patch<types.RealEstate>(
        `/api/realestate/${showModal.realestateId}`,
        {
          title: realEstateInfo.title,
          description: realEstateInfo.description,
          price: realEstateInfo.price,
          image: realEstateInfo.image,
        },
        { withCredentials: true }
      );
      toast.success("이미지 변경 성공");
    } catch (err: any) {
      toast.error(err.response.data.message);
    }
  };

  const handleModifyKey = (event: React.KeyboardEvent<HTMLDivElement>) => {
    if (event.key !== "Enter") return;
    event.preventDefault();
    if (realEstateInfo === null) return;
    setRealEstateInfo(realEstateInfo);
  };

  const handleCloseModal = () => {
    resetState();
  };

  const handleModifyRealEstateTitle = (newTitle: string) => {
    let newInfo = realEstateInfo;
    if (newInfo === null)
      return;
    newInfo.title = newTitle;
    setRealEstateInfo(newInfo);
  };

  const handleModifyRealEstateDescription = (newDescription: string) => {
    let newInfo = realEstateInfo;
    if (newInfo === null)
      return;
    newInfo.description = newDescription;
    setRealEstateInfo(newInfo);
  };

  const handleModifyRealEstatePrice = (newPrice: number) => {
    let newInfo = realEstateInfo;
    if (newInfo === null)
      return;
    newInfo.price = newPrice;
    setRealEstateInfo(newInfo);
  };

  const handleModifyRealEstateImage = (newImage: string) => {
    let newInfo = realEstateInfo;
    if (newInfo === null)
      return;
    newInfo.image = newImage;
    setRealEstateInfo(newInfo);
  };

  const handleModifyRealEstateSoldout = (newSoldout: boolean) => {
    let newInfo = realEstateInfo;
    if (newInfo === null)
      return;
    newInfo.soldout = newSoldout;
    setRealEstateInfo(newInfo);
  };

  return (
    <ModalBase open={showModal.show} onClose={handleCloseModal} closeButton>
      <Stack justifyContent="center" alignItems="center">
        <Typography variant="h2" gutterBottom>
          👥 수정페이지 👥
        </Typography>
        {realEstateInfo && (
          <Grid container columns={5} columnSpacing={2}>
            <Grid
              item
              xs={3}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="title"
                label="제목"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.title : ""}
                onChange={(event) =>
                  handleModifyRealEstateTitle(event.target.value)
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyTitleSubmit}
                sx={{ width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>

            <Grid
              item
              xs={3}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="description"
                label="설명"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.description : ""}
                onChange={(event) =>
                  handleModifyRealEstateDescription(event.target.value)
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyDescriptionSubmit}
                sx={{ width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>

            <Grid
              item
              xs={3}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="price"
                label="가격"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.price.toString() : ""}
                onChange={(event) =>
                  handleModifyRealEstatePrice(Number(event.target.value))
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyPriceSubmit}
                sx={{ width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>

            <Grid
              item
              xs={3}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="image"
                label="이미지"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.image : ""}
                onChange={(event) =>
                  handleModifyRealEstateImage(event.target.value)
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyImageSubmit}
                sx={{ width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
          </Grid>
        )}
      </Stack>
    </ModalBase>
  );
};

export default RealestatePostModal;