import React, { useEffect, useState, useRef } from "react";
import { useRecoilValue, useSetRecoilState, useResetRecoilState } from "recoil";
import { realestateEditModalState, mainUpdateChecker, selectedImgCardIndexState } from "../../../common/states/recoilModalState";
import * as types from "../../../common/types/User";
import ModalBase from "../../modal/ModalBase";
import axios from "axios";
import { REACT_APP_NAME, REACT_APP_MY_LOCATE_X, REACT_APP_MY_LOCATE_Y } from '../../../common/configData';
import { toast } from "react-toastify";
import "react-confirm-alert/src/react-confirm-alert.css";
import "./../../../assets/confirm-alert.css";
import { ModalScrollableWrapper } from "../../realestate/ScrollableWrapper.style";
import { Avatar, Button } from '@mui/material';
import { Typography, Stack, Grid, TextField } from "@mui/material";
import { DefaultButton } from "../../common";
import { MapMarker, Map, ZoomControl } from "react-kakao-maps-sdk";
import "./../../../assets/mapStyle.css";
import { confirmAlert } from "react-confirm-alert";
import ImageCard from "../../card/ImgCard";

const defaultLocate = { lat: Number(REACT_APP_MY_LOCATE_Y), lng: Number(REACT_APP_MY_LOCATE_X) }

