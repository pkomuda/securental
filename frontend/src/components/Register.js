import React, { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { object, string } from "yup";
import { FormGroup } from "./FormGroup";
import { emailRegex, validate } from "../utils/Validation";

export const Register = props => {

    const {t} = useTranslation();
    const schema = object().shape({
        username: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max"),
        email: string().required("account.email.required").matches(emailRegex, "account.email.invalid"),
        firstName: string().required("account.firstName.required").min(1, "account.firstName.min").max(32, "account.firstName.max"),
        lastName: string().required("account.lastName.required").min(1, "account.lastName.min").max(32, "account.lastName.max"),
        password: string().required("account.password.required").min(8, "account.password.min").max(32, "account.password.max")
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
    FormGroup.defaultProps = {
        schema: schema,
        values: account,
        errors: errors,
        setValues: newAccount => setAccount(newAccount),
        setErrors: newErrors => setErrors(newErrors)
    };

    const handleFirstStage = () => {
        const tempAccount = {...account};
        delete tempAccount.password;
        if (validate(tempAccount, errors, setErrors, schema)) {
            setStage(2);
        }
    };

    const handleSecondStage = () => {
        const tempAccount = {password: account.password};
        if (validate(tempAccount, errors, setErrors, schema)) {
            alert(JSON.stringify(account));
        }
    };

    const renderFirstStage = () => {
        if (stage === 1) {
            return (
                <div>
                    <Form className="center">
                        <FormGroup id="username"
                                   label="account.username"/>
                        <FormGroup id="email"
                                   label="account.email"/>
                        <FormGroup id="firstName"
                                   label="account.firstName"/>
                        <FormGroup id="lastName"
                                   label="account.lastName"/>
                        <Button id="submit1"
                                variant="dark"
                                onClick={handleFirstStage}>{t("navigation.next")}</Button>
                    </Form>
                    <Button id="back1"
                            variant="dark"
                            onClick={() => props.history.goBack}
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
                                   label="account.password"/>
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
