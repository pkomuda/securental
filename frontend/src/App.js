import React, { Suspense } from "react";
import { Button } from "react-bootstrap";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import { AddCar } from "./components/AddCar";
import { CarDetails } from "./components/CarDetails";
import { ListCars } from "./components/ListCars";
import { NavigationBar } from "./components/NavigationBar";
import { Spinner } from "./components/Spinner";
import { Home } from "./components/Home";
import { Login } from "./components/Login";
import { Register } from "./components/Register";
import { Confirm } from "./components/Confirm";
import { NotFound } from "./components/NotFound";
import "./styles/Common.css";
import "@sweetalert2/theme-bootstrap-4";
import { ListAccounts } from "./components/ListAccounts";
import { AccountDetails } from "./components/AccountDetails";
import { AddAccount } from "./components/AddAccount";
import { EditAccount } from "./components/EditAccount";

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
                    <Route component={NotFound}/>
                </Switch>
            </Suspense>
        </Router>
    );
};
