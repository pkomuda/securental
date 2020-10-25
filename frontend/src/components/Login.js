import React, { useState } from "react";
import axios from "axios";
import { Button, Form, FormControl, FormGroup } from "react-bootstrap";
import { useTranslation } from "react-i18next";

export const Login = props => {

    const {t} = useTranslation();
    const [stage, setStage] = useState(1);
    const [authRequest, setAuthRequest] = useState({
        "username": "",
        "combination": [],
        "characters": [],
        "totpCode": ""
    });

    const handleChangeProperty = (event, property) => {
        let tempAuthRequest = {...authRequest};
        tempAuthRequest[property] = event.target.value;
        setAuthRequest(tempAuthRequest);
    };

    const handleFirstStage = () => {
        axios.get("/initializeLogin/" + authRequest.username)
            .then(response => {
                let tempAuthRequest = {...authRequest};
                tempAuthRequest.combination = response.data;
                setAuthRequest(tempAuthRequest);
                setStage(2);
            }).catch(error => {
                alert(error.response.data);
        });
    };

    const handleSecondStage = () => {
        let chars = [];
        for (let i = 0; i < authRequest.combination.length - 1; i++) {
            chars[i] = i + " ";
        }
        let tempAuthRequest = {...authRequest};
        tempAuthRequest.characters = chars;
        setAuthRequest(tempAuthRequest);
        setStage(3);
    };

    const handleThirdStage = () => {
        axios.post("/login", authRequest)
            .then(response => {
                alert(response.data);
            }).catch(error => {
            alert(error.response.data);
        });
    };

    const renderFirstStage = () => {
        if (stage === 1) {
            return (
                <div>
                    <h4 className="center">{t("account.username")}</h4>
                    <Form className="center">
                        <FormGroup>
                            <FormControl id="username" value={authRequest.username} onChange={event => handleChangeProperty(event, "username")} style={{width: "20%", display: "inline-block"}}/>
                        </FormGroup>
                        <Button id="submit1"
                                variant="dark"
                                onClick={handleFirstStage}>{t("navigation.next")}</Button>
                    </Form>
                </div>
            );
        }
    };

    const renderSecondStage = () => {
        if (stage === 2) {
            let boxes = [];
            for (let i = 0; i < authRequest.combination[authRequest.combination.length - 1] + 1; i++) {
                if (authRequest.combination.includes(i)) {
                    boxes.push(
                        <div key={i} style={{display: "inline-block", position: "relative", marginBottom: "1em"}}>
                            <FormControl style={{width: "3em", marginRight: "1em"}} id={"textbox" + i} value={authRequest.characters[i]}
                                         onChange={event => handleChangeProperty(event, "characters")}
                                         disabled={false}/>
                            <p style={{position: "absolute", marginLeft: "30%"}}>{i + 1}</p>
                        </div>
                    );
                } else {
                    boxes.push(
                        <div key={i} style={{display: "inline-block", position: "relative", marginBottom: "1em"}}>
                            <FormControl style={{width: "3em", marginRight: "1em"}} id={"textbox" + i} onChange={event => handleChangeProperty(event, "characters")}
                                         disabled={true}/>
                            <p style={{position: "absolute", marginLeft: "30%"}}>{i + 1}</p>
                        </div>
                    );
                }
            }
            return (
                <div>
                    <h4 className="center">{t("account.password.characters")}</h4>
                    <Form className="center">
                        {boxes}
                        <br/>
                        <br/>
                        <Button id="submit2"
                                variant="dark"
                                onClick={handleSecondStage}>{t("navigation.next")}</Button>
                    </Form>
                </div>
            );
        }
    }

    const renderThirdStage = () => {
        if (stage === 3) {
            return (
                <div>
                    <h2 className="center">{t("login.otp.code")}</h2>
                    <Form className="center">
                        <FormGroup>
                            <FormControl id="code" style={{width: "20%", display: "inline-block"}}/>
                        </FormGroup>
                        <Button id="submit3"
                                variant="dark"
                                onClick={handleThirdStage}>{t("login.sign.in")}</Button>
                    </Form>
                </div>
            );
        }
    };

    return (
        <div>
            <h1 className="center">{t("login.header")}</h1>
            {renderFirstStage()}
            {renderSecondStage()}
            {renderThirdStage()}
            <Button id="back"
                    variant="dark"
                    onClick={props.history.goBack}
                    style={{marginTop: "5px"}}>{t("navigation.back")}</Button>
        </div>
    );
};
