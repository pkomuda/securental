import axios from "axios";
import React, { useState } from "react";
import { Button, ButtonToolbar, Col, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { Captcha } from "../common/Captcha";

export const ConfirmAccount = props => {

    const {t} = useTranslation();
    const [captcha, setCaptcha] = useState("");

    const handleChangeCaptcha = value => {
        let temp = value;
        setCaptcha(temp);
        validateCaptcha(temp);
    };

    const validateCaptcha = value => {
        if (value) {
            document.getElementById("captchaFeedback").style.display = "none";
            return true;
        } else {
            document.getElementById("captchaFeedback").style.display = "block";
            return false;
        }
    };

    const handleSubmit = () => {
        if (validateCaptcha(captcha)) {
            axios.put("/confirmAccount",
                {token: window.location.pathname.substring(window.location.pathname.lastIndexOf("/") + 1)},
                {headers: {"Captcha-Response": captcha}})
                .then(() => {
                    handleSuccess("confirm.success", "");
                    props.history.push("/");
                }).catch(error => {
                    handleError(error);
            });
        }
    };

    return (
        <Row className="justify-content-center">
            <Col sm={6} className="form-container">
                <h1 className="text-center">{t("confirm.header")}</h1>
                <ButtonToolbar className="justify-content-center">
                    <Button id="back"
                            onClick={() => props.history.push("/")}>{t("navigation.back")}</Button>
                    <Button id="ok"
                            onClick={handleSubmit}>{t("navigation.ok")}</Button>
                </ButtonToolbar>
                <Captcha onChange={handleChangeCaptcha}/>
            </Col>
        </Row>
    );
};
