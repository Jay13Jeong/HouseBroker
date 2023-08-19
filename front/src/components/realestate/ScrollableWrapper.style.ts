import styled from "@emotion/styled";

export const ScrollableWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  overflow: auto;
  max-height: 90%; /* 최대 높이 설정 (스크롤이 생길 높이) */
  width: 100%;

  form {
    width: 100%;
  }

  .mapMarkers {
    text-align: center;
    width: 15.6vh;
  }

  .mapMarkers:hover {
    text-align: center;
    width: 15.6vh;
    background-color: skyblue;
  }
`;

export const ModalScrollableWrapper = styled.div`
  max-height: 900px; /* 높이 조정 */
  overflow-y: auto;
`;