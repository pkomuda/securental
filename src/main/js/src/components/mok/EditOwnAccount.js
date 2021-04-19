import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useContext, useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import { array, bool, mixed, object, string } from "yup";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_CLIENT, ACCESS_LEVEL_EMPLOYEE } from "../../utils/Constants";
import { EMAIL_REGEX, NAME_REGEX, validate } from "../../utils/Validation";
import { EditFormGroup } from "../common/EditFormGroup";
import { FlatFormGroup } from "../common/FlatFormGroup";
import { Spinner } from "../common/Spinner";

export const EditOwnAccount = props => {

    const {t} = useTranslation();
    const [userInfo] = useContext(AuthenticationContext);
    const schema = object().shape({
        username: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max"),
        email: string().required("account.email.required").matches(EMAIL_REGEX, "account.email.invalid"),
        firstName: string().required("account.firstName.required").min(1, "account.firstName.min").max(32, "account.firstName.max").matches(NAME_REGEX, "account.firstName.invalid"),
        lastName: string().required("account.lastName.required").min(1, "account.lastName.min").max(32, "account.lastName.max").matches(NAME_REGEX, "account.lastName.invalid"),
        active: bool(),
        confirmed: bool(),
        accessLevels: array(),
        signature: string(),
        password: mixed().nullable(),
        confirmPassword: mixed().nullable()
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
    FlatFormGroup.defaultProps = {
        values: account
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
        axios.get(`/ownAccount/${userInfo.username}`)
            .then(response => {
                setAccount(response.data);
                setAccessLevels(toAccessLevelsObject(response.data.accessLevels));
                setLoaded(true);
            }).catch(error => {
                handleError(error);
        });
    }, [t, userInfo.username]);

    const validateAccessLevels = (object) => {
        if (userInfo.currentAccessLevel === ACCESS_LEVEL_ADMIN) {
            if (!Object.values(object).includes(true)) {
                document.getElementById("accessLevelsFeedback").style.display = "block";
                return false;
            } else {
                document.getElementById("accessLevelsFeedback").style.display = "none";
                return true;
            }
        } else {
            return true;
        }
    };

    const handleSubmit = () => {
        if (!!(validate(account, errors, setErrors, schema) & validateAccessLevels(accessLevels))) {
            Swal.fire({
                titleText: t("login.otp.code"),
                input: "password",
                preConfirm: otpCode => {
                    const tempAccount = {...account};
                    tempAccount.accessLevels = Object.keys(accessLevels).filter(key => accessLevels[key]);
                    axios.put(`/ownAccount/${tempAccount.username}`,
                        tempAccount,
                        {headers: {"Otp-Code": otpCode}})
                        .then(() => {
                            handleSuccess("account.edit.success", "");
                            props.history.push("/ownAccountDetails");
                        }).catch(error => {
                        handleError(error);
                    });
                }
            }).then(() => {});
        }
    };

    const renderAccessLevels = () => {
        if (userInfo.accessLevels.length > 1) {
            return (
                <FormGroup>
                    <hr/>
                    <FormLabel className="flat-form-label">{t("account.accessLevels")}</FormLabel>
                    <FormControl id="accessLevels"
                                 value={account.accessLevels.map(a => t(a)).join(", ")}
                                 disabled
                                 plaintext/>
                </FormGroup>
            );
        }
    }

    const renderAdminInfo = () => {
        if (userInfo.currentAccessLevel === ACCESS_LEVEL_ADMIN) {
            return (
                <React.Fragment>
                    <FormGroup>
                        <hr/>
                        <FormLabel className="flat-form-label">{t("account.activity")}</FormLabel>
                        <FormControl id="active"
                                     value={account.active ? t("account.active") : t("account.inactive")}
                                     disabled
                                     plaintext/>
                        <hr/>
                    </FormGroup>
                    <FormGroup>
                        <FormLabel className="flat-form-label">{t("account.confirmation")}</FormLabel>
                        <FormControl id="active"
                                     value={account.confirmed ? t("account.confirmed") : t("account.notConfirmed")}
                                     disabled
                                     plaintext/>
                    </FormGroup>
                </React.Fragment>
            );
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
                    <LinkContainer to="/ownAccountDetails" exact>
                        <Breadcrumb.Item>{t("breadcrumbs.accountDetails")}</Breadcrumb.Item>
                    </LinkContainer>
                    <Breadcrumb.Item active>{t("breadcrumbs.editAccount")}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
                    <Row className="justify-content-center">
                        <Col sm={6} className="form-container">
                            <Form>
                                <FlatFormGroup id="username"
                                               label="account.username"/>
                                <FlatFormGroup id="email"
                                               label="account.email"/>
                                <EditFormGroup id="firstName"
                                               label="account.firstName"
                                               required/>
                                <hr/>
                                <EditFormGroup id="lastName"
                                               label="account.lastName"
                                               required/>
                                {renderAccessLevels()}
                                {renderAdminInfo()}
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        onClick={() => props.history.push("/ownAccountDetails")}>{t("navigation.back")}</Button>
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
