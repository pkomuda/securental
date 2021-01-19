import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useContext, useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_CLIENT } from "../../utils/Constants";
import { FlatFormGroup } from "../FlatFormGroup";
import { Spinner } from "../Spinner";

export const OwnAccountDetails = props => {

    const {t} = useTranslation();
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
        axios.get(`/ownAccount/${userInfo.username}`, {withCredentials: true})
            .then(response => {
                setAccount(response.data);
                setLoaded(true);
            }).catch(error => {
            Swal.fire(t("errors:common.header"),
                t(`errors:${error.response.data}`),
                "error");
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
                                        onClick={() => props.history.push(`/editOwnAccount/${account.username}`)}>{t("navigation.edit")}</Button>
                                <Button id="reservations"
                                        onClick={() => props.history.push("/listOwnReservations")}>{t("account.reservations")}</Button>
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
