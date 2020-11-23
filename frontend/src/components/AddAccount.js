import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormCheck, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { bool, object, string } from "yup";
import { ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_CLIENT, ACCESS_LEVEL_EMPLOYEE, LAST_PASSWORD_CHARACTERS } from "../utils/Constants";
import { EMAIL_REGEX, validate } from "../utils/Validation";
import { EditFormGroup } from "./EditFormGroup";

export const AddAccount = props => {

    const {t} = useTranslation();
    const MySwal = withReactContent(Swal);
    const schema = object().shape({
        username: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max"),
        email: string().required("account.email.required").matches(EMAIL_REGEX, "account.email.invalid"),
        firstName: string().required("account.firstName.required").min(1, "account.firstName.min").max(32, "account.firstName.max"),
        lastName: string().required("account.lastName.required").min(1, "account.lastName.min").max(32, "account.lastName.max"),
        active: bool(),
        password: string().required("account.password.required").length(8, "account.password.min").max(8, "account.password.max")
    });
    const [account, setAccount] = useState({
        username: "",
        email: "",
        firstName: "",
        lastName: "",
        active: false,
        password: ""
    });
    const [accessLevels, setAccessLevels] = useState({
        [ACCESS_LEVEL_ADMIN]: false,
        [ACCESS_LEVEL_EMPLOYEE]: false,
        [ACCESS_LEVEL_CLIENT]: false
    });
    const [errors, setErrors] = useState({});
    EditFormGroup.defaultProps = {
        schema: schema,
        values: account,
        errors: errors,
        setValues: newAccount => setAccount(newAccount),
        setErrors: newErrors => setErrors(newErrors)
    };

    const handleSubmit = () => {
        if (!!(validate(account, errors, setErrors, schema) & validateAccessLevels(accessLevels))) {
            const tempAccount = {...account};
            const lastPasswordCharacters = generateLastPasswordCharacters(process.env.REACT_APP_LAST_PASSWORD_CHARACTERS);
            tempAccount.password += lastPasswordCharacters;
            tempAccount.accessLevels = Object.keys(accessLevels).filter(key => accessLevels[key]);
            console.log(tempAccount);
            axios.post("/account", tempAccount, {headers: {"Accept-Language": window.navigator.language}})
                .then(() => {
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

    const handleChangeAccessLevel = event => {
        const temp = {...accessLevels, [event.target.id]: !accessLevels[event.target.id]};
        setAccessLevels(temp);
        validateAccessLevels(temp);
    };

    const validateAccessLevels = (object) => {
        if (!Object.values(object).includes(true)) {
            document.getElementById("accessLevelsFeedback").style.display = "block";
            return false;
        } else {
            document.getElementById("accessLevelsFeedback").style.display = "none";
            return true;
        }
    };

    const handleChangeActive = event => {
        setAccount({...account, [event.target.id]: !account[event.target.id]});
    };

    return (
        <React.Fragment>
            <Breadcrumb>
                <LinkContainer to="/" exact>
                    <Breadcrumb.Item>
                        <FontAwesomeIcon icon={faHome}/>
                    </Breadcrumb.Item>
                </LinkContainer>
                <Breadcrumb.Item active>{t("breadcrumbs.addAccount")}</Breadcrumb.Item>
            </Breadcrumb>
            <Container>
                <Row className="justify-content-center">
                    <Col sm={5} className="form-container">
                        <Form>
                            <EditFormGroup id="username"
                                           label="account.username"
                                           required/>
                            <EditFormGroup id="password"
                                           label="account.password"
                                           required
                                           password/>
                            <EditFormGroup id="email"
                                           label="account.email"
                                           required/>
                            <EditFormGroup id="firstName"
                                           label="account.firstName"
                                           required/>
                            <EditFormGroup id="lastName"
                                           label="account.lastName"
                                           required/>
                            <FormGroup>
                                <FormLabel className="font-weight-bold">{t("account.accessLevels")} *</FormLabel>
                                <div>
                                    <FormCheck id={ACCESS_LEVEL_CLIENT} label={t(ACCESS_LEVEL_CLIENT)} onChange={handleChangeAccessLevel} inline/>
                                    <FormCheck id={ACCESS_LEVEL_EMPLOYEE} label={t(ACCESS_LEVEL_EMPLOYEE)} onChange={handleChangeAccessLevel} inline/>
                                    <FormCheck id={ACCESS_LEVEL_ADMIN} label={t(ACCESS_LEVEL_ADMIN)} onChange={handleChangeAccessLevel} inline/>
                                    <p id="accessLevelsFeedback" className="invalid" style={{display: "none"}}>{t("validation:account.accessLevels.required")}</p>
                                </div>
                            </FormGroup>
                            <FormGroup>
                                <FormLabel className="font-weight-bold">{t("account.activity")}</FormLabel>
                                <FormCheck id="active" label={t("account.active")} onChange={handleChangeActive}/>
                            </FormGroup>
                        </Form>
                        <ButtonToolbar className="justify-content-center">
                            <Button id="back"
                                    className="button"
                                    onClick={() => props.history.goBack}>{t("navigation.back")}</Button>
                            <Button id="submit"
                                    className="button"
                                    onClick={handleSubmit}>{t("navigation.submit")}</Button>
                        </ButtonToolbar>
                    </Col>
                </Row>
            </Container>
        </React.Fragment>
    );
};
