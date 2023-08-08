import React, { useEffect, useState } from "react";
import { useRecoilValue, useSetRecoilState, useResetRecoilState } from "recoil";
import { myPathModalState } from "../../../common/states/recoilModalState";
import ModalBase from "../../modal/ModalBase";
import "react-confirm-alert/src/react-confirm-alert.css";
import "./../../../assets/confirm-alert.css";
import { Stack } from "@mui/material";
import { ModalScrollableWrapper } from "../../realestate/ScrollableWrapper.style";
import { REACT_APP_NAME, REACT_APP_MY_LOCATE_X, REACT_APP_MY_LOCATE_Y } from '../../../common/configData';
import { Map, MapMarker } from "react-kakao-maps-sdk";

const MyPathModal: React.FC = () => {
   
  const showModal = useRecoilValue(myPathModalState);
  const resetState = useResetRecoilState(myPathModalState);

  useEffect(() => {
  }, [showModal])

  const handleCloseModal = () => {
    resetState();
  };



  return (
    <ModalBase open={showModal.show} onClose={handleCloseModal} closeButton>
      <ModalScrollableWrapper>
      <Stack justifyContent="center" alignItems="center">
        <h2>중개사무소 위치</h2>
        <Map
              className="myMap"
              style={{ width: "500px", height: "500px" }}
              center={{ lat: Number(REACT_APP_MY_LOCATE_Y), lng: Number(REACT_APP_MY_LOCATE_X) }}
              level={3}
            >
              <MapMarker position={{ lat: Number(REACT_APP_MY_LOCATE_Y), lng: Number(REACT_APP_MY_LOCATE_X) }}>
                <div style={{textAlign:"center", width:"15vh"}}>{REACT_APP_NAME}</div>
              </MapMarker>
        </Map>
        <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
      </Stack>
      </ModalScrollableWrapper>
    </ModalBase>
  );
};

export default MyPathModal;