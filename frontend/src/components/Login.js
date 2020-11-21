import React, { useState } from "react";
import axios from "axios";
import { Button, ButtonToolbar, Col, Form, FormControl, Row } from "react-bootstrap";
import { EditFormGroup } from "./EditFormGroup";
import { useTranslation } from "react-i18next";
import { object, string } from "yup";

export const Login = props => {

    const {t} = useTranslation();
    const schema = object().shape({
        username: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max"),
        otpCode: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max")
    });
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
            }).catch(error => {
                alert(error.response.data);
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
            .then(() => {
                props.history.push("/");
            }).catch(error => {
                alert(error.response.data);
        });
    };

    const renderFirstStage = () => {
        if (stage === 1) {
            return (
                <Col sm={5}>
                    <Form>
                        <EditFormGroup id="username"
                                       label="account.username"
                                       required/>
                    </Form>
                    <ButtonToolbar>
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
            let boxes = [];
            let currentIndex = 0;
            for (let i = 0; i <= authRequest.combination[authRequest.combination.length - 1]; i++) {
                if (authRequest.combination.includes(i)) {
                    boxes.push(
                        <div key={i} style={{display: "inline-block", position: "relative", marginBottom: "1em"}}>
                            <FormControl style={{width: "3em", marginRight: "1em"}} id={"enabled" + currentIndex} value={authRequest.characters[currentIndex]}
                                         onChange={handleChangeCharacters} maxLength="1"/>
                            <span style={{position: "absolute", marginLeft: "30%"}}>{i + 1}</span>
                        </div>
                    );
                    currentIndex++;
                } else {
                    boxes.push(
                        <div key={i} style={{display: "inline-block", position: "relative", marginBottom: "1em"}}>
                            <FormControl style={{width: "3em", marginRight: "1em"}} id={"disabled" + i} disabled={true}/>
                            <p style={{position: "absolute", marginLeft: "30%"}}>{i + 1}</p>
                        </div>
                    );
                }
            }
            return (
                <div>
                    <p>{t("login.password.characters")}</p>
                    <Form>
                        {boxes}
                    </Form>
                    <ButtonToolbar>
                        <Button id="back2"
                                variant="dark"
                                className="button"
                                onClick={() => setStage(1)}>{t("navigation.back")}</Button>
                        <Button id="clear"
                                variant="dark"
                                className="button"
                                onClick={handleClearCharacters}>Clear</Button>
                        <Button id="submit2"
                                variant="dark"
                                className="button"
                                onClick={handleSecondStage}>{t("navigation.next")}</Button>
                    </ButtonToolbar>
                </div>
            );
        }
    }

    const renderThirdStage = () => {
        if (stage === 3) {
            return (
                <Col sm={5}>
                    <Form>
                        <EditFormGroup id="otpCode"
                                       label="login.otp.code"
                                       required/>
                    </Form>
                    <ButtonToolbar>
                        <Button id="back3"
                                variant="dark"
                                className="button"
                                onClick={() => setStage(2)}>{t("navigation.back")}</Button>
                        <Button id="submit3"
                                variant="dark"
                                className="button"
                                onClick={handleThirdStage}>{t("login.sign.in")}</Button>
                    </ButtonToolbar>
                </Col>
            );
        }
    };

    return (
        <React.Fragment>
            <h1 className="text-center">{t("login.header")}</h1>
            <Row className="justify-content-center">
                {renderFirstStage()}
                {renderSecondStage()}
                {renderThirdStage()}
            </Row>
        </React.Fragment>
    );
};
