import React, { useState } from "react";
import { Button, Form, FormControl, FormGroup, FormLabel } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { object, string } from "yup";
import { emailRegex } from "../utils/Validation";

export const Register = props => {

    const {t} = useTranslation();
    const schema = object().shape({
        username: string().required().min(1).max(32),
        email: string().required().matches(emailRegex),
        firstName: string().required().min(1).max(32),
        lastName: string().required().min(1).max(32),
    });
    const [account, setAccount] = useState({
        username: "",
        email: "",
        firstName: "",
        lastName: "",
        password: ""
    });
    const [errors, setErrors] = useState([]);
    const [stage, setStage] = useState(1);

    const handleChange = event => {
        const key = event.target.id;
        const value = event.target.value;
        const tempAccount = {...account};
        tempAccount[key] = value;
        setAccount(tempAccount);
        try {
            schema.validateSyncAt(key, tempAccount);
            document.getElementById(key).classList.remove("is-invalid")
        } catch (err) {
            if (errors.some(e => e.path === err.path)) {
                const tempErrors = errors.filter(e => e.path !== err.path);
                setErrors([...tempErrors, {path: err.path, message: err.message}]);
            } else {
                setErrors([...errors, {path: err.path, message: err.message}]);
            }
            document.getElementById(key).classList.add("is-invalid")
        }
    };

    const handleFirstStage = () => {
        console.log(JSON.stringify(account));
        console.log(JSON.stringify(errors));
        // setStage(2);
    };

    const handleSecondStage = () => {
        alert(JSON.stringify(account));
    };

    const renderFirstStage = () => {
        if (stage === 1) {
            return (
                <div>
                    <Form className="center">
                        <FormGroup>
                            <FormLabel>{t("account.username")}</FormLabel>
                            <FormControl id="username"
                                         value={account.username}
                                         onChange={handleChange}/>
                            <FormControl.Feedback type="invalid">{errors.some(e => e.path === "username") && errors.find(e => e.path === "username").message}</FormControl.Feedback>
                        </FormGroup>

                        <FormGroup>
                            <FormLabel>{t("account.email")}</FormLabel>
                            <FormControl id="email"
                                         value={account.email}
                                         onChange={handleChange}/>
                        </FormGroup>

                        <FormGroup>
                            <FormLabel>{t("account.first.name")}</FormLabel>
                            <FormControl id="firstName"
                                         value={account.firstName}
                                         onChange={handleChange}/>
                        </FormGroup>

                        <FormGroup>
                            <FormLabel>{t("account.last.name")}</FormLabel>
                            <FormControl id="lastName"
                                         value={account.lastName}
                                         onChange={handleChange}/>
                        </FormGroup>

                        <Button id="submit1"
                                variant="dark"
                                onClick={handleFirstStage}>{t("navigation.next")}</Button>
                    </Form>
                    <Button id="back1"
                            variant="dark"
                            onClick={props.history.goBack}
                            style={{marginTop: "5px"}}>{t("navigation.back")}</Button>
                </div>
            );
        }
    };

    const renderSecondStage = () => {
        if (stage === 2) {
            return (
                <div>
                    <Form className="center"
                          onSubmit={handleSecondStage}>
                        <FormGroup>
                            <FormLabel>{t("account.password")}</FormLabel>
                            <FormControl id="password"
                                         value={account.password}
                                         onChange={handleChange}/>
                        </FormGroup>

                        <Button id="submit2"
                                variant="dark"
                                onClick={handleSecondStage}>{t("navigation.next")}</Button>
                    </Form>
                    <Button id="back2"
                            variant="dark"
                            onClick={() => setStage(1)}
                            style={{marginTop: "5px"}}>{t("navigation.back")}</Button>
                </div>
            );
        }
    };

    return (
        <div>
            <h1 className="center">{t("register.header")}</h1>
            {renderFirstStage()}
            {renderSecondStage()}
        </div>
    );
};
