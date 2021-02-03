import axios from "axios";
import React, { useContext, useState } from "react";
import { Button, ButtonToolbar, Col, Form, FormControl, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { object, string } from "yup";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { formatDate } from "../../utils/DateTime";
import { EditFormGroup } from "../common/EditFormGroup";

export const Login = props => {

    const {t} = useTranslation();
    const popup = withReactContent(Swal);
    const schema = object().shape({
        username: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max"),
        otpCode: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max")
    });
    const [, setUserInfo] = useContext(AuthenticationContext);
    const [authRequest, setAuthRequest] = useState({
        username: "",
        combination: [],
        characters: [],
        otpCode: ""
    });
    const [errors, setErrors] = useState({});
    const [stage, setStage] = useState(1);
    EditFormGroup.defaultProps = {
        schema: schema,
        values: authRequest,
        errors: errors,
        setValues: newAuthRequest => setAuthRequest(newAuthRequest),
        setErrors: newErrors => setErrors(newErrors)
    };

    const handleChangeCharacters = event => {
        const currentIndex = parseInt(event.target.id.slice(-1));
        const tempAuthRequest = {...authRequest};
        const tempCharacters = tempAuthRequest.characters;
        tempCharacters[currentIndex] = event.target.value;
        tempAuthRequest.characters = tempCharacters;
        setAuthRequest(tempAuthRequest);
        if (currentIndex !== tempAuthRequest.combination.length - 1) {
            document.getElementById(event.target.id.slice(0, -1) + (currentIndex + 1)).focus();
        }
    };

    const handleBackspace = event => {
        if (event.keyCode === 8) {
            const currentIndex = parseInt(event.target.id.slice(-1));
            const tempAuthRequest = {...authRequest};
            const tempCharacters = tempAuthRequest.characters;
            if (currentIndex === tempAuthRequest.combination.length - 1) {
                if (tempCharacters[currentIndex]) {
                    tempCharacters[currentIndex] = "";
                    tempAuthRequest.characters = tempCharacters;
                    setAuthRequest(tempAuthRequest);
                    document.getElementById(event.target.id.slice(0, -1) + (currentIndex)).focus();
                } else {
                    tempCharacters[currentIndex - 1] = "";
                    tempAuthRequest.characters = tempCharacters;
                    setAuthRequest(tempAuthRequest);
                    document.getElementById(event.target.id.slice(0, -1) + (currentIndex - 1)).focus();
                }
            } else {
                tempCharacters[currentIndex - 1] = "";
                tempAuthRequest.characters = tempCharacters;
                setAuthRequest(tempAuthRequest);
                if (currentIndex !== 0) {
                    document.getElementById(event.target.id.slice(0, -1) + (currentIndex - 1)).focus();
                }
            }
        }
    };

    const handleClearCharacters = () => {
        const tempAuthRequest = {...authRequest};
        const tempCharacters = [];
        for (let i = 0; i < tempAuthRequest.combination.length; i++) {
            tempCharacters.push("");
        }
        tempAuthRequest.characters = tempCharacters;
        setAuthRequest(tempAuthRequest);
    };

    const handleFirstStage = () => {
        axios.get(`/initializeLogin/${authRequest.username}`)
            .then(response => {
                const tempAuthRequest = {...authRequest};
                tempAuthRequest.combination = response.data;
                const tempCharacters = [];
                for (let i = 0; i < tempAuthRequest.combination; i++) {
                    tempCharacters.push("");
                }
                tempAuthRequest.characters = tempCharacters;
                setAuthRequest(tempAuthRequest);
                setStage(2);
            }).catch(error => {
                handleError(error);
        });
    };

    const handleSecondStage = () => {
        let valid = true;
        for (let character of authRequest.characters) {
            if (character === "") {
                valid = false;
            }
        }
        if (valid) {
            setStage(3);
        }
    };

    const handleThirdStage = () => {
        const tempAuthRequest = {...authRequest};
        tempAuthRequest.otpCode = parseInt(tempAuthRequest.otpCode);
        tempAuthRequest.characters = tempAuthRequest.characters.join("");
        axios.post("/login", tempAuthRequest)
            .then(response => {
                setUserInfo(response.data);
                popup.fire({
                    titleText: t("navigation.success"),
                    html:
                        <div>
                            <p>{t("login.last.success") + formatDate(response.data.lastSuccessfulAuthentication)}</p>
                            <p>{t("login.last.failure") + formatDate(response.data.lastFailedAuthentication)}</p>
                            <p>{t("login.last.ipAddress") + response.data.lastAuthenticationIpAddress}</p>
                        </div>,
                    icon: "success"
                }).then(() => {});
                props.history.push("/");
            }).catch(error => {
                handleError(error);
        });
    };

    const renderSessionExpiredText = () => {
        if (props.match.params.session === "sessionExpired") {
            return <h5 className="text-center">{t("login.session.expired")}</h5>;
        }
    };

    const renderFirstStage = () => {
        if (stage === 1) {
            return (
                <Col sm={6} className="form-container">
                    <h1 className="text-center">{t("login.header")}</h1>
                    {renderSessionExpiredText()}
                    <Form style={{marginTop: "2em"}}>
                        <EditFormGroup id="username"
                                       label="account.username"
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

    const blockMargin = index => {
        if (index < 9) {
            return {position: "absolute", marginLeft: "25%"};
        } else {
            return {position: "absolute", marginLeft: "15%"};
        }
    };

    const handleForgotPassword = () => {
        Swal.fire({
            titleText: t("login.password.reset.header"),
            text: t("login.password.reset.text"),
            icon: "info"
        }).then(result => {
            if (result.isConfirmed) {
                axios.put(`/initializeResetPassword/${authRequest.username}`, {}, {headers: {"Accept-Language": window.navigator.language}})
                    .then(() => {
                        handleSuccess("reset.mail.sent", "");
                        props.history.push("/");
                    }).catch(error => {
                    handleError(error);
                        props.history.push("/");
                });
            }
        });
    };

    const handleLostOtp = () => {
        Swal.fire({
            titleText: t("login.otp.lost.header"),
            text: "",
            icon: "info"
        }).then(() => {});
    };

    const renderSecondStage = () => {
        if (stage === 2) {
            let boxes = [];
            let currentIndex = 0;
            for (let i = 0; i <= authRequest.combination[authRequest.combination.length - 1]; i++) {
                if (authRequest.combination.includes(i)) {
                    boxes.push(
                        <div key={i} style={{display: "inline-block", position: "relative", marginBottom: "1em"}}>
                            <FormControl style={{width: "2em", marginRight: "1em"}} id={"enabled" + currentIndex} value={authRequest.characters[currentIndex]}
                                         onChange={handleChangeCharacters} onKeyDown={handleBackspace} maxLength="1" type="password"/>
                            <span style={blockMargin(i)}>{i + 1}</span>
                        </div>
                    );
                    currentIndex++;
                } else {
                    boxes.push(
                        <div key={i} style={{display: "inline-block", position: "relative", marginBottom: "1em"}}>
                            <FormControl style={{width: "2em", marginRight: "1em"}} id={"disabled" + i} disabled/>
                            <p style={blockMargin(i)}>{i + 1}</p>
                        </div>
                    );
                }
            }
            return (
                <div>
                    <Form className="form-container">
                        <h1 className="text-center">{t("login.header")}</h1>
                        {renderSessionExpiredText()}
                        <p className="font-weight-bold" style={{marginTop: "2em"}}>{t("login.password.characters")} *</p>
                        {boxes}
                        <ButtonToolbar className="justify-content-center" style={{marginTop: "1em"}}>
                            <Button id="back2"
                                    onClick={() => setStage(1)}>{t("navigation.back")}</Button>
                            <Button id="clear"
                                    onClick={handleClearCharacters}>{t("login.clear")}</Button>
                            <Button id="submit2"
                                    onClick={handleSecondStage}>{t("navigation.next")}</Button>
                        </ButtonToolbar>
                        <div className="text-center" style={{marginTop: "1em"}}>
                            <Link to="#"
                                  onClick={handleForgotPassword}>{t("login.password.forgot")}</Link>
                        </div>
                    </Form>
                </div>
            );
        }
    }

    const renderThirdStage = () => {
        if (stage === 3) {
            return (
                <Col sm={6} className="form-container">
                    <h1 className="text-center">{t("login.header")}</h1>
                    {renderSessionExpiredText()}
                    <Form style={{marginTop: "2em"}}>
                        <EditFormGroup id="otpCode"
                                       label="login.otp.code"
                                       type="password"
                                       required/>
                    </Form>
                    <ButtonToolbar className="justify-content-center">
                        <Button id="back3"
                                onClick={() => setStage(2)}>{t("navigation.back")}</Button>
                        <Button id="submit3"
                                onClick={handleThirdStage}>{t("login.sign.in")}</Button>
                    </ButtonToolbar>
                    <div className="text-center" style={{marginTop: "1em"}}>
                        <Link to="#"
                              onClick={handleLostOtp}>{t("login.otp.lost")}</Link>
                    </div>
                </Col>
            );
        }
    };

    return (
        <React.Fragment>
            <Row className="justify-content-center">
                {renderFirstStage()}
                {renderSecondStage()}
                {renderThirdStage()}
            </Row>
        </React.Fragment>
    );
};
