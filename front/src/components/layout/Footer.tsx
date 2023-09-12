import { Link } from "react-router-dom";
import { FooterWrapper } from "./Footer.style";
import logo from "../../assets/logo.png";
import { RoutePath } from "../../common/configData";
import { REACT_APP_LOCATION, REACT_APP_PHONE_INFO } from '../../common/configData';
import axios from "axios";
import { toast } from "react-toastify";
import { Button } from "@mui/material";

export default function Footer() {
    const handleDeleteUser = async () => {
        try {
            const res = await axios.delete(
              `/api/user/delete`,
              { withCredentials: true }
            );
            window.location.reload()
            toast.success("회원탈퇴 완료");
          } catch (err: any) {
            window.location.reload()
            toast.error("회원탈퇴 실패");
          }
    }

    return (
        <FooterWrapper>
            <hr/>
            {REACT_APP_LOCATION}
            <br/>
            {REACT_APP_PHONE_INFO}
            <br/><Button onClick={handleDeleteUser}>회원탈퇴</Button>
        </FooterWrapper>
    );
}