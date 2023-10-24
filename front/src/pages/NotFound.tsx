import { Typography } from "@mui/material";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { RoutePath } from "../common/configData";
function NotFound() {
    const navigate = useNavigate();

    useEffect(() => {
        navigate(RoutePath.root);
    }, []);

    return (
        <></>
    );
}

export default NotFound;