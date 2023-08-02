import styled from "@emotion/styled";

export const RootLayoutContainer = styled.div`
  display: flex;
  flex-direction: column;
  min-height: 100vh;
`;

export const Header = styled.header`
  background-color: #f2f2f2;
  padding: 20px;
`;

export const Main = styled.main`
  flex-grow: 1;
  padding: 20px;
  background-color: black;
`;

export const Section = styled.section`
  margin-bottom: 30px;

  h2 {
    font-size: 24px;
    margin-bottom: 10px;
  }

  form {
    display: flex;
    margin-top: 10px;

    input[type="text"] {
      flex-grow: 1;
      padding: 8px;
      border: 1px solid #ccc;
      border-radius: 4px 0 0 4px;
    }

    button {
      padding: 8px 16px;
      border: none;
      background-color: #007bff;
      color: #fff;
      border-radius: 0 4px 4px 0;
      cursor: pointer;
    }
  }
`;

export const RealEstateList = styled.div`
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
`;

export const RealEstateCard = styled.div`
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 20px;
  text-align: center;

  img {
    width: 100%;
    height: 200px;
    object-fit: cover;
    border-radius: 4px;
    margin-bottom: 10px;
  }

  h3 {
    font-size: 18px;
    margin-bottom: 10px;
  }

  p {
    margin-bottom: 10px;
  }
`;

export const Pagination = styled.div`
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 20px;

  button {
    padding: 8px 12px;
    border: 1px solid #ccc;
    border-radius: 4px;
    cursor: pointer;
  }

  button.active {
    background-color: #007bff;
    color: #fff;
  }
`;