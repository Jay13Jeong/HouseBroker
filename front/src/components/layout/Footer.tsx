import { Link } from "react-router-dom";
import { FooterWrapper } from "./Footer.style";
import logo from "../../assets/logo.png";
import { RoutePath } from "../../common/configData";
import { REACT_APP_LOCATION, REACT_APP_PHONE_NUMBER } from '../../common/configData';

export default function Footer() {
    return (
        <FooterWrapper>
            {REACT_APP_LOCATION}
            <br/>
            {REACT_APP_PHONE_NUMBER}
        </FooterWrapper>
    );
}