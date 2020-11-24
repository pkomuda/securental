import React, { Suspense } from "react";
import { Button } from "react-bootstrap";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import { AddCar } from "./components/mop/AddCar";
import { CarDetails } from "./components/mop/CarDetails";
import { EditCar } from "./components/mop/EditCar";
import { ListCars } from "./components/mop/ListCars";
import { NavigationBar } from "./components/NavigationBar";
import { Spinner } from "./components/Spinner";
import { Home } from "./components/Home";
import { Login } from "./components/mok/Login";
import { Register } from "./components/mok/Register";
import { Confirm } from "./components/mok/Confirm";
import { NotFound } from "./components/NotFound";
import "./styles/Common.css";
import "@sweetalert2/theme-bootstrap-4";
import { ListAccounts } from "./components/mok/ListAccounts";
import { AccountDetails } from "./components/mok/AccountDetails";
import { AddAccount } from "./components/mok/AddAccount";
import { EditAccount } from "./components/mok/EditAccount";

export const App = () => {

    Button.defaultProps = {
        variant: "dark"
    };

    return (
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
    );
};
