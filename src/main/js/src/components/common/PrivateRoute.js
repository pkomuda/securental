import React, { useContext } from "react";
import { Redirect, Route } from "react-router-dom";
import { AuthenticationContext, hasAccessLevel, isAuthenticated } from "../../utils/AuthenticationContext";
import { Spinner } from "./Spinner";

export const PrivateRoute = ({component: Component, accessLevels, ...rest}) => {

    const [userInfo] = useContext(AuthenticationContext);

    if (!isAuthenticated(userInfo) && userInfo.tokenPresent) {
        return <Spinner/>;
    } else {
        return (
            <Route {...rest} render={props => (
                hasAccessLevel(userInfo, accessLevels)
                    ? <Component {...props}/>
                    : <Redirect to="/noAccess"/>
            )}
            />
        );
    }
};
