import React from "react";

export const AuthenticationContext = React.createContext([]);

export const isAuthenticated = userInfo => {
    return !!userInfo.username;
};

export const hasAccessLevel = (userInfo, accessLevels) => {
    return isAuthenticated(userInfo) && accessLevels.some(accessLevel => userInfo.accessLevels.includes(accessLevel));
};
