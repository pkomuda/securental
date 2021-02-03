import axios from "axios";
import React, { useState } from "react";
import { Button, ButtonToolbar, Col, Form, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { array, object, string } from "yup";
import { handleError } from "../../utils/Alerts";
import { EMAIL_REGEX, validate } from "../../utils/Validation";
import { Captcha } from "../common/Captcha";
import { EditFormGroup } from "../common/EditFormGroup";

export const Register = props => {

    const {t} = useTranslation();
    const [captcha, setCaptcha] = useState("");
    const popup = withReactContent(Swal);
    const schema = object().shape({
        username: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max"),
        email: string().required("account.email.required").matches(EMAIL_REGEX, "account.email.invalid"),
        firstName: string().required("account.firstName.required").min(1, "account.firstName.min").max(32, "account.firstName.max"),
        lastName: string().required("account.lastName.required").min(1, "account.lastName.min").max(32, "account.lastName.max"),
        password: string().required("account.password.required").min(8, "account.password.min").max(8, "account.password.max"),
        accessLevels: array(),
    });
    const [account, setAccount] = useState({
        username: "",
        email: "",
        firstName: "",
        lastName: "",
        password: "",
        accessLevels: []
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
            setStage(2);
        }
    };

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

    const handleSecondStage = () => {
        if (!!(validate({password: account.password}, errors, setErrors, schema) & validateCaptcha(captcha))) {
            axios.post("/register",
                account,
                {headers: {"Accept-Language": window.navigator.language, "Captcha-Response": captcha}})
                .then(response => {
                    const alerts = [];
                    alerts.push({
                        titleText: t("register.password.header"),
                        html:
                            <div>
                                <p>{t("register.password.text1") + response.data.lastPasswordCharacters}</p>
                                <p>{t("register.password.text2")}</p>
                            </div>,
                        icon: "info"
                    });
                    alerts.push({
                        titleText: t("register.success.header"),
                        html:
                            <div>
                                <p>{t("register.success.text1")}</p>
                                <p>{t("register.success.text2")}</p>
                                <img src={`data:image/png;base64,${response.data.qrCode}`} alt="qrCode"/>
                            </div>,
                        icon: "success"
                    });
                    popup.queue(alerts).then(() => {});
                    props.history.push("/");
                }).catch(error => {
                    handleError(error);
            });
        }
    };

    const renderFirstStage = () => {
        if (stage === 1) {
            return (
                <Col sm={6} className="form-container">
                    <h1 className="text-center">{t("register.header")}</h1>
                    <Form style={{marginTop: "2em"}}>
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
                                onClick={() => props.history.push("/")}>{t("navigation.back")}</Button>
                        <Button id="submit1"
                                onClick={handleFirstStage}>{t("navigation.next")}</Button>
                    </ButtonToolbar>
                </Col>
            );
        }
    };

    const renderSecondStage = () => {
        if (stage === 2) {
            return (
                <Col sm={6} className="form-container">
                    <h1 className="text-center">{t("register.header")}</h1>
                    <Form style={{marginTop: "2em"}}>
                        <EditFormGroup id="password"
                                       label="account.password"
                                       type="password"
                                       required/>
                    </Form>
                    <ButtonToolbar className="justify-content-center">
                        <Button id="back2"
                                onClick={() => setStage(1)}>{t("navigation.back")}</Button>
                        <Button id="submit2"
                                onClick={handleSecondStage}>{t("navigation.next")}</Button>
                    </ButtonToolbar>
                    <Captcha onChange={handleChangeCaptcha}/>
                </Col>
            );
        }
    };

    return (
        <React.Fragment>
            <Row className="justify-content-center">
                {renderFirstStage()}
                {renderSecondStage()}
            </Row>
        </React.Fragment>
    );
};
