import React, { useEffect, useState } from 'react';
import { ChatBox, ChatContainer, MessageContainer, MessageText, TopSection } from './ChatCard.style';
import { Button, TextField, Typography } from '@mui/material';
import { useSocket } from '../../common/states/socketContext';
import { socketIdState, chatRoomState, messageState, } from '../../common/states/recoilModalState';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import { useAuth } from '../../common/states/AuthContext';
import { REACT_APP_NAME } from '../../common/configData';
import axios from 'axios';
import * as types from "../../common/types/User";
import { toast } from 'react-toastify';

const ChatCard: React.FC<{setShowChat : (status : boolean) => void}> = ({setShowChat}) => {
    const [newMessage, setNewMessage] = useState('');
    // const [messages, setMessages] = useState([
    //     { text: '안녕하세요!', isMine: true },
    //     { text: '반가워요!', isMine: false },
    //     { text: '오늘 날씨는 어떤가요?', isMine: true },
    //     { text: '매우 화창해요!', isMine: false },
    // ]);
    const socket = useSocket();
    const socketId = useRecoilValue(socketIdState);
    const Auth = useAuth();
    const [targetId, setTargetId] = useState<string | null>(null);
    const [selectPage, setSelectPage] = useState<boolean>(true);
    const chatRooms = useRecoilValue(chatRoomState);
    const setChatRoomState = useSetRecoilState(chatRoomState);
    const messages = useRecoilValue(messageState);

    useEffect(() => {
        if (!socket.stomp.connected){
            setShowChat(false);
            return;
        }
        if (Auth.permitLevel < 10) setSelectPage(false);
        else {
            getChatRooms();
        }
        // socket.addSubscribe('/topic/message' + socketId, (message) => {
        //    const tmpMsg = [ ...messages, { text: message.body, isMine: false}];
        //    setMessages(tmpMsg);
        // });
        
    }, []);

    const getChatRooms = async () => {
        try {
            const res = await axios.get<types.ChatRoom[]>(
              `/api/chat/rooms`,
              { withCredentials: true }
            );
            setChatRoomState({ chatRooms : res.data })
          } catch (err: any) {
            toast.info("채팅방 정보 불러오기 실패")
          }
    }

    const handleSendClick = () => {
        if (newMessage.trim() !== '') {
            if (Auth.permitLevel < 10){
                socket.sendMessage('/app/send/admin', Auth.user?.email + ':' + newMessage);
            } else if (targetId !== null) {
                socket.sendMessage('/app/send/' + targetId, REACT_APP_NAME + ':' + newMessage);
            }
            setNewMessage('');
        }
    };

    return (
        <ChatBox>
            <TopSection onClick={() => setShowChat(false)}>
                <Button>
                    채팅창 닫기
                </Button>
            </TopSection>
            {selectPage ?
                <ChatContainer>
            {chatRooms.chatRooms.length === 0 ? <>문의가 없습니다</> : 
                (
                    chatRooms.chatRooms.map((room, index) => (
                        <>
                        {room.roomName}<br/>
                        </>
                    ))
                )
            }
                </ChatContainer>
            :
            <ChatContainer>
                {messages.chat.map((message, index) => (
                <MessageContainer key={index} isMine={message.sender.id === Auth.user?.id}>
                <MessageText isMine={message.sender.id === Auth.user?.id}>{message.message}</MessageText>
                </MessageContainer>
            ))}
            <TextField
            label="메시지 입력"
            variant="outlined"
            size="small"
            value={newMessage}
            onChange={e => setNewMessage(e.target.value)}
            onKeyDown={(e) => {
                if (e.key === 'Enter'){
                    handleSendClick();  
                }
              }}
            />
            <Button variant="contained" color="primary" onClick={handleSendClick}>
            전송
            </Button>
            </ChatContainer>
            }
        </ChatBox>
    );
};

export default ChatCard;