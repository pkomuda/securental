import axios from "axios";
import React from "react";
import { Button, ButtonToolbar } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import Swal from "sweetalert2";

export const Confirm = props => {

    const {t} = useTranslation();

    const handleSubmit = () => {
        axios.put("/confirm", {token: window.location.pathname.substring(window.location.pathname.lastIndexOf("/") + 1)})
            .then(() => {
                Swal.fire(t("confirm.success"), "", "success");
                props.history.push("/");
            }).catch(() => {
                Swal.fire(t("errors:common.header"),
                    t("errors:common.text"),
                    "error");
        })
    };

    return (
        <React.Fragment>
            <h1 className="text-center">{t("confirm.header")}</h1>
            <ButtonToolbar className="justify-content-center">
                <Button id="back"
                        onClick={() => props.history.push("/")}>{t("navigation.back")}</Button>
                <Button id="submit"
                        onClick={handleSubmit}>{t("navigation.submit")}</Button>
            </ButtonToolbar>
        </React.Fragment>
    );
};