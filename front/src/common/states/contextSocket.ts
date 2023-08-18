import {createContext} from "react";
import io, { Socket } from "socket.io-client";

const ENDPOINT = 'http://localhost/api/socket.io';
export const socket = io(ENDPOINT, {
    transports: ['websocket'],
    withCredentials: true,
});
export const SocketContext = createContext<Socket>({} as Socket);