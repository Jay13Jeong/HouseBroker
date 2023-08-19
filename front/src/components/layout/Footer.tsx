import { Link } from "react-router-dom";
import { FooterWrapper } from "./Footer.style";
import logo from "../../assets/logo.png";
import { RoutePath } from "../../common/configData";
import { REACT_APP_LOCATION, REACT_APP_PHONE_INFO } from '../../common/configData';

export default function Footer() {
    return (
        <FooterWrapper>
            <hr/>
            {REACT_APP_LOCATION}
            <br/>
            {REACT_APP_PHONE_INFO}
        </FooterWrapper>
    );
}