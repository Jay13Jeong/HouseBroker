import styled from "@emotion/styled";
import { Paper, Typography } from '@mui/material';

export const ChatBox = styled.div`
  width: 40vh;
  height: 50vh;  
  position: absolute;
  top: 7%;
  right: 1%;
  z-index: 20;
  box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.3);
`;

export const TopSection = styled.div`
display: flex;
width: 100%;
padding: 0 2% 0 2%;
background-color: white;
box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.3);
justify-content: center;
`;

export const ChatContainer = styled(Paper)`
width: 100%;
height: 100%;
padding: 2%;
display: flex;
flex-direction: column;
justify-content: flex-end;
box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.3);
// background-color: gray;
`;

export const MessageContainer = styled.div<{ isMine?: boolean }>`
  display: flex;
  justify-content: flex-start;
  margin-bottom: 1%;
  ${({ isMine }) => isMine && 'justify-content: flex-end;'}
`;

export const MessageText = styled(Typography)<{ isMine?: boolean }>`
  padding: 1% 2%;
  border-radius: 10px;
  background-color: #f0f0f0;
  ${({ isMine }) => isMine && 'background-color: #DCF8C6;'}
`;