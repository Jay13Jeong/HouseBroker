import styled from "@emotion/styled";

export const ScrollableWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  overflow: auto;
  max-height: 90%; /* 최대 높이 설정 (스크롤이 생길 높이) */
`;