import React from "react";
import { Container } from "react-bootstrap";
import { Jumbotron } from "./Jumbotron";

export const Home = () => {

    return (
        <React.Fragment>
            <Jumbotron/>
            <Container>
                <h1>Secure car rental</h1>
            </Container>
        </React.Fragment>
    );
};
