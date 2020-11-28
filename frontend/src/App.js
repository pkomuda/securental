import React, { Suspense, useState } from "react";
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
import { NavigationBar } from "./components/NavigationBar";
import { NotFound } from "./components/NotFound";
import { Spinner } from "./components/Spinner";
import { AuthenticationContext } from "./utils/AuthenticationContext";

export const App = () => {

    const [user, setUser] = useState({username: "", accessLevels: []});
    const value = {user, setUser};

    Button.defaultProps = {
        variant: "dark",
        className: "button"
    };

    return (
        <AuthenticationContext.Provider value={value}>
            <Router>
                <NavigationBar/>
                <Suspense fallback={<Spinner/>}>
                    <Switch>
                        <Route exact path="/" component={Home}/>
                        <Route exact path="/login" component={Login}/>
                        <Route exact path="/register" component={Register}/>
                        <Route exact path="/confirm/:token" component={Confirm}/>
                        <Route exact path="/listAccounts" component={ListAccounts}/>
                        <Route exact path="/accountDetails/:username" component={AccountDetails}/>
                        <Route exact path="/addAccount" component={AddAccount}/>
                        <Route exact path="/editAccount/:username" component={EditAccount}/>
                        <Route exact path="/addCar" component={AddCar}/>
                        <Route exact path="/listCars" component={ListCars}/>
                        <Route exact path="/carDetails/:number" component={CarDetails}/>
                        <Route exact path="/editCar/:number" component={EditCar}/>
                        <Route component={NotFound}/>
                    </Switch>
                </Suspense>
            </Router>
        </AuthenticationContext.Provider>
    );
};
