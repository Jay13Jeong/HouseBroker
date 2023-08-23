import { Outlet } from "react-router-dom";
import Header from "./Header";
import {
    TableWrapper,
    TableRow,
    LeftDiv,
    MainLayoutWrapper,
    RightDiv,
    BottomRow,
    BottomCellLeft,
    BottomCellRight,
    BottomCellMiddle,
  } from "./MainLayout.style";
import Footer from "./Footer";
import LeftSide from "./LeftSide";
import CustomToastContainer from "../util/CustomToastContainer";
import { MyPathModal, ProfileModal } from "../lobby/modal";
import OtherProfileModal from "../lobby/modal/OtherProfileModal";

import RealestateModal from "../lobby/modal/RealestateModal";
import RealestateEditModal from "../lobby/modal/RealestateEditModal";
import RightSide from "./RightSide";

function MainLayout() {
    return (
      <>
        <Header />
        <CustomToastContainer />
        <TableWrapper>
        <TableRow>
          <LeftDiv><LeftSide/></LeftDiv>
              <MainLayoutWrapper>
                <Outlet />
                <ProfileModal />
                <OtherProfileModal />
                <RealestateModal />
                <RealestateEditModal />
                <MyPathModal />
              </MainLayoutWrapper>
          <RightDiv><RightSide/></RightDiv>
        </TableRow>
        <BottomRow>
          <BottomCellLeft></BottomCellLeft>
          <BottomCellMiddle>
            <Footer/>
          </BottomCellMiddle>
          <BottomCellRight></BottomCellRight>
        </BottomRow>
      </TableWrapper>
              
      </>
    );
  }

export default MainLayout;