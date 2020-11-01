import React, { useState } from "react";
import axios from "axios";
import Swal from "sweetalert2";
import { Button, ButtonToolbar, Col, Form, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { object, string } from "yup";
import { FormGroup } from "./FormGroup";
import { emailRegex, validate } from "../utils/Validation";

export const Register = props => {

    const {t} = useTranslation();
    const schema = object().shape({
        username: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max"),
        email: string().required("account.email.required").matches(emailRegex, "account.email.invalid"),
        firstName: string().required("account.firstName.required").min(1, "account.firstName.min").max(32, "account.firstName.max"),
        lastName: string().required("account.lastName.required").min(1, "account.lastName.min").max(32, "account.lastName.max"),
        password: string().required("account.password.required").min(8, "account.password.min").max(32, "account.password.max")
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
    FormGroup.defaultProps = {
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
        console.log(valid);
        if (valid) {
            setStage(2);
        }
    };

    const handleSecondStage = () => {
        if (validate({password: account.password}, errors, setErrors, schema)) {
            const tempAccount = {...account};
            const lastPasswordCharacters = generateLastPasswordCharacters(process.env.REACT_APP_LAST_PASSWORD_CHARACTERS);
            tempAccount.password += lastPasswordCharacters;
            axios.post("/register", tempAccount, {headers: {"Accept-Language": window.navigator.language}})
                .then(() => {
                    const alerts = [];
                    alerts.push({
                        title: t("register.password.header"),
                        html: t("register.password.text1")
                            + lastPasswordCharacters
                            + t("register.password.text2"),
                        icon: "info"
                    });
                    alerts.push({
                        title: t("register.success.header"),
                        text: t("register.success.text"),
                        icon: "success"
                    });
                    Swal.queue(alerts);
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
        const allCharacters = "0123456789";
        for (let i = 0; i < length; i++) {
            characters += allCharacters.charAt(Math.floor(Math.random() * allCharacters.length));
        }
        return characters;
    }

    const renderFirstStage = () => {
        if (stage === 1) {
            return (
                <Col sm={5} style={{outline: "1px solid black"}}>
                    <Form>
                        <FormGroup id="username"
                                   label="account.username"
                                   required/>
                        <FormGroup id="email"
                                   label="account.email"
                                   required/>
                        <FormGroup id="firstName"
                                   label="account.firstName"
                                   required/>
                        <FormGroup id="lastName"
                                   label="account.lastName"
                                   required/>
                    </Form>
                    <ButtonToolbar className="justify-content-center">
                        <Button id="back1"
                                variant="dark"
                                className="button"
                                onClick={() => props.history.goBack}>{t("navigation.back")}</Button>
                        <Button id="submit1"
                                variant="dark"
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
                <Col sm={5}>
                    <Form>
                        <FormGroup id="password"
                                   label="account.password"
                                   required
                                   password/>
                    </Form>
                    <ButtonToolbar className="justify-content-center">
                        <Button id="back2"
                                variant="dark"
                                className="button"
                                onClick={() => setStage(1)}>{t("navigation.back")}</Button>
                        <Button id="submit2"
                                variant="dark"
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
