import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useContext, useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { handleError } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { ACCESS_LEVEL_ADMIN } from "../../utils/Constants";
import { FlatFormGroup } from "../common/FlatFormGroup";
import { Spinner } from "../common/Spinner";

export const OwnAccountDetails = props => {

    const {t} = useTranslation();
    const popup = withReactContent(Swal);
    const [userInfo] = useContext(AuthenticationContext);
    const [account, setAccount] = useState({
        username: "",
        email: "",
        firstName: "",
        lastName: "",
        accessLevels: [],
        active: false,
        confirmed: false
    });
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        axios.get(`/ownAccount/${userInfo.username}`)
            .then(response => {
                setAccount(response.data);
                setLoaded(true);
            }).catch(error => {
                handleError(error);
        });
    }, [t, userInfo.username]);

    FlatFormGroup.defaultProps = {
        values: account
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

    const handleQrCode = () => {
        axios.get(`/qrCode/${userInfo.username}`)
            .then(response => {
                const alerts = [];
                alerts.push({
                    title: t("register.success.header"),
                    html:
                        <div>
                            <p>{t("register.success.text1")}</p>
                            <p>{t("register.success.text2")}</p>
                            <img src={`data:image/png;base64,${response.data.qrCode}`} alt="qrCode"/>
                        </div>,
                    icon: "success"
                });
                popup.queue(alerts).then(() => {});
            }).catch(error => {
                handleError(error);
        });
    };

    const adminButtons = () => {
        if (userInfo.currentAccessLevel === ACCESS_LEVEL_ADMIN) {
            return <Button id="qrCode"
                           onClick={handleQrCode}>{t("account.qrCode")}</Button>;
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
                    <Breadcrumb.Item active>{t("breadcrumbs.accountDetails")}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
                    <Row className="justify-content-center">
                        <Col sm={6} className="form-container">
                            <Form>
                                <FlatFormGroup id="username"
                                               label="account.username"/>
                                <FlatFormGroup id="email"
                                               label="account.email"/>
                                <FlatFormGroup id="firstName"
                                               label="account.firstName"/>
                                <FlatFormGroup id="lastName"
                                               label="account.lastName"
                                               last/>
                                {renderAccessLevels()}
                                {renderAdminInfo()}
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        onClick={() => props.history.push("/")}>{t("navigation.back")}</Button>
                                <Button id="edit"
                                        onClick={() => props.history.push("/editOwnAccount")}>{t("navigation.edit")}</Button>
                                <Button id="changePassword"
                                        onClick={() => props.history.push("/changeOwnPassword")}>{t("breadcrumbs.changePassword")}</Button>
                                {adminButtons()}
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
