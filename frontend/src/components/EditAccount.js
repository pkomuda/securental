import React, { useEffect, useState } from "react";
import axios from "axios";
import Swal from "sweetalert2";
import {
    Breadcrumb,
    Button,
    ButtonToolbar,
    Col,
    Container,
    Form,
    FormCheck,
    FormControl,
    FormGroup,
    FormLabel,
    Row
} from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import { Spinner } from "./Spinner";
import { FlatFormGroup } from "./FlatFormGroup";
import { ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_CLIENT, ACCESS_LEVEL_EMPLOYEE } from "../utils/Constants";
import { EditFormGroup } from "./EditFormGroup";
import { bool, object, string } from "yup";
import { emailRegex } from "../utils/Validation";

export const EditAccount = props => {

    const {t} = useTranslation();
    const schema = object().shape({
        username: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max"),
        email: string().required("account.email.required").matches(emailRegex, "account.email.invalid"),
        firstName: string().required("account.firstName.required").min(1, "account.firstName.min").max(32, "account.firstName.max"),
        lastName: string().required("account.lastName.required").min(1, "account.lastName.min").max(32, "account.lastName.max"),
        active: bool()
    });
    const [account, setAccount] = useState({
        username: "",
        firstName: "",
        lastName: "",
        accessLevels: [],
        active: null
    });
    const [loaded, setLoaded] = useState(false);
    const [accessLevels, setAccessLevels] = useState({});
    const [errors, setErrors] = useState({});
    EditFormGroup.defaultProps = {
        schema: schema,
        values: account,
        errors: errors,
        setValues: newAccount => setAccount(newAccount),
        setErrors: newErrors => setErrors(newErrors)
    };

    const toAccessLevelsObject = accessLevelsArray => {
        const object = {};
        setAccessLevelValue(object, accessLevelsArray, ACCESS_LEVEL_ADMIN);
        setAccessLevelValue(object, accessLevelsArray, ACCESS_LEVEL_EMPLOYEE);
        setAccessLevelValue(object, accessLevelsArray, ACCESS_LEVEL_CLIENT);
        return object;
    };

    const setAccessLevelValue = (object, array, name) => {
        object[name] = array.includes(name);
    };

    useEffect(() => {
        axios.get(`/account/${props.match.params.username}`)
            .then(response => {
                setAccount(response.data);
                setAccessLevels(toAccessLevelsObject(response.data.accessLevels));
                setLoaded(true);
            }).catch(error => {
            Swal.fire(t("errors:common.header"),
                t(`errors:${error.response.data}`),
                "error");
        });
    }, [props.match.params.username, t]);

    const accountDetailsBreadcrumb = () => {
        switch (window.navigator.language) {
            case "pl":
                return `${t("breadcrumbs.accountDetails")} ${account.username}`;
            default:
                return `${account.username}'s ${t("breadcrumbs.accountDetails")}`;
        }
    };

    const activeBreadcrumb = () => {
        switch (window.navigator.language) {
            case "pl":
                return `${t("breadcrumbs.editAccount")} ${account.username}`;
            default:
                return `${t("navigation.edit")} ${account.username}'s ${t("breadcrumbs.editAccount")}`;
        }
    };

    const handleChangeAccessLevel = event => {
        const temp = {...accessLevels, [event.target.id]: !accessLevels[event.target.id]};
        setAccessLevels(temp);
        validateAccessLevels(temp);
    };

    const validateAccessLevels = (object) => {
        if (!Object.values(object).includes(true)) {
            document.getElementById("accessLevelsFeedback").style.display = "block";
            return false;
        } else {
            document.getElementById("accessLevelsFeedback").style.display = "none";
            return true;
        }
    };

    const handleChangeActive = event => {
        setAccount({...account, [event.target.id]: !account[event.target.id]});
    };

    if (loaded) {
        return (
            <React.Fragment>
                <Breadcrumb>
                    <LinkContainer to="/" exact>
                        <Breadcrumb.Item>Home</Breadcrumb.Item>
                    </LinkContainer>
                    <LinkContainer to="/listAccounts" exact>
                        <Breadcrumb.Item>{t("breadcrumbs.listAccounts")}</Breadcrumb.Item>
                    </LinkContainer>
                    <LinkContainer to={`accountDetails/${account.username}`} exact>
                        <Breadcrumb.Item>{accountDetailsBreadcrumb()}</Breadcrumb.Item>
                    </LinkContainer>
                    <Breadcrumb.Item active>{activeBreadcrumb()}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
                    <Row className="justify-content-center">
                        <Col sm={5} className="form-container">
                            <Form>
                                <EditFormGroup id="firstName"
                                               label="account.firstName"
                                               required/>
                                <EditFormGroup id="lastName"
                                               label="account.lastName"
                                               required/>
                                <FormGroup>
                                    <FormLabel className="font-weight-bold">{t("account.accessLevels")} *</FormLabel>
                                    <div>
                                        <FormCheck id={ACCESS_LEVEL_CLIENT} label={t(ACCESS_LEVEL_CLIENT)} onChange={handleChangeAccessLevel} inline defaultChecked={accessLevels[ACCESS_LEVEL_CLIENT]}/>
                                        <FormCheck id={ACCESS_LEVEL_EMPLOYEE} label={t(ACCESS_LEVEL_EMPLOYEE)} onChange={handleChangeAccessLevel} inline defaultChecked={accessLevels[ACCESS_LEVEL_EMPLOYEE]}/>
                                        <FormCheck id={ACCESS_LEVEL_ADMIN} label={t(ACCESS_LEVEL_ADMIN)} onChange={handleChangeAccessLevel} inline defaultChecked={accessLevels[ACCESS_LEVEL_ADMIN]}/>
                                        <p id="accessLevelsFeedback" className="invalid" style={{display: "none"}}>{t("validation:account.accessLevels.required")}</p>
                                    </div>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="font-weight-bold">{t("account.activity")}</FormLabel>
                                    <FormCheck id="active" label={t("account.active")} onChange={handleChangeActive}/>
                                </FormGroup>
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        variant="dark"
                                        className="button"
                                        onClick={() => props.history.goBack()}>{t("navigation.back")}</Button>
                                <Button id="edit"
                                        variant="dark"
                                        className="button"
                                        onClick={() => console.log(accessLevels)}>{t("navigation.submit")}</Button>
                            </ButtonToolbar>
                        </Col>
                    </Row>
                </Container>
            </React.Fragment>
        );
    } else {
        return <Spinner/>;
    }
};
