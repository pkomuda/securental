import React, { Suspense } from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import { Container } from "react-bootstrap";
import { NavigationBar } from "./components/NavigationBar";
import { Spinner } from "./components/Spinner";
import { Home } from "./components/Home";
import { Login } from "./components/Login";
import { Register } from "./components/Register";
import { Confirm } from "./components/Confirm";
import { NotFound } from "./components/NotFound";
import "./components/styles/Common.css";
import "@sweetalert2/theme-bootstrap-4";

export const App = () => (
    <Router>
        <NavigationBar/>
        <Container>
            <Suspense fallback={<Spinner/>}>
                <Switch>
                    <Route exact path="/" component={Home}/>
                    <Route exact path="/login" component={Login}/>
                    <Route exact path="/register" component={Register}/>
                    <Route exact path="/confirm/:token" component={Confirm}/>
                    <Route component={NotFound}/>
                </Switch>
            </Suspense>
        </Container>
    </Router>
);
