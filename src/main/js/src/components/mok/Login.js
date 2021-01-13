import axios from "axios";
import React, { useContext, useState } from "react";
import { Button, ButtonToolbar, Col, Form, FormControl, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { object, string } from "yup";
import { handleError } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { EditFormGroup } from "../EditFormGroup";

export const Login = props => {

    const {t} = useTranslation();
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

    const handleClearCharacters = () => {
        const tempAuthRequest = {...authRequest};
        const tempCharacters = [];
        for (let i = 0; i < tempAuthRequest.combination; i++) {
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
            }).catch(err => {
                handleError(err);
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
        console.log(tempAuthRequest);
        axios.post("/login", tempAuthRequest, {withCredentials: true})
            .then(response => {
                setUserInfo(response.data);
                props.history.push("/");
            }).catch(error => {
                console.log(error);
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
                                onClick={() => props.history.goBack}>{t("navigation.back")}</Button>
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

    const renderSecondStage = () => {
        if (stage === 2) {
            let boxes = [];
            let currentIndex = 0;
            for (let i = 0; i <= authRequest.combination[authRequest.combination.length - 1]; i++) {
                if (authRequest.combination.includes(i)) {
                    boxes.push(
                        <div key={i} style={{display: "inline-block", position: "relative", marginBottom: "1em"}}>
                            <FormControl style={{width: "2em", marginRight: "1em"}} id={"enabled" + currentIndex} value={authRequest.characters[currentIndex]}
                                         onChange={handleChangeCharacters} maxLength="1" type="password"/>
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
                                       required/>
                    </Form>
                    <ButtonToolbar className="justify-content-center">
                        <Button id="back3"
                                onClick={() => setStage(2)}>{t("navigation.back")}</Button>
                            <Button id="submit3"
                                    onClick={handleThirdStage}>{t("login.sign.in")}</Button>
                    </ButtonToolbar>
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
