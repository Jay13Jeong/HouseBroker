import React, { useState } from 'react';
import { ChatBox, ChatContainer, MessageContainer, MessageText, TopSection } from './ChatCard.style';
import { Button, TextField, Typography } from '@mui/material';

const ChatCard: React.FC<{setShowChat : (status : boolean) => void}> = ({setShowChat}) => {
    const [newMessage, setNewMessage] = useState('');

    const messages = [
        { text: '안녕하세요!', isMine: true },
        { text: '반가워요!', isMine: false },
        { text: '오늘 날씨는 어떤가요?', isMine: true },
        { text: '매우 화창해요!', isMine: false },
    ];

    const handleSendClick = () => {
        if (newMessage.trim() !== '') {
          // 소켓전송하기...

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
            <ChatContainer>
                {messages.map((message, index) => (
                <MessageContainer key={index} isMine={message.isMine}>
                <MessageText isMine={message.isMine}>{message.text}</MessageText>
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
        </ChatBox>
    );
};

export default ChatCard;