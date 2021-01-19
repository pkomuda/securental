import axios from "axios";
import React, { Suspense, useEffect, useState } from "react";
import { Button } from "react-bootstrap";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import { Home } from "./components/Home";
import { AccountDetails } from "./components/mok/AccountDetails";
import { AddAccount } from "./components/mok/AddAccount";
import { Confirm } from "./components/mok/Confirm";
import { EditAccount } from "./components/mok/EditAccount";
import { EditOwnAccount } from "./components/mok/EditOwnAccount";
import { ListAccounts } from "./components/mok/ListAccounts";
import { Login } from "./components/mok/Login";
import { OwnAccountDetails } from "./components/mok/OwnAccountDetails";
import { Register } from "./components/mok/Register";
import { AddCar } from "./components/mop/AddCar";
import { CarDetails } from "./components/mop/CarDetails";
import { EditCar } from "./components/mop/EditCar";
import { ListCars } from "./components/mop/ListCars";
import { AddReservation } from "./components/mor/AddReservation";
import { EditOwnReservation } from "./components/mor/EditOwnReservation";
import { ListOwnReservations } from "./components/mor/ListOwnReservations";
import { ListReservations } from "./components/mor/ListReservations";
import { OwnReservationDetails } from "./components/mor/OwnReservationDetails";
import { ReservationDetails } from "./components/mor/ReservationDetails";
import { NavigationBar } from "./components/NavigationBar";
import { NoAccess } from "./components/NoAccess";
import { NotFound } from "./components/NotFound";
import { PrivateRoute } from "./components/PrivateRoute";
import { RestrictedRoute } from "./components/RestrictedRoute";
import { Spinner } from "./components/Spinner";
import { AuthenticationContext, isAuthenticated } from "./utils/AuthenticationContext";
import { ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_CLIENT, ACCESS_LEVEL_EMPLOYEE } from "./utils/Constants";

export const App = () => {

    const [userInfo, setUserInfo] = useState({
        username: "",
        accessLevels: [],
        currentAccessLevel: "",
        tokenPresent: true,
        tokenExpiration: 0
    });
    const value = [userInfo, setUserInfo];

    useEffect(() => {
        if (!isAuthenticated(userInfo) && userInfo.tokenPresent) {
            axios.get("/currentUser", {withCredentials: true})
                .then(response => {
                    const tempUserInfo = response.data;
                    tempUserInfo.tokenPresent = true;
                    setUserInfo(tempUserInfo);
                }).catch(() => {
                    const tempUserInfo = {...userInfo};
                    tempUserInfo.tokenPresent = false;
                    setUserInfo(tempUserInfo);
            });
        }
    }, [userInfo]);

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
                        <Route exact path="/listCars" component={ListCars}/>
                        <Route exact path="/carDetails/:number" component={CarDetails}/>
                        <RestrictedRoute exact path="/login/:session?" component={Login}/>
                        <RestrictedRoute exact path="/register" component={Register}/>
                        <RestrictedRoute exact path="/confirm/:token" component={Confirm}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_EMPLOYEE, ACCESS_LEVEL_CLIENT]} exact path="/ownAccountDetails" component={OwnAccountDetails}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_EMPLOYEE, ACCESS_LEVEL_CLIENT]} exact path="/editOwnAccount" component={EditOwnAccount}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN]} exact path="/addAccount" component={AddAccount}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN]} exact path="/listAccounts" component={ListAccounts}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN]} exact path="/accountDetails/:username" component={AccountDetails}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN]} exact path="/editAccount/:username" component={EditAccount}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_EMPLOYEE]} exact path="/addCar" component={AddCar}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_EMPLOYEE]} exact path="/editCar/:number" component={EditCar}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_EMPLOYEE]} exact path="/listReservations" component={ListReservations}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_EMPLOYEE]} exact path="/reservationDetails/:number" component={ReservationDetails}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_CLIENT]} exact path="/addReservation/:number" component={AddReservation}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_CLIENT]} exact path="/listOwnReservations" component={ListOwnReservations}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_CLIENT]} exact path="/ownReservationDetails/:number" component={OwnReservationDetails}/>
                        <PrivateRoute accessLevels={[ACCESS_LEVEL_CLIENT]} exact path="/editOwnReservation/:number" component={EditOwnReservation}/>
                        <Route exact path="/noAccess" component={NoAccess}/>
                        <Route component={NotFound}/>
                    </Switch>
                </Suspense>
            </Router>
        </AuthenticationContext.Provider>
    );
};
