import React, { useContext } from "react";
import { Redirect, Route } from "react-router-dom";
import { AuthenticationContext, isAuthenticated } from "../utils/AuthenticationContext";

export const RestrictedRoute = ({component: Component, ...rest}) => {

    const [userInfo] = useContext(AuthenticationContext);

    return (
        <Route {...rest} render={props => (
            !isAuthenticated(userInfo)
                ? <Component {...props}/>
                : <Redirect to="/"/>
            )}
        />
    );
};
