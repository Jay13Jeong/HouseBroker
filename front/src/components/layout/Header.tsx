import { Link } from "react-router-dom";
import { HeaderWrapper } from "./Header.style";
import logo from "../../assets/logo.png";
import { RoutePath } from "../../common/configData";
import { REACT_APP_NAME } from '../../common/configData';
import { useAuth } from "../../common/states/AuthContext";
import { useSocket } from "../../common/states/socketContext";

export default function Header() {
    const Auth = useAuth();
    const socket = useSocket();

    return (
        <HeaderWrapper>
        <Link to={RoutePath.lobby} style={{ textDecoration: "none" }}>
            <span className="navi-left">
            <img src={logo} alt="logo" />
            <span>메인으로</span>
            </span>
        </Link>
        <Link to={RoutePath.lobby} style={{ textDecoration: "none" }}>
            <span className="navi-title">
            <img src={logo} alt="logo" />
            <span>{REACT_APP_NAME}</span>
            </span>
        </Link>
        {/* Right Container */}
        <span className="navi-right-container">
            {Auth.isLoggedIn && Auth.permitLevel >= 10 &&
                <Link to={RoutePath.postRE} style={{ textDecoration: "none" }}>
                <span className="navi-right">
                    {/* <img src={logo} alt="logo" /> */}
                    <span>매물 올리기 \&nbsp;</span>
                </span>
                </Link>
            }
            {Auth.isLoggedIn ?
                <span onClick={() => { socket.sendMessage('/app/logout', ''); window.location.href = "/api/auth/logout"; }} style={{ textDecoration: "none" }}>
                <span className="navi-right">
                    {/* <img src={logo} alt="logo" /> */}
                    <span>로그아웃&nbsp;</span>
                </span>
                </span>
            :
                <span onClick={() => { window.location.href = "/api/auth/google/login" }} style={{ textDecoration: "none" }}>
                <span className="navi-right">
                    {/* <img src={logo} alt="logo" /> */}
                    <span>로그인&nbsp;</span>
                </span>
                </span>
            }
        </span>
        </HeaderWrapper>
    );
}