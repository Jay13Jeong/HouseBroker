import { Link, useNavigate } from "react-router-dom";
import { HeaderWrapper } from "./Header.style";
import logo from "../../assets/logo.png";
import { RoutePath } from "../../common/configData";
import { REACT_APP_NAME } from '../../common/configData';
import { useAuth } from "../../common/states/AuthContext";
import { useSocket } from "../../common/states/socketContext";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

export default function Header() {
    const Auth = useAuth();
    const socket = useSocket();
    const navigate = useNavigate();
    const [loginBtnStatus, setLoginBtnStatus] = useState('로그인');
    const [newWindow, setNewWindow] = useState<Window | null>(null);

    useEffect(() => {
        if (Auth.user) {
            if (Auth.user.dormant === true) navigate(RoutePath.dormant);
        }
    }, [Auth]);

    useEffect(() => {
        if (newWindow === null) return;
        function checkWindowStatus() {
            if (newWindow && newWindow.closed) {
              clearInterval(windowCheckInterval); // 새 창이 닫히면 감지 중단
              setNewWindow(null);
              Auth.initUserInfo();
            }
        }
        const windowCheckInterval = setInterval(checkWindowStatus, 1000);
    }, [newWindow]);

    function openLoginWindow() {
        // 로그인 페이지 URL을 여기에 설정
        const loginUrl = "/api/auth/google/login"; // 구글 로그인 페이지 또는 다른 인증 페이지 URL로 변경
        // 새 창 열기
        const newWindow_ = window.open(loginUrl, "_blank", "width=500,height=600")
        setNewWindow(newWindow_);
        // 새 창이 차단되었는지 확인
        if (newWindow_) {
            setLoginBtnStatus('로그인 중...')
        } else {
          // 팝업 차단 등의 이유로 새 창 열기에 실패한 경우
        //   setLoginBtnStatus("팝업 차단됨");
            toast.error('팝업 차단을 해제해주세요')
        }
    }

    return (
        <HeaderWrapper>
        <Link to={RoutePath.root} style={{ textDecoration: "none" }}>
            <span className="navi-left">
            <img src={logo} alt="logo" />
            <span>메인으로</span>
            </span>
        </Link>
        <Link to={RoutePath.root} style={{ textDecoration: "none" }}>
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
                <span onClick={() => { 
                    // socket.sendMessage('/app/logout', ''); 
                    window.location.href = "/api/auth/logout"; 
                    }} style={{ textDecoration: "none" }}>
                <span className="navi-right">
                    {/* <img src={logo} alt="logo" /> */}
                    <span>로그아웃&nbsp;</span>
                </span>
                </span>
            :
                <span onClick={openLoginWindow} style={{ textDecoration: "none" }}>
                <span className="navi-right">
                    {/* <img src={logo} alt="logo" /> */}
                    <span>{loginBtnStatus}&nbsp;</span>
                </span>
                </span>
            }
        </span>
        </HeaderWrapper>
    );
}