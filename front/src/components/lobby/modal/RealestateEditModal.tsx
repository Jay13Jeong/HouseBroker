import React, { useEffect, useState, useRef } from "react";
import { useRecoilValue, useSetRecoilState, useResetRecoilState } from "recoil";
import { realestateEditModalState, mainUpdateChecker } from "../../../common/states/recoilModalState";
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

const RealestateEditModal: React.FC = () => {
  const [isChange, setIsChange] = useState<number>(0);
  const showModal = useRecoilValue(realestateEditModalState);
  const setModalState = useSetRecoilState(realestateEditModalState);
  const resetState = useResetRecoilState(realestateEditModalState);
  const updateChecker = useRecoilValue(mainUpdateChecker);
  const setMainUpdateChecker = useSetRecoilState(mainUpdateChecker);
  const [realEstateInfo, setRealEstateInfo] = useState<types.RealEstate | null>(null);
  const [realEstatePreInfo, setRealEstatePreInfo] = useState<types.RealEstate | null>(null);
  const [title, setTitle] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [price, setPrice] = useState<number>(0);
  const [imageFile, setImageFile] = useState('');
  const [soldout, setSoldout] = useState<boolean>(false);
  const inputRef = useRef<HTMLInputElement | null> (null);
  const [uploadedId, setUploadedId] = useState<string>('');
  const [relay_object_type, setRelay_object_type] = useState<string>('');
  const [location, setLocation] = useState<string>('');
  const [area, setArea] = useState<number>(0);
  const [transaction_type, setTransaction_type] = useState<string>('');
  const [residence_availability_date, setResidence_availability_date] = useState<string>('');
  const [administrative_agency_approval_date, setAdministrative_agency_approval_date] = useState<string>('');
  const [number_of_cars_parked, setNumber_of_cars_parked] = useState<number>(0);
  const [direction, setDirection] = useState<string>('');
  const [administration_cost, setAdministration_cost] = useState<number>(0);

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

  const pageUpdateChecker = () => {
    if (updateChecker.updateCount >= 9999){
        setMainUpdateChecker({updateCount:0});
        return;
    }
    setMainUpdateChecker({updateCount:(updateChecker.updateCount + 1)});
  }

  const handleModifyTitleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    // console.log("handleModifyTitleSubmit")
    // console.log(title)
    if (realEstateInfo === null) return;
    try {
      const response = await axios.patch<types.RealEstate>(
        `/api/realestate/${showModal.realestateId}`,
        {
          title: title,
        },
        { withCredentials: true, headers: { 'Content-Type': 'multipart/form-data' } }
      );
      toast.success("제목 변경 성공");
      pageUpdateChecker();
    } catch (err: any) {
      toast.error("제목 변경 실패");
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
          description: description,
        },
        { withCredentials: true, headers: { 'Content-Type': 'multipart/form-data' } }
      );
      toast.success("세부사항 변경 성공");
    } catch (err: any) {
      toast.error("세부사항 변경 실패");
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
          price: price,
        },
        { withCredentials: true, headers: { 'Content-Type': 'multipart/form-data' } }
      );
      toast.success("가격 변경 성공");
    } catch (err: any) {
      toast.error("가격 변경 실패");
      toast.error(err.response.data.message);
    }
  };

  // const handleModifyImageSubmit = async (event: React.FormEvent) => {
  //   event.preventDefault();
  //   if (realEstateInfo === null) return;
  //   try {
  //     const response = await axios.patch<types.RealEstate>(
  //       `/api/realestate/${showModal.realestateId}`,
  //       {
  //         image : 
  //       },
  //       { withCredentials: true, headers: { 'Content-Type': 'multipart/form-data' } }
  //     );
  //     toast.success("이미지 변경 성공");
  //   } catch (err: any) {
  //     toast.error(err.response.data.message);
  //   }
  // };

  const handleModifyKey = (event: React.KeyboardEvent<HTMLDivElement>) => {
    if (event.key !== "Enter") return;
    event.preventDefault();
    if (realEstateInfo === null) return;
    setRealEstateInfo(realEstateInfo);
  };

  const handleCloseModal = () => {
    resetState();
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
                  setTitle(event.target.value)
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyTitleSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
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
                  setDescription(event.target.value)
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyDescriptionSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
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
                  setPrice(Number(event.target.value))
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyPriceSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>

            {/* <Grid
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
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid> */}
          </Grid>
        )}
      </Stack>
    </ModalBase>
  );
};

export default RealestateEditModal;