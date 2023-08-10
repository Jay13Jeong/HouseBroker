import styled from "@emotion/styled";
import { Grade } from "@mui/icons-material";
import { Grid } from "@mui/material";
import zIndex from "@mui/material/styles/zIndex";

export const modalSx = {
    padding: '20px',
    backgroundColor: 'background.paper',
    borderRadius: 2,
    position: 'absolute' as 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: '80%',
}

export const closeButtonSx = {
    position: "absolute",
    right: 8,
    top: 8,
}

export const CustomGrid = styled(Grid)`
h4 {
margin-bottom: 0px;
}

.typographyInfo {
  white-space: pre-line; /* 줄바꿈을 유지하면서 빈 공간도 유지됨 */
  overflow: visible; /* 내용 넘치는 경우 가리지 않음 */
  text-overflow: unset; /* 생략(...) 표시하지 않음 */
  max-width: 100%;
`;

export const CustomCloseBtn = styled.button`
margin-right: 0;
margin-top: 10px;
padding: 8px 16px;
background-color: #007BFF;
color: #fff;
border: 10px solid;
cursor: pointer;
border-radius: 15px;
display: block;
margin-left: auto;
`;

export const CustomTextWrapper = styled.div`
white-space: normal;
overflow: hidden;
text-overflow: ellipsis;
max-width: 100%;
word-wrap: break-word;
`;

