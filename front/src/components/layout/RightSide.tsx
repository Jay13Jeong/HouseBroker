import { Link } from "react-router-dom";
import { RightSideWrapper } from "./RightSide.style"
import logo from "../../assets/logo.png";
import { RoutePath } from "../../common/configData";
import { REACT_APP_PHONE_NUMBER } from '../../common/configData';
import { Avatar } from '@mui/material';
import ChatCard from "../card/ChatCard";
import { useState } from "react";

export default function RightSide() {
    const [showChat, setShowChat] = useState(false);

    const handleClick = () => {
        setShowChat(true);
    }

    return (
        <RightSideWrapper>
            <center onClick={handleClick}>
            <Avatar
                className="ImgSection"
                // src={require("../../assets/email-512.jpg")}
                // src={require("../../assets/mail2-512.jpg")}
                src={require("../../assets/email3-green.png")}
                alt="msg_img"
                variant="rounded"
                sx={{ marginTop: 8,marginBottom: 1 }}
                onDragStart={e => e.preventDefault()}
            />
            <h3>문의 남기기</h3>
            </center>
            {showChat && <ChatCard setShowChat={setShowChat} />}
        </RightSideWrapper>
    );
}