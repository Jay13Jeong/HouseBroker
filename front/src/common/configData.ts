export const { REACT_APP_HOST } = process.env;
export const { REACT_APP_NAME } = process.env;
export const RoutePath = {
    root: "/",
    postRE: "/realestate/post",
    patchRE: "/realestate/patch",
    delRE: "/realestate/delete",
    loginTest: "/api/auth/google/login",

    lobby: "/lobby",
    fa2: "/auth/fa2",
    profile: "/profile/init",
    chat: "/chat",
    game: "/game",
    gameMatch: "/game/match",
    gameWatch: "/game/watch",
    dm: "/dm"
}
export const colors = {
    backgroudColor: "#2B3467",
    p1Color: "#BAD7E9",
    p2Color: "#FFC6D3",
    ballColor: "#EB455F",
    gameColor: "#FCFFE7"
};
export const sizes = {
    canvasWidth: 800,
    canvasHeight: 500,
    lineWidth: 12,
    paddleSize: 100
};