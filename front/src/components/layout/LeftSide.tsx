import { useNavigate } from "react-router-dom";
import { LeftSideWrapper } from "./LeftSide.style"
import { DefaultButton, DefaultButton2 } from "../common";
import { Avatar } from '@mui/material';
import { useSetRecoilState, } from "recoil";
import { myPathModalState, realestateFilterState } from "../../common/states/recoilModalState";
import { REACT_APP_PHONE_NUMBER } from '../../common/configData';
import { RoutePath } from "../../common/configData";

export default function LeftSide() {
    const navigate = useNavigate();
    const setModalState = useSetRecoilState(myPathModalState);
    const setFilterState = useSetRecoilState(realestateFilterState);

    const handleMapClick = () => {
        navigate(RoutePath.map);
    }

    const handleFilterOffice = () => {
        setFilterState({ filter : "office"});
    }

    const handleFilterHouse = () => {
        setFilterState({ filter : "house"});
    }

    const handleFilterRoom = () => {
        setFilterState({ filter : "room"});
    }

    const handleFilterMall = () => {
        setFilterState({ filter : "mall"});
    }

    const handleFilterLand = () => {
        setFilterState({ filter : "land"});
    }

    const handleFilterAll = () => {
        setFilterState({ filter : "default"});
    }

    const handleMyPathModal = () => {
        setModalState({ show : true })
    }

    const handlePhoneClick = () => {
        //전화 걸기.
        window.location.href = 'tel:' + REACT_APP_PHONE_NUMBER;
    }

    return (
        <LeftSideWrapper>
            <Avatar
                className="ImgSection"
                src={require("../../assets/map.png")}
                alt="map_img"
                variant="rounded"
                sx={{ height: 150, marginTop: 8,marginBottom: 1 }}
                onClick={handleMapClick}
                
            />
            <DefaultButton
                className="LeftSideButton"
                onClick={handleFilterAll}
                sx={{ }}
              >
                모두보기
            </DefaultButton>
            <DefaultButton
                className="LeftSideButton"
                onClick={handleFilterOffice}
                sx={{ }}
              >
                아파트 / 오피스텔
            </DefaultButton>
            <DefaultButton
                className="LeftSideButton"
                onClick={handleFilterHouse}
                sx={{ }}
              >
                주택
            </DefaultButton>
            <DefaultButton
                className="LeftSideButton"
                onClick={handleFilterRoom}
                sx={{ }}
              >
                원룸 / 투룸
            </DefaultButton>
            <DefaultButton
                className="LeftSideButton"
                onClick={handleFilterMall}
                sx={{ }}
              >
                상가
            </DefaultButton>
            <DefaultButton
                className="LeftSideButton"
                onClick={handleFilterLand}
                sx={{ }}
              >
                토지
            </DefaultButton>
            <DefaultButton2
                className="MyPathButton"
                onClick={handleMyPathModal}
                sx={{ }}
              >
                오시는 길
            </DefaultButton2>
            <center onClick={handlePhoneClick}>
            <Avatar
                className="ImgPhoneSection"
                src={require("../../assets/phone.jpg")}
                alt="map_img"
                variant="rounded"
                sx={{ marginTop: 8,marginBottom: 1 }}
            />
            <h3>지금 전화걸기</h3>
            </center>
        </LeftSideWrapper>
    );
}