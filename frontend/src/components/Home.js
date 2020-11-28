import React from "react";
import { Button } from "react-bootstrap";
import Swal from "sweetalert2";
import { error } from "../utils/Alerts";
import i18n from "../utils/i18n"
import { Jumbotron } from "./Jumbotron";

export const Home = () => {

    const handleClick = () => {
        Swal.fire(i18n.t("errors:common.header"),
            i18n.t(`errors:common.text`),
            "error")
    };

    return (
        <React.Fragment>
            <Jumbotron/>
            <Button onClick={handleClick}>swal</Button>
            <Button onClick={() => error("common.header", "common.text")}>utils</Button>
        </React.Fragment>
    );
};
