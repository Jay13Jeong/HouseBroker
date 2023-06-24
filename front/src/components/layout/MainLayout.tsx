import { Outlet } from "react-router-dom";
import Header from "./Header";
import MainLayoutWrapper from "./MainLayout.style";
import CustomToastContainer from "../util/CustomToastContainer";
import { ProfileModal } from "../lobby/modal";
import OtherProfileModal from "../lobby/modal/OtherProfileModal";

import RealestateModal from "../lobby/modal/RealestateModal";
import RealestateEditModal from "../lobby/modal/RealestateEditModal";
import RealestatePostModal from "../lobby/modal/RealestatePostModal";

function MainLayout() {
    return (
        <>
        <Header/>
        <CustomToastContainer/>
        <MainLayoutWrapper>
            <Outlet/>
            <ProfileModal/>
            <OtherProfileModal/>
            <RealestateModal/>
            <RealestateEditModal/>
            <RealestatePostModal/>
        </MainLayoutWrapper>
        <footer></footer>
        </>
    );
}

export default MainLayout;