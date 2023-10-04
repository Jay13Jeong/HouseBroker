import {useEffect, useState } from "react";
import { useRecoilValue, useResetRecoilState } from "recoil";
import { dmModalState } from "../../../common/states/recoilModalState";
// import DmCardButtonList from "../../card/dm/DmCardButtonList";
import ModalBase from "../../modal/ModalBase";
import { REACT_APP_HOST } from "../../../common/configData";
import axios from "axios";

import { Typography, Stack } from '@mui/material'
import { DefaultLinearProgress } from "../../common";

function DMModal() {
    const showModal = useRecoilValue(dmModalState);
    const resetState = useResetRecoilState(dmModalState);
    const [dmList, setDmList] = useState<string[]>();
    useEffect(() => {
        const getDmList = async () => {
            try {
                await axios.get(`http://` + REACT_APP_HOST + `/api/chatdm/rooms`, {withCredentials: true})
                .then((res) => {
                    setDmList([...res.data]);
                })
            }
            catch (e: any) {
                // error
            }
        };
        if (showModal)
            getDmList();
    }, [showModal]);

    return (
        <ModalBase open={showModal} reset={resetState} closeButton>
        </ModalBase>
    )
}

export default DMModal;