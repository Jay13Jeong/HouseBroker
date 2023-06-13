import { Link } from "react-router-dom";
import { HeaderWrapper } from "./Header.style";
import logo from "../../assets/logo.png";
import { RoutePath } from "../../common/configData";
import { REACT_APP_HOST } from '../../common/configData';

export default function Header() {
    return (
        <HeaderWrapper>
            <Link to={RoutePath.lobby} style={{ textDecoration: "none" }}>
                <span className="navi-title">
                    <img src={logo} alt="logo" />
                    <span>부동산 사이트</span>
                </span>
            </Link>
            <Link to={RoutePath.postRE} style={{ textDecoration: "none" }}>
                <span className="navi-title">
                    <img src={logo} alt="logo" />
                    <span>매물 올리기</span>
                </span>
            </Link>
            <Link to={RoutePath.patchRE} style={{ textDecoration: "none" }}>
                <span className="navi-title">
                    <img src={logo} alt="logo" />
                    <span>매물 수정</span>
                </span>
            </Link>
            <Link to={RoutePath.delRE} style={{ textDecoration: "none" }}>
                <span className="navi-title">
                    <img src={logo} alt="logo" />
                    <span>매물 내리기</span>
                </span>
            </Link>
            <span onClick={()=>{window.location.href = "http://" + REACT_APP_HOST + "/api/auth/google/login"}} style={{ textDecoration: "none" }}>
                <span className="navi-title">
                    <img src={logo} alt="logo" />
                    <span>로그인</span>
                </span>
            </span>
            <span onClick={()=>{window.location.href = "http://" + REACT_APP_HOST + "/api/auth/logout"}} style={{ textDecoration: "none" }}>
                <span className="navi-title">
                    <img src={logo} alt="logo" />
                    <span>로그아웃</span>
                </span>
            </span>
        </HeaderWrapper>
    );
}