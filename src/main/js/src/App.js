import axios from "axios";
import React, { Suspense, useEffect, useState } from "react";
import { Button } from "react-bootstrap";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import { Home } from "./components/Home";
import { AccountDetails } from "./components/mok/AccountDetails";
import { AddAccount } from "./components/mok/AddAccount";
import { Confirm } from "./components/mok/Confirm";
import { EditAccount } from "./components/mok/EditAccount";
import { ListAccounts } from "./components/mok/ListAccounts";
import { Login } from "./components/mok/Login";
import { Register } from "./components/mok/Register";
import { AddCar } from "./components/mop/AddCar";
import { CarDetails } from "./components/mop/CarDetails";
import { EditCar } from "./components/mop/EditCar";
import { ListCars } from "./components/mop/ListCars";
import { AddReservation } from "./components/mor/AddReservation";
import { NavigationBar } from "./components/NavigationBar";
import { NoAccess } from "./components/NoAccess";
import { NotFound } from "./components/NotFound";
import { PrivateRoute } from "./components/PrivateRoute";
import { RestrictedRoute } from "./components/RestrictedRoute";
import { Spinner } from "./components/Spinner";
import { AuthenticationContext, isAuthenticated } from "./utils/AuthenticationContext";
import { ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_EMPLOYEE } from "./utils/Constants";

export const App = () => {

    const [tokenPresent, setTokenPresent] = useState(true);
    const [userInfo, setUserInfo] = useState({
        username: "",
        accessLevels: [],
        currentAccessLevel: "",
        tokenExpiration: 0
    });
    const value = [userInfo, setUserInfo];

    useEffect(() => {
        if (!isAuthenticated(userInfo) && tokenPresent) {
            axios.get("/currentUser", {withCredentials: true})
                .then(response => {
                    setUserInfo(response.data);
                }).catch(() => {
                    setTokenPresent(false);
            });
        }
    }, [tokenPresent, userInfo]);

    Button.defaultProps = {
        variant: "dark",
        className: "button"
    };

    return (
        <AuthenticationContext.Provider value={value}>
            <Router>
                <Suspense fallback={<Spinner/>}>
                    <NavigationBar/>
                    <Switch>
                        <Route exact path="/" component={Home}/>
                        <RestrictedRoute exact path="/login/:session?" component={Login}/>
                        <Route exact path="/register" component={Register}/>
                        <Route exact path="/confirm/:token" component={Confirm}/>
                        <Route exact path="/listAccounts" component={ListAccounts}/>
                        <Route exact path="/accountDetails/:username" component={AccountDetails}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN]} exact path="/addAccount" component={AddAccount}/>
                        <Route exact path="/editAccount/:username" component={EditAccount}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_EMPLOYEE]} exact path="/addCar" component={AddCar}/>
                        <Route exact path="/listCars" component={ListCars}/>
                        <Route exact path="/carDetails/:number" component={CarDetails}/>
                        <Route exact path="/editCar/:number" component={EditCar}/>
                        <Route exact path="/addReservation/:number" component={AddReservation}/>
                        <Route exact path="/noAccess" component={NoAccess}/>
                        <Route component={NotFound}/>
                    </Switch>
                </Suspense>
            </Router>
        </AuthenticationContext.Provider>
    );
};
