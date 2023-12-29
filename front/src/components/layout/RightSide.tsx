import { Link } from "react-router-dom";
import { RightSideWrapper } from "./RightSide.style"
import logo from "../../assets/logo.png";
import { RoutePath } from "../../common/configData";
import { REACT_APP_PHONE_NUMBER } from '../../common/configData';
import { Avatar } from '@mui/material';
import ChatCard from "../card/ChatCard";
import { useEffect, useState } from "react";
import axios from "axios";
import { Chat, ChatRoom } from "../../common/types/User";
import { toast } from "react-toastify";
import { useRecoilValue, useSetRecoilState } from "recoil";
import { chatRoomState, messageState, socketConnectState, socketIdState } from "../../common/states/recoilModalState";
import { useAuth } from "../../common/states/AuthContext";
import { useSocket } from "../../common/states/socketContext";

export default function RightSide() {
    const Auth = useAuth();
    const [showChat, setShowChat] = useState(false);
    const setChatRoomState = useSetRecoilState(chatRoomState);
    const setMessages = useSetRecoilState(messageState);
    const messages = useRecoilValue(messageState);
    const [newAlert, setNewAlert] = useState('');
    const socket = useSocket();
    const socketId = useRecoilValue(socketIdState);
    const socketState = useRecoilValue(socketConnectState);

    useEffect(() => {
        if (!socket.stomp.connected){
            return;
        }
        if (socketId.socketId === "") return;
        // toast.info("/topic/message 구독진행")
        // getChatRooms();
        socket.addSubscribe('/topic/message' + socketId.socketId, (message) => {
            // toast.info("뭔가 도착: " + message.body );
           const newChat : Chat =  JSON.parse(message.body);
           const sender = newChat.sender;
           const isAdmin = Auth.permitLevel === 10;
        //    const roomNo = isAdmin ? (sender.email === Auth.user?.email ? newChat.receiver.id : sender.id) : 0;
           let roomNo = newChat.chatRoom ? newChat.chatRoom.id : 0;
           roomNo = isAdmin ? roomNo : 0;
        newChat.timestamp = new Date(newChat.timestamp);
           try{
            setMessages((prevChatState) => ({
                chat: {
                  ...prevChatState.chat,
                  [roomNo] : [...prevChatState.chat[roomNo], newChat],
                },
            }));
           }catch{
            setMessages((prevChatState) => ({
                chat: {
                  ...prevChatState.chat,
                  [roomNo] : [newChat],
                },
            }));
           }
           setNewAlert(newChat.message);
        });
        // socket.addSubscribe('/topic/message2' + socketId.socketId, (message) => {
        //         toast.info("뭔가 도착2");
        //     });
        // toast.info("/topic/message 구독함")
    }, [socketState, socketId]);

    useEffect(() => {
        if (newAlert === '') return ;
        if (newAlert === '대화를 종료합니다.') setShowChat(false);
        if (showChat === false) toast.info(newAlert);
    }, [newAlert]);

    const handleClick = () => {
        setShowChat(true);
    }

    // const getChatRooms = async () => {
    //     try {
    //         const res = await axios.get<ChatRoom[]>(
    //           `/api/chat/rooms`,
    //           { withCredentials: true }
    //         );
    //         setChatRoomState({ chatRooms : res.data })
    //       } catch (err: any) {

    //       }
    // }

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
            {showChat && <ChatCard setShowChat={setShowChat}  />}
        </RightSideWrapper>
    );
}