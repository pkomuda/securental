import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormCheck, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import { array, bool, mixed, object, string } from "yup";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_CLIENT, ACCESS_LEVEL_EMPLOYEE } from "../../utils/Constants";
import { EMAIL_REGEX, NAME_REGEX, validate } from "../../utils/Validation";
import { EditFormGroup } from "../common/EditFormGroup";
import { Spinner } from "../common/Spinner";

export const EditAccount = props => {

    const {t} = useTranslation();
    const schema = object().shape({
        username: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max"),
        email: string().required("account.email.required").matches(EMAIL_REGEX, "account.email.invalid"),
        firstName: string().required("account.firstName.required").min(1, "account.firstName.min").max(32, "account.firstName.max").matches(NAME_REGEX, "account.firstName.invalid"),
        lastName: string().required("account.lastName.required").min(1, "account.lastName.min").max(32, "account.lastName.max").matches(NAME_REGEX, "account.lastName.invalid"),
        active: bool(),
        confirmed: bool(),
        accessLevels: array(),
        signature: string(),
        password: mixed().nullable()
    });
    const [account, setAccount] = useState({
        username: "",
        firstName: "",
        lastName: "",
        accessLevels: [],
        active: false
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

    const setAccessLevelValue = (object, array, name) => {
        object[name] = array.includes(name);
    };

    useEffect(() => {
        const toAccessLevelsObject = accessLevelsArray => {
            const object = {};
            setAccessLevelValue(object, accessLevelsArray, ACCESS_LEVEL_ADMIN);
            setAccessLevelValue(object, accessLevelsArray, ACCESS_LEVEL_EMPLOYEE);
            setAccessLevelValue(object, accessLevelsArray, ACCESS_LEVEL_CLIENT);
            return object;
        };
        axios.get(`/account/${props.match.params.username}`)
            .then(response => {
                setAccount(response.data);
                setAccessLevels(toAccessLevelsObject(response.data.accessLevels));
                setLoaded(true);
            }).catch(error => {
                handleError(error);
        });
    }, [props.match.params.username, t]);

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

    const handleSubmit = () => {
        if (!!(validate(account, errors, setErrors, schema) & validateAccessLevels(accessLevels))) {
            Swal.fire({
                titleText: t("login.otp.code"),
                input: "password",
                preConfirm: otpCode => {
                    const tempAccount = {...account};
                    tempAccount.accessLevels = Object.keys(accessLevels).filter(key => accessLevels[key]);
                    axios.put(`/account/${tempAccount.username}`,
                        tempAccount,
                        {headers: {"Otp-Code": otpCode}})
                        .then(() => {
                            handleSuccess("account.edit.success", "");
                            props.history.push(`/accountDetails/${tempAccount.username}`);
                        }).catch(error => {
                        handleError(error);
                    });
                }
            }).then(() => {});
        }
    };

    if (loaded) {
        return (
            <React.Fragment>
                <Breadcrumb>
                    <LinkContainer to="/" exact>
                        <Breadcrumb.Item>
                            <FontAwesomeIcon icon={faHome}/>
                        </Breadcrumb.Item>
                    </LinkContainer>
                    <LinkContainer to="/listAccounts" exact>
                        <Breadcrumb.Item>{t("breadcrumbs.listAccounts")}</Breadcrumb.Item>
                    </LinkContainer>
                    <LinkContainer to={`/accountDetails/${account.username}`} exact>
                        <Breadcrumb.Item>{t("breadcrumbs.accountDetails")}</Breadcrumb.Item>
                    </LinkContainer>
                    <Breadcrumb.Item active>{t("breadcrumbs.editAccount")}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
                    <Row className="justify-content-center">
                        <Col sm={6} className="form-container">
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
                                    <FormCheck id="active" label={t("account.active")} onChange={handleChangeActive} defaultChecked={account.active}/>
                                </FormGroup>
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        onClick={() => props.history.push(`/accountDetails/${account.username}`)}>{t("navigation.back")}</Button>
                                <Button id="edit"
                                        onClick={handleSubmit}>{t("navigation.submit")}</Button>
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
