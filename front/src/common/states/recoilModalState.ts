import { atom } from "recoil"
import { Chat, ChatRoom } from "../types/User"

export const dmModalState = atom<boolean>({
    key: "dmModalState",
    default: false
})

export const profileModalState = atom<{ userId: number, show: boolean }>({
    key: "profileModalState",
    default: {
        userId: 0,
        show: false
    }
})

export const otherProfileModalState = atom<{ userId: number, show: boolean }>({
    key: "otherProfileModalState",
    default: {
        userId: 0,
        show: false
    }
})

export const profileEditModalState = atom<boolean>({
    key: "profileEditModalState",
    default: false
})

export const friendModalState = atom<boolean>({
    key: "friendModalState",
    default: false
})

export const pendingModalState = atom<boolean>({
    key: "pendingModalState",
    default: false
})

export const blockModalState = atom<boolean>({
    key: "blockModalState",
    default: false
})

export const createChatModalState = atom<boolean>({
    key: "createChatModalState",
    default: false
})

export const secretChatModalState = atom<{roomName: string, show: boolean}>({
    key: "secretChatModalState",
    default: {
        roomName: "",
        show: false
    }
})

export const loginState = atom<boolean>({
    key: "loginState",
    default: false
})

export const changeChatPwModalState = atom<{roomName: string, show: boolean}>({
    key: "changeChatPwModalState",
    default: {
        roomName: "",
        show: false
    }
})

export const chatMenuModalState = atom<{user: string, show: boolean}>({
    key: "chatMenuModalState",
    default: {user: "", show: false}
})

export const gameInviteModalState = atom<boolean>({
    key: "gameInviteModalState",
    default: false
})

/////////////////////// /////////////// ////////////

export const realestateModalState = atom<{ realestateId: number, show: boolean }>({
    key: "realestateModalState",
    default: {
        realestateId: 0,
        show: false
    }
})

export const realestateEditModalState = atom<{ realestateId: number, show: boolean }>({
    key: "realestateEditModalState",
    default: {
        realestateId: 0,
        show: false
    }
})

export const realestatePostModalState = atom<{ realestateId: number, show: boolean }>({
    key: "realestatePostModalState",
    default: {
        realestateId: 0,
        show: false
    }
})

export const mainUpdateChecker = atom<{ updateCount: number }>({
    key: "mainUpdateChecker",
    default: {
        updateCount: 0,
    }
})

export const myPathModalState = atom<{ show: boolean }>({
    key: "myPathModalState",
    default: {
        show: false,
    }
})

export const realestateFilterState = atom<{ filter: string }>({
    key: "realestateFilterState",
    default: {
        filter: "default",
    }
})

export const socketConnectState = atom<{ connected: boolean }>({
    key: "socketConnectState",
    default: {
        connected: false,
    }
})

export const selectedImgCardIndexState = atom<{ index: number }>({
    key: "selectedImgCardIndexState",
    default: {
        index: -1,
    }
})

export const bigImgModalState = atom<{ show: boolean, imgUrl: string }>({
    key: "bigImgModalState",
    default: {
        show: false,
        imgUrl: require('../../assets/sampleroom.png'),
    }
})

export const dormantModalState = atom<{ show: boolean, }>({
    key: "dormantModalState",
    default: {
        show: false,
    }
})

export const socketIdState = atom<{ socketId: string }>({
    key: "socketIdState",
    default: {
        socketId: "",
    }
})

export const chatRoomState = atom<{ chatRooms : ChatRoom[] }>({
    key: "chatRoomState",
    default: {
        chatRooms: [],
    }
})

export const messageState = atom<{ chat : { [key: number]: Chat[] } }>({
    key: "messageState",
    default: {
        chat : {
            0 : []
        },
    }
})