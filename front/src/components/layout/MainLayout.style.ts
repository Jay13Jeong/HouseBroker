import styled from "@emotion/styled";
import { pingu1, pingu2, pingu3, pingu4, pingu5, pingu6, pingu7, pingu8 } from "../../assets/background";

function randomNum(){
    const max = 8; //마지막 이미지 번호.
    const min = 1; //최초 이미지 번호.
    return Math.floor(Math.random() * (max - min) + min);
}

const backgrounds = [pingu1, pingu2, pingu3, pingu4, pingu5, pingu6, pingu7, pingu8];

const backgroundImg = backgrounds[randomNum()];

const calculateHeight = () => {
  const absSize = 900;
  const absMinSize = 900;
  let screenHeight = window.innerHeight > absSize ? absSize : window.innerHeight; // 현재 화면의 높이
  if (screenHeight > absSize)
    screenHeight = absSize;
  else if (screenHeight < absMinSize)
    screenHeight = absMinSize
  const additionalHeight = screenHeight * 0.2;
  return `${screenHeight + additionalHeight}px`;
};

const calculateWidth = () => {
  const screenWidth = window.innerWidth;
  const absSize = 2560;
  if (screenWidth > absSize)
    return `${absSize}px`;
  return `${screenWidth}px`;
};

const middleTopPx = '55px'; 

export const TableWrapper = styled.div`
  display: table;
  width: ${calculateWidth()};
  table-layout: fixed; /* 너비를 동일하게 유지 */
  height: ${calculateHeight()};
  overflow-y: auto;
  overflow-x: hidden;
`;

export const TableRow = styled.div`
  display: table-row;
`;

export const TableCell = styled.div`
  display: table-cell;
  vertical-align: top;
`;

export const MainLayoutWrapper = styled(TableCell)`
position: relative;
top: ${middleTopPx};
// background-image: url(${backgroundImg});
margin: 0 auto;
width: 100%;
background-size: cover;
background-repeat: no-repeat;
display: flex;
justify-content: center;
align-items: center;
padding: 10px; /* 내용과 화면 경계 간의 여백 */
`;

export const LeftDiv = styled(TableCell)`
  width: 13%; /* 왼쪽 분할의 너비 조정 */
  // background-color: lightblue;
`;

export const RightDiv = styled(TableCell)`
  width: 10%; /* 오른쪽 분할의 너비 조정 */
  // background-color: lightgreen;
`;

export const BottomRow = styled(TableRow)`
  display: table-row;
`;

export const BottomCellLeft = styled(TableCell)`
  display: table-cell;
  // background-color: lightyellow;
`;

export const BottomCellMiddle = styled(TableCell)`
  position: relative;
  top: ${middleTopPx};
  display: table-cell;
  // background-color: lightpink;
`;

export const BottomCellRight = styled(TableCell)`
  display: table-cell;
  // background-color: lightseagreen;
`;