import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormCheck, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import { bool, object, string } from "yup";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_CLIENT, ACCESS_LEVEL_EMPLOYEE } from "../../utils/Constants";
import { EMAIL_REGEX, validate } from "../../utils/Validation";
import { EditFormGroup } from "../common/EditFormGroup";

export const AddAccount = props => {

    const {t} = useTranslation();
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
            tempAccount.accessLevels = Object.keys(accessLevels).filter(key => accessLevels[key]);
            axios.post("/account", tempAccount, {withCredentials: true, headers: {"Accept-Language": window.navigator.language}})
                .then(() => {
                    handleSuccess("account.add.success", "");
                    props.history.push("/");
                }).catch(error => {
                    handleError(error);
            });
        }
    };

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
                    <Col sm={6} className="form-container">
                        <Form>
                            <EditFormGroup id="username"
                                           label="account.username"
                                           required/>
                            <EditFormGroup id="password"
                                           label="account.password"
                                           type="password"
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
                                    onClick={() => props.history.goBack}>{t("navigation.back")}</Button>
                            <Button id="submit"
                                    onClick={handleSubmit}>{t("navigation.submit")}</Button>
                        </ButtonToolbar>
                    </Col>
                </Row>
            </Container>
        </React.Fragment>
    );
};
