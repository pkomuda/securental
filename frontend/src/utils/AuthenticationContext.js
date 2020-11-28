import React from "react";

export const AuthenticationContext = React.createContext({
    user: {
        username: "",
        accessLevels: []
    },
    setUser: () => {}
});

export const isAuthenticated = user => {
    return user.username && user.accessLevels;
};
