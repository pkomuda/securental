import React from "react";

export const AuthenticationContext = React.createContext([]);

export const isAuthenticated = userInfo => {
    return !!userInfo.username;
};
