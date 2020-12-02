import React, { useContext } from "react";
import { Redirect, Route } from "react-router-dom";
import { AuthenticationContext, hasAccessLevel } from "../utils/AuthenticationContext";

export const PrivateRoute = ({component: Component, accessLevels, ...rest}) => {

    const [userInfo] = useContext(AuthenticationContext);

    return (
        <Route {...rest} render={props => (
            hasAccessLevel(userInfo, accessLevels)
                ? <Component {...props}/>
                : <Redirect to="/noAccess"/>
            )}
        />
    );
};
