export const { REACT_APP_HOST } = process.env;
export const { REACT_APP_NAME } = process.env;
export const { REACT_APP_LOCATION } = process.env;
export const { REACT_APP_PHONE_INFO } = process.env;
export const { REACT_APP_PHONE_NUMBER } = process.env;
export const { REACT_APP_KAKAO_MAP_KEY } = process.env;
export const { REACT_APP_MY_LOCATE_X } = process.env;
export const { REACT_APP_MY_LOCATE_Y } = process.env;
export const RoutePath = {
    root: "/",
    map: "/map",
    postRE: "/realestate/post",
    patchRE: "/realestate/patch",
    delRE: "/realestate/delete",
    loginTest: "/api/auth/google/login",
    dormant: "/returning",
    callback: "/auth/callback",
    login: "/auth/login",
    signup: "/auth/signup",

    // fa2: "/auth/fa2",
}
export const colors = {
    backgroudColor: "#2B3467",
};

export const sizes = {
    canvasWidth: 800,
    canvasHeight: 500,
    lineWidth: 12,
    paddleSize: 100
};