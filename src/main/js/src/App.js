import axios from "axios";
import React, { Suspense, useEffect, useState } from "react";
import { Button, DropdownButton } from "react-bootstrap";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import { Error } from "./components/common/Error";
import ErrorBoundary from "./components/common/ErrorBoundary";
import { Home } from "./components/common/Home";
import { NavigationBar } from "./components/common/NavigationBar";
import { NoAccess } from "./components/common/NoAccess";
import { NotFound } from "./components/common/NotFound";
import { PrivateRoute } from "./components/common/PrivateRoute";
import { RestrictedRoute } from "./components/common/RestrictedRoute";
import { Spinner } from "./components/common/Spinner";
import { ListLogs } from "./components/mod/ListLogs";
import { AccountDetails } from "./components/mok/AccountDetails";
import { AddAccount } from "./components/mok/AddAccount";
import { ChangeOwnPassword } from "./components/mok/ChangeOwnPassword";
import { ChangePassword } from "./components/mok/ChangePassword";
import { ConfirmAccount } from "./components/mok/ConfirmAccount";
import { EditAccount } from "./components/mok/EditAccount";
import { EditOwnAccount } from "./components/mok/EditOwnAccount";
import { ListAccounts } from "./components/mok/ListAccounts";
import { Login } from "./components/mok/Login";
import { OwnAccountDetails } from "./components/mok/OwnAccountDetails";
import { Register } from "./components/mok/Register";
import { ResetPassword } from "./components/mok/ResetPassword";
import { AddReservation } from "./components/mor/AddReservation";
import { EditOwnReservation } from "./components/mor/EditOwnReservation";
import { FinishReservation } from "./components/mor/FinishReservation";
import { ListOwnReservations } from "./components/mor/ListOwnReservations";
import { ListReservations } from "./components/mor/ListReservations";
import { OwnReservationDetails } from "./components/mor/OwnReservationDetails";
import { ReceiveOwnReservation } from "./components/mor/ReceiveOwnReservation";
import { ReservationDetails } from "./components/mor/ReservationDetails";
import { AddCar } from "./components/mos/AddCar";
import { CarDetails } from "./components/mos/CarDetails";
import { EditCar } from "./components/mos/EditCar";
import { ListCars } from "./components/mos/ListCars";
import { AuthenticationContext, isAuthenticated } from "./utils/AuthenticationContext";
import { ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_CLIENT, ACCESS_LEVEL_EMPLOYEE } from "./utils/Constants";
import i18n from "./utils/i18n";

export const App = () => {

    const [userInfo, setUserInfo] = useState({
        username: "",
        accessLevels: [],
        currentAccessLevel: "",
        preferredLanguage: "",
        tokenPresent: true,
        tokenExpiration: 0
    });
    const value = [userInfo, setUserInfo];

    useEffect(() => {
        if (!isAuthenticated(userInfo) && userInfo.tokenPresent) {
            axios.get("/currentUser")
                .then(response => {
                    const tempUserInfo = response.data;
                    tempUserInfo.tokenPresent = true;
                    setUserInfo(tempUserInfo);
                    i18n.changeLanguage(tempUserInfo.preferredLanguage).then(() => {});
                }).catch(() => {
                    const tempUserInfo = {...userInfo};
                    tempUserInfo.tokenPresent = false;
                    setUserInfo(tempUserInfo);
                    i18n.changeLanguage(tempUserInfo.preferredLanguage).then(() => {});
            });
        }
    }, [userInfo]);

    Button.defaultProps = {
        variant: "dark",
        className: "button"
    };
    DropdownButton.defaultProps = {
        variant: "dark",
        className: "button"
    };

    return (
        <ErrorBoundary>
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
                            <RestrictedRoute exact path="/confirmAccount/:token" component={ConfirmAccount}/>
                            <RestrictedRoute exact path="/resetPassword/:hash" component={ResetPassword}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_EMPLOYEE, ACCESS_LEVEL_CLIENT]} exact path="/ownAccountDetails" component={OwnAccountDetails}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_EMPLOYEE, ACCESS_LEVEL_CLIENT]} exact path="/editOwnAccount" component={EditOwnAccount}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_EMPLOYEE, ACCESS_LEVEL_CLIENT]} exact path="/changeOwnPassword" component={ChangeOwnPassword}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN]} exact path="/addAccount" component={AddAccount}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN]} exact path="/listAccounts" component={ListAccounts}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN]} exact path="/accountDetails/:username" component={AccountDetails}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN]} exact path="/editAccount/:username" component={EditAccount}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN]} exact path="/changePassword/:username" component={ChangePassword}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_ADMIN]} exact path="/listLogs" component={ListLogs}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_EMPLOYEE]} exact path="/addCar" component={AddCar}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_EMPLOYEE]} exact path="/editCar/:number" component={EditCar}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_EMPLOYEE]} exact path="/listReservations" component={ListReservations}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_EMPLOYEE]} exact path="/reservationDetails/:number" component={ReservationDetails}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_EMPLOYEE]} exact path="/finishReservation/:number" component={FinishReservation}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_CLIENT]} exact path="/addReservation/:number" component={AddReservation}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_CLIENT]} exact path="/listOwnReservations" component={ListOwnReservations}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_CLIENT]} exact path="/ownReservationDetails/:number" component={OwnReservationDetails}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_CLIENT]} exact path="/receiveOwnReservation/:number" component={ReceiveOwnReservation}/>
                            <PrivateRoute accessLevels={[ACCESS_LEVEL_CLIENT]} exact path="/editOwnReservation/:number" component={EditOwnReservation}/>
                            <Route exact path="/noAccess" component={NoAccess}/>
                            <Route exact path="/error" component={Error}/>
                            <Route component={NotFound}/>
                        </Switch>
                    </Suspense>
                </Router>
            </AuthenticationContext.Provider>
        </ErrorBoundary>
    );
};
