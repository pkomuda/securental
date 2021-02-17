import React from "react";
import { Jumbotron as Jumbo } from "react-bootstrap";
import { useTranslation } from "react-i18next";

export const Jumbotron = () => {

    const {t} = useTranslation();

    return (
        <Jumbo fluid className="jumbo">
            <div className="overlay"/>
            <h1 style={{textAlign: "center", marginTop: "1em"}}>{t("home.header")}</h1>
        </Jumbo>
    );
};
