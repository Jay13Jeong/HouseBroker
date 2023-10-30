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

export default function CallbackPage() {

  const showModal = useRecoilValue(dormantModalState);
  const resetState = useResetRecoilState(dormantModalState);
  const setModal = useSetRecoilState(dormantModalState);
  const navigate = useNavigate();
  const Auth = useAuth();

  // useEffect(() => {
  //   // if (Auth.user) {
  //   //     if (Auth.user.dormant === false) navigate(RoutePath.root);
  //   // }
  // }, [Auth]);

  useEffect(() => {
    closeWindow();
  }, []);

  const closeWindow = () => {
    window.close();
  }

  return (
    <center>
    인가 중...
    </center>
  );
}
