import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import { FlatFormGroup } from "../common/FlatFormGroup";
import { Spinner } from "../common/Spinner";

export const AccountDetails = props => {

    const {t} = useTranslation();
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
        axios.get(`/account/${props.match.params.username}`)
            .then(response => {
                setAccount(response.data);
                setLoaded(true);
            }).catch(error => {
            Swal.fire(t("errors:common.header"),
                t(`errors:${error.response.data}`),
                "error");
        });
    }, [props.match.params.username, t]);

    FlatFormGroup.defaultProps = {
        values: account
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
                                               label="account.lastName"/>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("account.accessLevels")}</FormLabel>
                                    <FormControl id="accessLevels"
                                                 value={account.accessLevels.map(a => t(a)).join(", ")}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
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
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        onClick={() => props.history.push("/listAccounts")}>{t("navigation.back")}</Button>
                                <Button id="edit"
                                        onClick={() => props.history.push(`/editAccount/${account.username}`)}>{t("navigation.edit")}</Button>
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
