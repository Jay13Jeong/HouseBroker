import React, { createContext, useContext, useEffect, ReactNode } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { useSetRecoilState } from "recoil";
import { socketConnectState } from "../../common/states/recoilModalState";

interface MySocketIterface {
  sendMessage: (destination: string, body: string) => void;
  addSubscribe: (destination: string, callback: (message: Stomp.Message) => void) => void;
  unsubscribe: (subscription: string) => void;
  stomp: Stomp.Client
}

const SocketContext = createContext<MySocketIterface>({} as MySocketIterface);

export const useSocket = () => {
  const { 
    sendMessage, 
    addSubscribe, 
    unsubscribe, 
    // socketState, 
    stomp 
  } = useContext(SocketContext);

  return {
    sendMessage,
    addSubscribe,
    unsubscribe,
    // socketState,
    stomp,
  };
};
interface SocketProviderProps {
  children: ReactNode; // React 컴포넌트의 children의 타입을 ReactNode로 지정합니다.
}

export const SocketProvider = ({ children }: SocketProviderProps) => {
  const Urll = '/api/ws';
  const socketjs = new SockJS(Urll);
  const stomp = Stomp.over(socketjs);
  const setSocketState = useSetRecoilState(socketConnectState);
  const subscriptionMap = new Map<string, Stomp.Subscription>();
  stomp.debug = () => {}; // debug log off.

  useEffect(() => {
    stomp.connect({}, () => {
      setSocketState({connected : true});
    });

    return () => {
      stomp.disconnect(() => {
        setSocketState({connected : false});
      });
    };
  }, []);

  const sendMessage = (destination: string, body: string) => {
    if (stomp && stomp.connected) {
      stomp.send(destination, {}, body);
    }
  };

  const addSubscribe = (destination: string, callback: (message: Stomp.Message) => void) => {
    if (subscriptionMap.get(destination) !== undefined){
      return;
    }
    if (stomp && stomp.connected) {
      const subscription = stomp.subscribe(destination, callback);
      subscriptionMap.set(destination, subscription);
    }
  };

  const unsubscribe = (destination: string) => {
    const subscription = subscriptionMap.get(destination);
    if (subscription) {
      subscription.unsubscribe();
      subscriptionMap.delete(destination);
    }
  };

  const value = { 
    sendMessage, 
    addSubscribe, 
    unsubscribe, 
    // socketState, 
    stomp, 
  };

  return (
    <SocketContext.Provider value={value}>
      {children}
    </SocketContext.Provider>
  );
};