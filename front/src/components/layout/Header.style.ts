import styled from "@emotion/styled";

export const HeaderWrapper = styled.header`
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    background-color: #f6f6f8;
    display: inline-flex;
    box-sizing: border-box;
    justify-content: space-between;
    align-items: center;
    height: 50px;
    padding: 5px;
    width: 100%;
    z-index: 999;

    .navi-title {
        display: flex;
        align-items: center;
        img {
            width: auto;
            height: 40px;
        }
        span {
            vertical-align: middle;
            font-size: 2.5rem;
            font-weight: bold;
            font-family: "Jua", sans-serif;
            color: #000000;
        }
    }

    .navi-left {
        display: flex;
        align-items: center;
        img {
            width: auto;
            height: 40px;
        }
        span {
            vertical-align: middle;
            font-size: 2.5rem;
            font-weight: bold;
            font-family: "Jua", sans-serif;
            color: #000000;
        }
    }

    .navi-right-container {
        display: flex;
        align-items: center;
    }

    .navi-right {
        display: flex;
        align-items: center;
        img {
            width: auto;
            height: 40px;
        }
        span {
            vertical-align: middle;
            font-size: 2.5rem;
            font-weight: bold;
            font-family: "Jua", sans-serif;
            color: #000000;
        }
    }
`;