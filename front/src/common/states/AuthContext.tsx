import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import * as types from "../../common/types/User"
import axios from 'axios';
import { toast } from 'react-toastify';

// 로그인 정보의 타입 정의
interface AuthContextType {
  isLoggedIn: boolean;
  user: types.User | null;
  permitLevel: number;
  initUserInfo: () => void;
  // login: () => void;
  // logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('AuthContext 컨텍스트 값 가져오기 실패.');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
  const [user, setUser] = useState<types.User | null>(null);
  const [permitLevel, setPermitLevel] = useState<number>(0);

  const login = () => {

  };

  const logout = () => {
  
  };

  useEffect(() => {
    initUserInfo();
  }, []);

  const initUserInfo = async () => {
    try{
        const res = await axios.get('/api/user/', {withCredentials: true});
        const permitRes = await axios.get<string>('/api/user/permit', {withCredentials: true});
        setUser(res.data);
        setPermitLevel(Number(permitRes.data));
        setIsLoggedIn(true);
    }catch{
        setUser(null);
        setPermitLevel(0);
        setIsLoggedIn(false);
    }
  }

  const authContextValue: AuthContextType = {
    isLoggedIn,
    user,
    permitLevel,
    initUserInfo,
    // login,
    // logout,
  };

  return (
    <AuthContext.Provider value={authContextValue}>
      {children}
    </AuthContext.Provider>
  );
};