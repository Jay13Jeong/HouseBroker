import { useRecoilValue, useResetRecoilState, useSetRecoilState } from 'recoil';
import { LoginButton } from '../../components/auth/Login';
import { dormantModalState } from '../../common/states/recoilModalState';
import { useEffect } from 'react';
import { DefaultButton } from '../../components/common';
import axios from 'axios';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import { RoutePath } from '../../common/configData';
import { useAuth } from '../../common/states/AuthContext';
import Footer from '../../components/layout/Footer';

export default function DormantPage() {

  const showModal = useRecoilValue(dormantModalState);
  const resetState = useResetRecoilState(dormantModalState);
  const setModal = useSetRecoilState(dormantModalState);
  const navigate = useNavigate();
  const Auth = useAuth();

  useEffect(() => {
   if (Auth.user) {
        if (Auth.user.dormant === false) navigate(RoutePath.root);
    }
}, [Auth] );

  // useEffect(() => {
  //   // setModal({show : true});
  // }, []);
  
  const modifyDormant = async () => {
    try {
      const response = await axios.patch(
        `/api/user/dormant`,{},{ withCredentials: true, }
      );
      alert("회원복귀 성공")
      window.location.reload();
    } catch (err: any) {
      toast.error("회원복귀 실패");
      toast.error("관리자에게 문의하세요.");
    }
  };

  return (
    <>
    <center>
    <h1>탈퇴한 회원입니다</h1><br/>
    <DefaultButton onClick={modifyDormant}>다시 회원되기</DefaultButton>
    <DefaultButton onClick={() => (window.location.href = "/api/auth/logout")}>돌아가기</DefaultButton>
    </center>
    <Footer btnShow={false}/>
    </>
  );
}
