import { Link } from "react-router-dom";
import { FooterWrapper } from "./Footer.style";
import logo from "../../assets/logo.png";
import { RoutePath } from "../../common/configData";
import { REACT_APP_LOCATION, REACT_APP_PHONE_INFO } from '../../common/configData';
import axios from "axios";
import { toast } from "react-toastify";
import { Button } from "@mui/material";
import { useAuth } from "../../common/states/AuthContext";

export default function Footer({btnShow} : { btnShow : boolean}) {
  const Auth = useAuth();

    const handleDeleteUser = async () => {
        try {
            const res = await axios.delete(
              `/api/user/delete`,
              { withCredentials: true }
            );
            alert("회원탈퇴 완료");
            window.location.href = "/api/auth/logout";
          } catch (err: any) {
            alert("회원탈퇴 실패 : 관리자에게 문의 주세요.");
          }
    }

    return (
        <FooterWrapper>
            <hr/>
            {REACT_APP_LOCATION}
            <br/>
            {REACT_APP_PHONE_INFO}
            {btnShow && Auth.isLoggedIn && <><br/><Button onClick={handleDeleteUser}>회원탈퇴</Button></>}
        </FooterWrapper>
    );
}