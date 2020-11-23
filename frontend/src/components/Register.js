import axios from "axios";
import React, { useState } from "react";
import { Button, ButtonToolbar, Col, Form, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { object, string } from "yup";
import { LAST_PASSWORD_CHARACTERS } from "../utils/Constants";
import { EMAIL_REGEX, validate } from "../utils/Validation";
import { EditFormGroup } from "./EditFormGroup";

export const Register = props => {

    const {t} = useTranslation();
    const MySwal = withReactContent(Swal);
    const schema = object().shape({
        username: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max"),
        email: string().required("account.email.required").matches(EMAIL_REGEX, "account.email.invalid"),
        firstName: string().required("account.firstName.required").min(1, "account.firstName.min").max(32, "account.firstName.max"),
        lastName: string().required("account.lastName.required").min(1, "account.lastName.min").max(32, "account.lastName.max"),
        password: string().required("account.password.required").length(8, "account.password.min").max(8, "account.password.max")
    });
    const [account, setAccount] = useState({
        username: "",
        email: "",
        firstName: "",
        lastName: "",
        password: ""
    });
    const [errors, setErrors] = useState({});
    const [stage, setStage] = useState(1);
    EditFormGroup.defaultProps = {
        schema: schema,
        values: account,
        errors: errors,
        setValues: newAccount => setAccount(newAccount),
        setErrors: newErrors => setErrors(newErrors)
    };

    const handleFirstStage = () => {
        const tempAccount = {...account};
        delete tempAccount.password;
        const valid = validate(tempAccount, errors, setErrors, schema);
        if (valid) {
            setStage(1);
        }
    };

    const handleSecondStage = () => {
        if (validate({password: account.password}, errors, setErrors, schema)) {
            const tempAccount = {...account};
            const lastPasswordCharacters = generateLastPasswordCharacters(process.env.REACT_APP_LAST_PASSWORD_CHARACTERS);
            tempAccount.password += lastPasswordCharacters;
            axios.post("/register", tempAccount, {headers: {"Accept-Language": window.navigator.language}})
                .then(response => {
                    const alerts = [];
                    alerts.push({
                        title: t("register.password.header"),
                        html:
                            <div>
                                <p>{t("register.password.text1") + lastPasswordCharacters}</p>
                                <p>{t("register.password.text2")}</p>
                            </div>,
                        icon: "info"
                    });
                    alerts.push({
                        title: t("register.success.header"),
                        html:
                            <div>
                                <p>{t("register.success.text1")}</p>
                                <p>{t("register.success.text2")}</p>
                                <img src={`data:image/png;base64,${response.data}`} alt="qrCode"/>
                            </div>,
                        icon: "success"
                    });
                    MySwal.queue(alerts);
                    props.history.push("/");
                }).catch(() => {
                    Swal.fire(t("errors:common.header"),
                        t("errors:common.text"),
                        "error");
            });
        }
    };

    const generateLastPasswordCharacters = length => {
        let characters = "";
        for (let i = 0; i < length; i++) {
            characters += LAST_PASSWORD_CHARACTERS.charAt(Math.floor(Math.random() * LAST_PASSWORD_CHARACTERS.length));
        }
        return characters;
    }

    const renderFirstStage = () => {
        if (stage === 1) {
            return (
                <Col sm={5} className="form-container">
                    <Form>
                        <EditFormGroup id="username"
                                       label="account.username"
                                       required/>
                        <EditFormGroup id="email"
                                       label="account.email"
                                       required/>
                        <EditFormGroup id="firstName"
                                       label="account.firstName"
                                       required/>
                        <EditFormGroup id="lastName"
                                       label="account.lastName"
                                       required/>
                    </Form>
                    <ButtonToolbar className="justify-content-center">
                        <Button id="back1"
                                className="button"
                                onClick={() => props.history.goBack}>{t("navigation.back")}</Button>
                        <Button id="submit1"
                                className="button"
                                onClick={handleFirstStage}>{t("navigation.next")}</Button>
                    </ButtonToolbar>
                </Col>
            );
        }
    };

    const renderSecondStage = () => {
        if (stage === 2) {
            return (
                <Col sm={5} className="form-container">
                    <Form>
                        <EditFormGroup id="password"
                                       label="account.password"
                                       type="password"
                                       required/>
                    </Form>
                    <ButtonToolbar className="justify-content-center">
                        <Button id="back2"
                                className="button"
                                onClick={() => setStage(1)}>{t("navigation.back")}</Button>
                        <Button id="submit2"
                                className="button"
                                onClick={handleSecondStage}>{t("navigation.next")}</Button>
                    </ButtonToolbar>
                </Col>
            );
        }
    };

    return (
        <React.Fragment>
            <h1 className="text-center">{t("register.header")}</h1>
            <Row className="justify-content-center">
                {renderFirstStage()}
                {renderSecondStage()}
            </Row>
        </React.Fragment>
    );
};
