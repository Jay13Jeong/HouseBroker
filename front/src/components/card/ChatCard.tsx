import React, { useEffect, useRef, useState } from 'react';
import { ChatBox, ChatContainer, MessageContainer, MessageText, TopBotSection } from './ChatCard.style';
import { Button, TextField, Typography } from '@mui/material';
import { useSocket } from '../../common/states/socketContext';
import { socketIdState, chatRoomState, messageState, } from '../../common/states/recoilModalState';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import { useAuth } from '../../common/states/AuthContext';
import { REACT_APP_NAME } from '../../common/configData';
import axios from 'axios';
import * as types from "../../common/types/User";
import { toast } from 'react-toastify';
import { DefaultButton } from '../common';

const ChatCard: React.FC<{setShowChat : (status : boolean) => void}> = ({setShowChat}) => {
    const [newMessage, setNewMessage] = useState('');
    const socket = useSocket();
    const socketId = useRecoilValue(socketIdState);
    const Auth = useAuth();
    const [targetId, setTargetId] = useState<string | null>(null);
    const [selectPage, setSelectPage] = useState<boolean>(true);
    const chatRooms = useRecoilValue(chatRoomState);
    const setChatRoomState = useSetRecoilState(chatRoomState);
    const messages = useRecoilValue(messageState);
    const setMessages = useSetRecoilState(messageState);
    const [selectChatNo, setSelectChatNo] = useState<number>(0);
    const [chatRoomName, setChatRoomName] = useState<string>('');
    const chatContainerRef = useRef<HTMLDivElement | null>(null);
    const [page, setPage] = useState(1);
    const pageSize = 5; // 한 페이지당 표시할 개수
    const displayedChatRooms = chatRooms.chatRooms.slice(
      (page - 1) * pageSize,
      page * pageSize
    );
    const isLastPage = page === Math.ceil(chatRooms.chatRooms.length / pageSize);
    const isFirstPage = page === 1;
  
    const handlePrevPage = () => {
        // if (isFirstPage) return ;
      if (page > 1) {
        setPage(page - 1);
      }
    };
  
    const handleNextPage = () => {
        // if (isLastPage) return;
      if (page < Math.ceil(chatRooms.chatRooms.length / pageSize)) {
        setPage(page + 1);
      }
    };

    useEffect(() => {
        // if (selectChatNo === -1) return;
        setSelectPage(false);
        scrollDown();
    }, [messages]);

    useEffect(() => {
        scrollDown();
    }, [selectPage]);

    useEffect(() => {
        if (!Auth.isLoggedIn){
            setShowChat(false);
            toast.info("로그인 후 이용가능합니다");
            return;
        }
        setSelectPage(true);
        if (!socket.stomp.connected){
            setShowChat(false);
            return;
        }
        // setMessagesTmp(messages.chat);
        if (Auth.permitLevel < 10) {
            getChatsFromAdmin();
        }
        else {
            getChatRooms();
        }
        scrollDown();
    }, []);

    const scrollDown = () => {
        if (chatContainerRef.current) 
        chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
    }

    const getChatRooms = async () => {
        try {
            const res = await axios.get<types.ChatRoom[]>(
              `/api/chat/rooms`,
              { withCredentials: true }
            );
            setChatRoomState({ chatRooms : res.data })
            // setChatRoomState((pre) => ({ chatRooms : [...pre.chatRooms, ...res.data] }))////////////
          } catch (err: any) {
            toast.info("채팅방 정보 불러오기 실패")
          }
    }

    const handleSendClick = (e : any) => {
        if (e.key === 'Enter' && e.keyCode !== 13) return;
        if (newMessage.trim() !== '') {
            if (Auth.permitLevel < 10){
                socket.sendMessage('/app/send/admin', newMessage);
            } else if (targetId !== null) {
                socket.sendMessage('/app/send/' + targetId, newMessage);
            }
            if (!Auth.user) return;
            // let dummyUser : types.User = Auth.user;
            // dummyUser.email = "";
            // dummyUser.id = -1;
            const newChat : types.Chat = {
                message : newMessage,
                sender : Auth.user,
                timestamp : new Date().toLocaleString(),
                id : -1,
                receiver : Auth.user,
                chatRoom : null,
            }
            const isAdmin = Auth.permitLevel === 10;
            const roomNo = isAdmin ? selectChatNo : 0;
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
            setNewMessage('');
        }
    };

    const getChats = async (no : number) => {
        try {
            const res = await axios.get<types.Chat[]>(
              `/api/chat/` + no,
              { withCredentials: true }
            );
            const sender = res.data[0].sender;
            const roomNo = sender.email === Auth.user?.email ? res.data[0].receiver.id : sender.id;
            // setMessagesTmp((prevChatState) => ({
            //     ...prevChatState,
            //     [roomNo] : res.data,
            // }));
            // setMessages({ chat: messagesTmp });
            setMessages((prevChatState) => ({
                chat : {
                    ...prevChatState.chat,
                    [roomNo] : res.data,
                }
            }));
            setSelectChatNo(roomNo);
        } catch (err: any) {
            toast.info("채팅방 정보 불러오기 실패")
        }
    }

    const getChatsFromAdmin = async () => {
        try {
            const res = await axios.get<types.Chat[]>(
              `/api/chat/admin`,
              { withCredentials: true }
            );
            res.data.sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());
            // setMessagesTmp((prevChatState) => ({
            //     ...prevChatState,
            //     0 : res.data,
            // }));
            setMessages((prevChatState) => ({
                chat : {
                    ...prevChatState.chat,
                    0 : res.data,
                }
            }));
            // setMessages({ chat: messagesTmp });
            // setSelectPage(false);
            setSelectChatNo(0);
        } catch (err: any) {
            toast.info("채팅방 정보 불러오기 실패")
        }
    }

    const handleRoomClick = (roomNo : number, users : types.User[], roomName : string) => {
        getChats(roomNo);
        setChatRoomName(roomName);
        if (users[0].email === Auth.user?.email){
            setTargetId(users[1].id.toString());
        }else{
            setTargetId(users[0].id.toString());
        }
    };

    const manageMsg = (msg : string) => {
        return msg.replace(/(.{30})/g, '$1\n');
    }

    return (
        <ChatBox>
            <TopBotSection onClick={() => setShowChat(false)}>
                <Button>
                    채팅창 닫기
                </Button>
            </TopBotSection>
            {chatRoomName && <TopBotSection><h5>{chatRoomName}</h5></TopBotSection>}
            
            {selectPage ?
            <ChatContainer ref={chatContainerRef}>
            {chatRooms.chatRooms.length === 0 ? (
              <>문의가 없습니다</>
            ) : (
              <>
                {displayedChatRooms.map((room, index) => (
                  <DefaultButton
                    key={room.id}
                    className="chatRoomBtn"
                    onClick={(e) => {
                      e.preventDefault();
                      handleRoomClick(room.id, room.users, room.roomName);
                    }}
                  >
                    {room.roomName}
                    <br />
                  </DefaultButton>
                ))}
                {chatRooms.chatRooms.length > pageSize && (
                  <div>
                    {isFirstPage ? 
                    <Button
                        sx={{
                            backgroundColor: 'white',
                            color: 'gray',
                        }}
                    >
                        이전
                    </Button>
                    : <Button onClick={handlePrevPage}>이전</Button>
                    }
                    {isLastPage ? 
                    <Button
                        sx={{
                            backgroundColor: 'white',
                            color: 'gray',
                        }}
                    >
                        다음
                    </Button>
                    : <Button onClick={handleNextPage}>다음</Button>
                    }
                  </div>
                )}
              </>
            )}
          </ChatContainer>
            :
            <>
            <ChatContainer ref={chatContainerRef}>
                {messages.chat[selectChatNo].map((message : types.Chat, index : number) => (
                <MessageContainer key={index} isMine={message.sender.email === Auth.user?.email}>
                <MessageText isMine={message.sender.email === Auth.user?.email}>
                    { manageMsg(message.message) }
                </MessageText>
                </MessageContainer>
            ))}
            </ChatContainer>
            <TopBotSection>
            <TextField
            className='textField'
            label="메시지 입력"
            variant="outlined"
            size="small"
            value={newMessage}
            onChange={e => setNewMessage(e.target.value)}
            onKeyDown={(e) => {
                if (e.key === 'Enter'){
                    handleSendClick(e);  
                }
              }}
            />
            <Button className='textFieldBtn' variant="contained" color="primary" onClick={handleSendClick}>
            전송
            </Button>
            </TopBotSection>
            </>
            }
        </ChatBox>
    );
};

export default ChatCard;