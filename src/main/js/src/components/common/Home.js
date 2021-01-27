import React from "react";
import { Container } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { Jumbotron } from "./Jumbotron";

export const Home = () => {

    const {t} = useTranslation();

    return (
        <React.Fragment>
            <Jumbotron/>
            <Container>
                <h1>{t("home.header")}</h1>
                <p>{t("home.paragraph1")}</p>
            </Container>
        </React.Fragment>
    );
};
