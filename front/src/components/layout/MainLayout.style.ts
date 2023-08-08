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
  const screenHeight = window.innerHeight; // 현재 화면의 높이
  const additionalHeight = screenHeight * 0.2;

  return `${screenHeight + additionalHeight}px`;
};

export const TableWrapper = styled.div`
  display: table;
  width: 100%;
  table-layout: fixed; /* 너비를 동일하게 유지 */
  height: ${calculateHeight()};
  overflow-y: auto;
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
top: 55px; 
// background-image: url(${backgroundImg});
margin: 0 auto;
width: 100%;
background-size: cover;
background-repeat: no-repeat;
display: flex;
justify-content: center;
align-items: center;
overflow-y: auto; /* 스크롤 스타일 적용 */
padding: 10px; /* 내용과 화면 경계 간의 여백 */
`;

export const LeftDiv = styled(TableCell)`
  width: 13%; /* 왼쪽 분할의 너비 조정 */
  // background-color: lightblue;
`;

export const RightDiv = styled(TableCell)`
  width: 13%; /* 오른쪽 분할의 너비 조정 */
  // background-color: lightgreen;
`;

export const BottomRow = styled(TableRow)`
  display: table-row;
`;

export const BottomCellLeft = styled(TableCell)`
  display: table-cell;
  vertical-align: middle; /* 수직 가운데 정렬 */
  text-align: center; /* 수평 가운데 정렬 */
  font-weight: bold;
  background-color: lightyellow;
`;

export const BottomCellMiddle = styled(TableCell)`
  display: table-cell;
  vertical-align: middle; /* 수직 가운데 정렬 */
  text-align: center; /* 수평 가운데 정렬 */
  font-weight: bold;
  background-color: lightpink;
`;

export const BottomCellRight = styled(TableCell)`
  display: table-cell;
  vertical-align: middle; /* 수직 가운데 정렬 */
  text-align: center; /* 수평 가운데 정렬 */
  font-weight: bold;
  background-color: lightseagreen;
`;