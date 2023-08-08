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
import RealestatePostModal from "../lobby/modal/RealestatePostModal";
import RightSide from "./RightSide";

function MainLayout() {
    return (
      <>
        <Header />
        <CustomToastContainer />
        <TableWrapper>
        <TableRow>
          <LeftDiv>
            <LeftSide/>
          </LeftDiv>
              <MainLayoutWrapper>
                <Outlet />
                <ProfileModal />
                <OtherProfileModal />
                <RealestateModal />
                <RealestateEditModal />
                <RealestatePostModal />
                <MyPathModal />
              </MainLayoutWrapper>
          <RightDiv><RightSide/></RightDiv>

        </TableRow>
        <BottomRow>
          <BottomCellLeft>
            아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역<br/>
            아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역<br/>
            </BottomCellLeft>
            <BottomCellMiddle>
            <Footer/>
            </BottomCellMiddle>
            <BottomCellRight>
            아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역<br/>
            아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역/아래 영역<br/>
            </BottomCellRight>
        </BottomRow>
      </TableWrapper>
              
      </>
    );
  }

export default MainLayout;