const RealestateEditModal: React.FC = () => {
  const indexState = useRecoilValue(selectedImgCardIndexState);
  const showModal = useRecoilValue(realestateEditModalState);
  const resetState = useResetRecoilState(realestateEditModalState);
  const updateChecker = useRecoilValue(mainUpdateChecker);
  const setMainUpdateChecker = useSetRecoilState(mainUpdateChecker);
  const [realEstateInfo, setRealEstateInfo] = useState<types.RealEstate | null>(null);
  const [title, setTitle] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [price, setPrice] = useState<number>(0);
  const [imageFile, setImageFile] = useState<string[]>([]);
  const [imageBin, setImageBin] = useState<any[]>([]);
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
  const [administration_cost2, setAdministration_cost2] = useState<number>(0);
  const [latitude, setLatitude] = useState<number | null>(null);
  const [longitude, setLongitude] = useState<number | null>(null);
  const [clickedPosition, setClickedPosition] = useState<{ lat: number; lng: number } | null>(null);
  const [isTextareaDisabled, setIsTextareaDisabled] = useState(true);
  const [hoverdPosition, setHoverdPosition] = useState<{ lat: number; lng: number } | null>(null);
  const [mapAddressString, setMapAddressString] = useState<string>('');
  const [mapCenter, setMapCenter] = useState<{ lat: number; lng: number }>(defaultLocate);
  const [markers, setMarkers] = useState([{
    position: defaultLocate,
    content: REACT_APP_NAME,
  }]);
  const [mapViewLevel, setMapViewLevel] = useState<number>(4);
  const [zoomable, setZoomable] = useState<boolean>(false);

  useEffect(() => {
    if (mapAddressString === '') return;
    const ps = new kakao.maps.services.Places();
    ps.keywordSearch(mapAddressString, (data, status, _pagination) => {
      if (status === kakao.maps.services.Status.OK) {
        const bounds = new kakao.maps.LatLngBounds();
        let markers = [];

        for (var i = 0; i < data.length; i++) {
          markers.push({
            position: {
              lat: Number(data[i].y),
              lng: Number(data[i].x),
            },
            content: data[i].place_name,
          })
          bounds.extend(new kakao.maps.LatLng(Number(data[i].y), Number(data[i].x)));
        }
        setMarkers(markers);

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
        setMapCenter({lat:markers[0].position.lat, lng: markers[0].position.lng});
      }
    })
  }, [mapAddressString])

  useEffect(() => {
    setImageBin([null,null,null,null,null,null,null,null,null,null,]);
    setRealEstateInfo(null);
    setTitle('');
    setDescription('');
    setPrice(0);
    setSoldout(false);
    setUploadedId('');
    setRelay_object_type('');
    setLocation('');
    setArea(0);
    setTransaction_type('');
    setResidence_availability_date('');
    setAdministrative_agency_approval_date('');
    setNumber_of_cars_parked(0);
    setDirection('');
    setAdministration_cost(0);
    setAdministration_cost2(0);
    setLatitude(null);
    setLongitude(null);
    setClickedPosition(null);
    setIsTextareaDisabled(true);
    setHoverdPosition(null);
    setMapAddressString('');
    setMapCenter(defaultLocate);
    setMarkers([{
      position: defaultLocate,
      content: REACT_APP_NAME,
    }]);
    setMapViewLevel(4);
    setZoomable(false);
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
      setImageFile([]);
      for (let i= 1; i <= 10; i++){
        const imgData = await getImageData(res.data.id, i)
        const imgDataUrl = imgData === null ? require('../../../assets/sampleroom.png') : URL.createObjectURL(imgData);
        setImageFile((prevState) => [...prevState, imgDataUrl]);
        // setImageBin((prevState) => [...prevState, imgData]);
      }
      if (res.data.latitude && res.data.longitude) {
        setMapCenter({ lat: res.data.latitude, lng: res.data.longitude });
      }
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

  const ModifyImageSubmit = async (target: string, index: number) => {
    if (realEstateInfo === null) return;
    const newImg : File = imageBin[indexState.index];
    if (newImg === null){
      if (await handleEmptySubmit() === true){
        try {
          const response = await axios.delete(
            `/api/realestate/image/${showModal.realestateId}/${index}`,
            { withCredentials: true, headers: { 'Content-Type': 'multipart/form-data' } }
          );
          toast.success("내리기 성공");
          pageUpdateChecker();
        } catch (err: any) {
          toast.error("내리기 실패");
          toast.error(err.response.data.message);
        }
      }
      return;
    }
    if (await handleConfirmSubmit('') === false) return;
    try {
      const response = await axios.patch<types.RealEstate>(
        `/api/realestate/${showModal.realestateId}`,
        {
          [target]: newImg,
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

  const ModifyDataSubmit = async (target: string, newData : any) => {
    if (realEstateInfo === null) return;
    if (newData === '' || newData === null || newData === 0){
      if (await handleEmptySubmit() === false) return;
    }
    else {
      if (await handleConfirmSubmit(newData) === false) return;
    }
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

  const handleModifyAdministration_cost2Submit = (event: React.FormEvent) => {
    event.preventDefault();
    ModifyDataSubmit("administration_cost2", administration_cost2);
  };

  const handleModifyMapSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    const modifyLatLong = async () => {
      try{
        await ModifyDataSubmit("latitude", latitude);
        await ModifyDataSubmit("longitude", longitude);
      } catch (err : any) {
        toast.error("위치 수정 실패");
      } 
    }
    modifyLatLong();
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
    let target = "";
    switch (indexState.index) {
      case 0:
        target = "image"; break;
      case 1:
        target = "image2"; break;
      case 2:
        target = "image3"; break;
      case 3:
        target = "image4"; break;
      case 4:
        target = "image5"; break;
      case 5:
        target = "image6"; break;
      case 6:
        target = "image7"; break;
      case 7:
        target = "image8"; break;
      case 8:
        target = "image9"; break;
      case 9:
        target = "image10"; break;
      default : break;
    }
    if (target === ""){
      toast.info("이미지 슬롯 지정해주세요");
      return;
    }
    if (imageBin[indexState.index] === null){
      toast.info("기존과 동일한 이미지입니다");
      return;
    }
    ModifyImageSubmit(target, indexState.index);
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

  const setImageFileAtIndex = (indexToUpdate: number, newValue: string) => {
    setImageFile((prevImageFile) => {
      const updatedImageFile = [...prevImageFile];
      updatedImageFile[indexToUpdate] = newValue;
      return updatedImageFile;
    });
  };

  const setImageBinAtIndex = (indexToUpdate: number, newBin: File) => {
    setImageBin((prevImageBin) => {
      const updatedImageBin = [...prevImageBin];
      updatedImageBin[indexToUpdate] = newBin;
      return updatedImageBin;
    });
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (indexState.index === -1){
      return;
    }
    if (e.target.files && e.target.files.length > 0) {
      const selectedFile = e.target.files[0];
      if (selectedFile.size >= ((1 << 20) * 10)){
        toast.info("10M이하 파일만 가능합니다.")
        return;
      }
      setImageFileAtIndex(indexState.index, URL.createObjectURL(selectedFile));
      setImageBinAtIndex(indexState.index, selectedFile);
    }
  };

  const handleBtnCondCheck = (e: any) => {
    if (indexState.index === -1){
      e.preventDefault();
      toast.info("슬롯을 지정해주세요");
    }
  }

  const getImageData = async (id: number, index: number) => {
    try {
      const imgDataRes = await axios.get('/api/realestate/image/' + id + '/' + index, {
        withCredentials: true,
        responseType: 'blob'
      });
      return imgDataRes.data;
    } catch (error) {
      return null;
    }
  };

  const handleTextareaDisable = (value : string) => {
    setIsTextareaDisabled(true);
    if (value === "office")
      setRelay_object_type("office")
    else if (value === "house")
      setRelay_object_type("house")
    else if (value === "room")
      setRelay_object_type("room")
    else if (value === "mall")
      setRelay_object_type("mall")
    else if (value === "land")
      setRelay_object_type("land")
    else{
      setIsTextareaDisabled(false);
      setRelay_object_type("");
    }
  };

  const handleMapClick = (mouseEvent: any, coords: any) => {
    if (zoomable === false) return;
    const lat = coords.latLng.getLat(); // 클릭한 위치의 위도
    const lng = coords.latLng.getLng(); // 클릭한 위치의 경도
    setClickedPosition({ lat, lng });
  };

  const handleMapSelect = () => {
    if (!clickedPosition)
      return;
    try{
      setLatitude(clickedPosition.lat);
      setLongitude(clickedPosition.lng);
      toast.success("위치 지정 성공");
    }catch(err:any){
      toast.error("위치지정 실패");
    }
  };

  const handleMapHoverdSelect = () => {
    if (!hoverdPosition)
      return;
    try{
      setLatitude(hoverdPosition.lat);
      setLongitude(hoverdPosition.lng);
      toast.success("위치 지정 성공");
    }catch(err:any){
      toast.error("위치지정 실패");
    }
  };

  const handleConfirmSubmit = async (newData: any): Promise<boolean> => {
    return new Promise<boolean>((resolve) => {
      confirmAlert({
        customUI: ({ onClose }) => {
          return (
            <div className="react-confirm-alert-overlay">
              <div className="react-confirm-alert">
                <h1>{newData}</h1>
                <h3>수정하시겠습니까?</h3>
                <div className="react-confirm-alert-button-group">
                  <button onClick={onClose}>아니오</button>
                  <button
                    onClick={async () => {
                      onClose();
                      resolve(true);
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
    });
  };


  const handleEmptySubmit = async () => {
    return new Promise(async (resolve) => {
      confirmAlert({
        customUI: ({ onClose }) => {
          return (
            <div className="react-confirm-alert-overlay">
              <div className="react-confirm-alert">
                <h1>빈값으로 수정하시겠습니까?</h1>
                <div className="react-confirm-alert-button-group">
                  <button onClick={onClose}>아니오</button>
                  <button
                    onClick={async () => {
                      onClose();
                      resolve(true);
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
    });
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
                    <ImageCard images={imageFile}/>
                  </Grid>
                  <Grid item xs={5} display="flex" justifyContent="center" alignItems="center">
                    <h3>선택된 사진 슬롯 번호 :&nbsp;{ indexState.index === -1 ? '미지정' : indexState.index + 1 }</h3>
                  </Grid>
                  <Grid item xs={2.5} display="flex" justifyContent="center" alignItems="center">
                    <Button  variant="contained" component="label"
                      sx={{ width: "97%", }}
                    >
                      슬롯 사진 선택
                    <input
                      type="file"
                      id="image"
                      accept="image/*"
                      onChange={handleImageChange}
                      onClick={handleBtnCondCheck}
                      hidden
                    />
                    </Button>
                    
                  </Grid>
                  <Grid item xs={2.5} display="flex" justifyContent="center" alignItems="center">
                    <DefaultButton
                      onClick={handleModifyImageSubmit}
                      sx={{ width: "100%" }}
                    >
                      선택 이미지 수정하기
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
              <h3>거래완료 여부&nbsp;</h3>
              <label>
                  <input type="radio" name="soldout" id="soldout" defaultChecked={!soldout} value="true" onChange={(e) => setSoldout(false)} /> 거래 중
              </label>
              <label>
                  <input type="radio" name="soldout" id="soldout" defaultChecked={soldout} value="false" onChange={(e) => setSoldout(true)} /> 거래 완료
              </label>
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifySoldOutSubmit}
                sx={{ width: "100%" }}
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
                sx={{ width: "100%" }}
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
                sx={{ width: "100%" }}
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
                sx={{ width: "100%" }}
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
              <h3>중계대상물종류</h3>
              <label>
                  <input type="radio" name="relay_object_type" id="relay_object_type" onChange={(e) => handleTextareaDisable("office")} /> 아파트 / 오피스텔
              </label>
              <label>
                  <input type="radio" name="relay_object_type" id="relay_object_type" onChange={(e) => handleTextareaDisable("house")} /> 주택
              </label>
              <label>
                  <input type="radio" name="relay_object_type" id="relay_object_type" onChange={(e) => handleTextareaDisable("room")} /> 원룸 / 투룸
              </label>
              <br></br>
              <label>
                  <input type="radio" name="relay_object_type" id="relay_object_type" onChange={(e) => handleTextareaDisable("mall")} /> 상가
              </label>
              <label>
                  <input type="radio" name="relay_object_type" id="relay_object_type" onChange={(e) => handleTextareaDisable("land")} /> 토지
              </label>
              <label>
                  <input type="radio" name="relay_object_type" id="relay_object_type" onChange={() => handleTextareaDisable("")} /> 직접입력&nbsp;
              </label>
              <TextField
                sx={{width : "40%"}}
                id="relay_object_type"
                label="중계대상물 종류"
                variant="outlined"
                size="small"
                onChange={(event) =>
                  setRelay_object_type(event.target.value)
                }
                onKeyDown={handleModifyKey}
                disabled={isTextareaDisabled}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyRelay_object_typeSubmit}
                sx={{ width: "100%" }}
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
                sx={{ width: "100%" }}
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
                sx={{ width: "100%" }}
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
                sx={{ width: "100%" }}
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
                sx={{ width: "100%" }}
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
                sx={{ width: "100%" }}
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
                sx={{ width: "100%" }}
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
                sx={{ width: "100%" }}
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
                sx={{ width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* 사용료 수정 */}
            <Grid
              item
              xs={4}
              display="flex"
              justifyContent="center"
              alignItems="center"
            >
              <TextField
                fullWidth
                id="administration_cost2"
                label="사용료"
                variant="outlined"
                size="small"
                onChange={(event) =>
                  setAdministration_cost2(Number(event.target.value))
                }
                onKeyDown={handleModifyKey}
              />
            </Grid>
            <Grid item xs={1} display="flex" justifyContent="center" alignItems="center">
              <DefaultButton
                onClick={handleModifyAdministration_cost2Submit}
                sx={{ width: "100%" }}
              >
                수정하기
              </DefaultButton>
            </Grid>
            {/* 지도수정 */}
            <Grid container columns={23} columnSpacing={2}>
              <Grid item xs={1}/>
              <Grid item xs={21} sx={{ border: '1px solid black', borderRadius: '5px' }}>
                <Grid container columns={5} columnSpacing={2} onClick={() => setZoomable(true)}>
                  <Grid item xs={5} display="flex" justifyContent="center" alignItems="center">
                    <Map
                      className="myMap"
                      style={{ width: "100%", height: "500px" }}
                      center={mapCenter}
                      level={mapViewLevel}
                      onClick={(mouseEvent: any, coords: any) => {
                        handleMapClick(mouseEvent, coords);
                      }}
                      zoomable={zoomable}
                    >
                      <ZoomControl />
                      {markers
                      .map((realEstate, i) => (
                        realEstate && realEstate.position && realEstate.content &&
                        <MapMarker
                          position={{ lat: realEstate.position.lat, lng: realEstate.position.lng }}
                        >
                          <div
                            className='mapMarkers'
                            onMouseEnter={() => setHoverdPosition({ lat: realEstate.position.lat, lng: realEstate.position.lng })}
                            onClick={handleMapHoverdSelect}
                          >
                            {realEstate.content}
                          </div>
                        </MapMarker>
                      ))}
                      {clickedPosition && (
                        <MapMarker position={{ lat: clickedPosition.lat, lng: clickedPosition.lng }}>
                          <div
                            className='mapMarkers'
                            onClick={handleMapSelect}
                          >직접 선택
                          </div>
                        </MapMarker>
                      )}
                      {realEstateInfo.latitude && realEstateInfo.longitude &&
                        <MapMarker position={{ lat: realEstateInfo.latitude, lng: realEstateInfo.longitude }}>
                          <div style={{textAlign:"center", width:"15vh"}}>기존위치</div>
                        </MapMarker>
                      }
                    </Map>
                  </Grid>
                  <Grid item xs={5} display="flex" justifyContent="center" alignItems="center">
                    <h3>주소로 검색&nbsp;</h3>
                    <textarea
                      style={{width : "50%"}}
                      onChange={(e) => setMapAddressString(e.target.value)}
                    ></textarea>
                    {/* <Button onClick={() => setMapViewLevel((level) => (level > 1 ? level - 1 : level))}>확대</Button>
                    <Button onClick={() => setMapViewLevel((level) => (level < 14 ? level + 1 : level))}>축소</Button>
                    (확대레벨 {mapViewLevel}) */}
                  </Grid>
                  {latitude && longitude && 
                    <Grid item xs={5} display="flex" justifyContent="center" alignItems="center">
                    <h3>선택된 위도&nbsp;</h3>{latitude}&nbsp;
                    <h3>경도&nbsp;</h3>{longitude}&nbsp;
                    </Grid>
                  }
                  <Grid item xs={5} display="flex" justifyContent="center" alignItems="center">
                    <DefaultButton
                      onClick={handleModifyMapSubmit}
                      sx={{ width: "100%" }}
                    >
                      수정하기
                    </DefaultButton>
                  </Grid>
                </Grid>
              </Grid>
              <Grid item xs={1}/>
            </Grid>
            {/* end */}
          </Grid>
        )}
      </Stack>
      <br/><br/><br/><br/>
      </ModalScrollableWrapper>
    </ModalBase>
  );
};

export default RealestateEditModal;