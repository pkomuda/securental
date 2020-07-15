import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import { Container } from "react-bootstrap";
import Home from "./components/Home";
import NotFound from "./components/NotFound";

export default class App extends Component {

    render() {
        return (
            <React.Fragment>
                <Router>
                    <Container>
                        <Switch>
                            <Route exact path="/" component={Home}/>
                            <Route component={NotFound}/>
                        </Switch>
                    </Container>
                </Router>
            </React.Fragment>
        );
    }
}
