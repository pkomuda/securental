import React, { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { object, string } from "yup";
import { FormGroup } from "./FormGroup";
import { emailRegex } from "../utils/Validation";

export const Register = props => {

    const {t} = useTranslation();
    const schema = object().shape({
        username: string().required("account.username.invalid").min(1).max(32),
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

    const handleChangeAccount = newAccount => setAccount(newAccount);
    const handleChangeErrors = newErrors => setErrors(newErrors);

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
                        <FormGroup id="username"
                                   label="account.username"
                                   schema={schema}
                                   values={account}
                                   errors={errors}
                                   setValues={handleChangeAccount}
                                   setErrors={handleChangeErrors}/>

                        <FormGroup id="email"
                                   label="account.email"
                                   schema={schema}
                                   values={account}
                                   errors={errors}
                                   setValues={handleChangeAccount}
                                   setErrors={handleChangeErrors}/>

                        <FormGroup id="firstName"
                                   label="account.first.name"
                                   schema={schema}
                                   values={account}
                                   errors={errors}
                                   setValues={handleChangeAccount}
                                   setErrors={handleChangeErrors}/>

                        <FormGroup id="lastName"
                                   label="account.last.name"
                                   schema={schema}
                                   values={account}
                                   errors={errors}
                                   setValues={handleChangeAccount}
                                   setErrors={handleChangeErrors}/>

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
                    <Form className="center">
                        <FormGroup id="password"
                                   label="account.password"
                                   schema={schema}
                                   values={account}
                                   errors={errors}
                                   setValues={handleChangeAccount}
                                   setErrors={handleChangeErrors}/>

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
