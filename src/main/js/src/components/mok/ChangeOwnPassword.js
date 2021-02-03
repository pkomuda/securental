import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useContext, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { object, string } from "yup";
import { handleError } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { validate } from "../../utils/Validation";
import { EditFormGroup } from "../common/EditFormGroup";

export const ChangeOwnPassword = props => {

    const {t} = useTranslation();
    const popup = withReactContent(Swal);
    const [userInfo] = useContext(AuthenticationContext);
    const schema = object().shape({
        password: string().required("account.password.required").min(8, "account.password.min").max(8, "account.password.max"),
        confirmPassword: string().required("account.confirmPassword.required").min(8, "account.password.min").max(8, "account.password.max")
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

    const validatePasswords = () => {
        if (changePasswordRequest.password === changePasswordRequest.confirmPassword) {
            document.getElementById("passwordsFeedback").style.display = "none";
            return true;
        } else {
            document.getElementById("passwordsFeedback").style.display = "block";
            return false;
        }
    };

    const handleSubmit = () => {
        if (!!(validate(changePasswordRequest, errors, setErrors, schema) & validatePasswords())) {
            Swal.fire({
                titleText: t("login.otp.code"),
                input: "password",
                preConfirm: otpCode => {
                    axios.put(`/changeOwnPassword/${userInfo.username}`,
                        changePasswordRequest,
                        {headers: {"Otp-Code": otpCode}})
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
            }).then(() => {});
        }
    };

    return (
        <React.Fragment>
            <Breadcrumb>
                <LinkContainer to="/" exact>
                    <Breadcrumb.Item>
                        <FontAwesomeIcon icon={faHome}/>
                    </Breadcrumb.Item>
                </LinkContainer>
                <LinkContainer to="/ownAccountDetails" exact>
                    <Breadcrumb.Item>{t("breadcrumbs.accountDetails")}</Breadcrumb.Item>
                </LinkContainer>
                <Breadcrumb.Item active>{t("breadcrumbs.changePassword")}</Breadcrumb.Item>
            </Breadcrumb>
            <Container>
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
                            <p id="passwordsFeedback" className="invalid" style={{display: "none"}}>{t("validation:account.passwords.match")}</p>
                        </Form>
                        <ButtonToolbar className="justify-content-center">
                            <Button id="back"
                                    onClick={() => props.history.push("/ownAccountDetails")}>{t("navigation.back")}</Button>
                            <Button id="ok"
                                    onClick={handleSubmit}>{t("navigation.ok")}</Button>
                        </ButtonToolbar>
                    </Col>
                </Row>
            </Container>
        </React.Fragment>
    );
};
