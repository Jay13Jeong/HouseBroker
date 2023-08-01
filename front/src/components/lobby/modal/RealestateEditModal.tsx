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
import { ModalScrollableWrapper } from "../../realestate/ScrollableWrapper.style";
import { Avatar } from '@mui/material';

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
      setSoldout(res.data.soldout);
      setImageFile(await getImageData(res.data.id));
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

  const ModifyDataSubmit = async (target: string, newData : any) => {
    if (realEstateInfo === null) return;
    try {
      const response = await axios.patch<types.RealEstate>(
        `/api/realestate/${showModal.realestateId}`,
        {
          [target]: newData,
        },
        { withCredentials: true, headers: { 'Content-Type': 'multipart/form-data' } }
      );
      toast.success("변경 성공");
      pageUpdateChecker();
    } catch (err: any) {
      toast.error("변경 실패");
      toast.error(err.response.data.message);
    }
  };

  const handleModifyTitleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("title", title);
  };

  const handleModifyDescriptionSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("description", description);
  };

  const handleModifyPriceSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("price", price);
  };

  const handleModifyRelay_object_typeSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("relay_object_type", relay_object_type);
  };

  const handleModifyLocationSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("location", location);
  };

  const handleModifyAreaSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("area", area);
  };

  const handleModifyTransaction_typeSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("transaction_type", transaction_type);
  };

  const handleModifyResidence_availability_dateSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("residence_availability_date", residence_availability_date);
  };

  const handleModifyAdministrative_agency_approval_dateSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("administrative_agency_approval_date", administrative_agency_approval_date);
  };

  const handleModifyNumber_of_cars_parkedSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("number_of_cars_parked", number_of_cars_parked);
  };

  const handleModifyDirectionSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("direction", direction);
  };

  const handleModifyAdministration_costSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("administration_cost", administration_cost);
  };

  const handleModifySoldOutSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (realEstateInfo === null) return;
    try {
      const response = await axios.patch(
        `/api/realestate/soldout/${showModal.realestateId}`,
        {
          soldout: soldout,
        },
        { withCredentials: true, }
      );
      toast.success("거래상태 변경 성공");
      pageUpdateChecker();
    } catch (err: any) {
      toast.error("거래상태 변경 실패");
      toast.error(err.response.data.message);
    }
  };

  const handleModifyImageSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!(inputRef.current && inputRef.current.value))
        return;
    ModifyDataSubmit("image", inputRef.current.files![0]);
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

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const selectedFile = e.target.files[0];
      if (selectedFile.size >= ((1 << 20) * 10))
          throw("10MB미만 업로드 가능.");
      setImageFile(URL.createObjectURL(selectedFile));
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

  return (
    <ModalBase open={showModal.show} onClose={handleCloseModal} closeButton>
      <ModalScrollableWrapper>
      <Stack justifyContent="center" alignItems="center">
        <Typography variant="h2" gutterBottom>
          부동산 수정 페이지 
        </Typography>
        {realEstateInfo && (
          <Grid container columns={5} columnSpacing={2}>
            {/* 이미지 수정 */}
            <Grid container columns={23} columnSpacing={2}>
              <Grid item xs={1}/>
              <Grid item xs={21} sx={{ border: '1px solid black', borderRadius: '5px' }}>
                <Grid container columns={5} columnSpacing={2}>
                  <Grid item xs={5} display="flex" justifyContent="center" alignItems="center">
                    <Avatar src={imageFile} alt="real-estate image" variant="rounded" sx={{ width: 500, height: 350 }} />
                  </Grid>
                  <Grid item xs={5} display="flex" justifyContent="center" alignItems="center">
                    <input
                      type="file"
                      id="image"
                      accept="image/*"
                      onChange={handleImageChange}
                      ref={inputRef}
                    />
                  </Grid>
                  <Grid item xs={5} display="flex" justifyContent="center" alignItems="center">
                    <DefaultButton
                      onClick={handleModifyImageSubmit}
                      sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
                    >
                      수정하기
                    </DefaultButton>
                  </Grid>
                </Grid>
              </Grid>
              <Grid item xs={1}/>
            </Grid>
            {/* 거래완료여부 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <label htmlFor="soldout"><h3>거래완료 여부</h3>
                <label>
                    <input type="radio" name="soldout" id="soldout" defaultChecked={!soldout} value="true" onChange={(e) => setSoldout(false)} /> 거래 중
                </label>
                <label>
                    <input type="radio" name="soldout" id="soldout" defaultChecked={soldout} value="false" onChange={(e) => setSoldout(true)} /> 거래 완료
                </label>
              </label>
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifySoldOutSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* 제목수정 */}
            {/* <Grid item xs={4} display="flex" justifyContent="center" alignItems="center" >
              <TextField
                fullWidth
                id="title"
                label="기존 제목(읽기전용)"
                variant="outlined"
                size="small"
                value={realEstateInfo.title}
              />
            </Grid> */}
            <Grid
              item
              xs={4}
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
            {/* 세부사항 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="description"
                label="세부사항"
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
            {/* 가격수정 */}
            <Grid
              item
              xs={4}
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
            {/* 중계대상물 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="relay_object_type"
                label="중계대상물"
                variant="outlined"
                size="small"
                onChange={(event) =>
                  setRelay_object_type(event.target.value)
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyRelay_object_typeSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* 소재지 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="location"
                label="소재지"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.description : ""}
                onChange={(event) =>
                  setLocation(event.target.value)
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyLocationSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* 면적 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="area"
                label="면적"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.description : ""}
                onChange={(event) =>
                  setArea(Number(event.target.value))
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyAreaSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* 거래형태 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="transaction_type"
                label="거래형태"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.description : ""}
                onChange={(event) =>
                  setTransaction_type(event.target.value)
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyTransaction_typeSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* 입주가능일 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="residence_availability_date"
                label="입주가능일"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.description : ""}
                onChange={(event) =>
                  setResidence_availability_date(event.target.value)
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyResidence_availability_dateSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* 행정기관승인날짜 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="administrative_agency_approval_date"
                label="행정기관승인날짜"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.description : ""}
                onChange={(event) =>
                  setAdministrative_agency_approval_date(event.target.value)
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyAdministrative_agency_approval_dateSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* 주차대수 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="number_of_cars_parked"
                label="주차대수"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.description : ""}
                onChange={(event) =>
                  setNumber_of_cars_parked(Number(event.target.value))
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyNumber_of_cars_parkedSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* 방향 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="direction"
                label="방향"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.description : ""}
                onChange={(event) =>
                  setDirection(event.target.value)
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyDirectionSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* 관리비 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="administration_cost"
                label="관리비"
                variant="outlined"
                size="small"
                // placeholder={realEstatePreInfo? realEstatePreInfo.description : ""}
                onChange={(event) =>
                  setAdministration_cost(Number(event.target.value))
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyAdministration_costSubmit}
                sx={{ marginLeft: 0, marginRight: 0, width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* end */}
          </Grid>
        )}
      </Stack>
      </ModalScrollableWrapper>
    </ModalBase>
  );
};

export default RealestateEditModal;