import React from "react";

export const AuthenticationContext = React.createContext({
    userInfo: {
        username: "",
        accessLevels: [],
        // currentAccessLevel: "",
        tokenExpiration: 0
    },
    setUserInfo: () => {}
});

export const isAuthenticated = userInfo => {
    return userInfo.username;
};
