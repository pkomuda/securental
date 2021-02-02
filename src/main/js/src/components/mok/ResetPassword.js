import axios from "axios";
import React, { useState } from "react";
import { Button, ButtonToolbar, Col, Form, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { object, string } from "yup";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { formatDate } from "../../utils/DateTime";
import { validate } from "../../utils/Validation";
import { Captcha } from "../common/Captcha";
import { EditFormGroup } from "../common/EditFormGroup";

export const ResetPassword = props => {

    const {t} = useTranslation();
    const popup = withReactContent(Swal);
    const [captcha, setCaptcha] = useState("");
    const schema = object().shape({
        password: string().required("account.password.required").min(8, "account.password.min").max(8, "account.password.max"),
        confirmPassword: string().required("acccount.confirmPassword.required").min(8, "account.password.min").max(8, "account.password.max")
    });
    const [changePasswordRequest, setChangePasswordRequest] = useState({
        password: "",
        confirmPassword: ""
    });
    const [errors, setErrors] = useState({});
    EditFormGroup.defaultProps = {
        schema: schema,
        values: changePasswordRequest,
        errors: errors,
        setValues: newChangePasswordRequest => setChangePasswordRequest(newChangePasswordRequest),
        setErrors: newErrors => setErrors(newErrors)
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

    const handleSubmit = () => {
        if (!!(validate(changePasswordRequest, errors, setErrors, schema) & validateCaptcha(captcha))) {
            axios.put(`/resetOwnPassword/${window.location.pathname.substring(window.location.pathname.lastIndexOf("/") + 1)}`,
                changePasswordRequest,
                {headers: {"Captcha-Response": captcha}})
                .then(response => {
                    popup.fire({
                        titleText: t("register.password.header"),
                        html:
                            <div>
                                <p>{t("register.password.text1") + response.data.lastPasswordCharacters}</p>
                                <p>{t("register.password.text2")}</p>
                            </div>,
                        icon: "info"
                    }).then(() => {});
                    props.history.push("/");
                }).catch(error => {
                    handleError(error);
            });
        }
    };

    return (
        <Row className="justify-content-center">
            <Col sm={6} className="form-container">
                <h1 className="text-center">{t("reset.header")}</h1>
                <Form>
                    <EditFormGroup id="password"
                                   label="account.password"
                                   type="password"
                                   required/>
                    <EditFormGroup id="confirmPassword"
                                   label="account.confirmPassword"
                                   type="password"
                                   required/>
                </Form>
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
