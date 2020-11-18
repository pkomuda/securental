import React, { useEffect, useState } from "react";
import axios from "axios";
import Swal from "sweetalert2";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";

export const AccountDetails = props => {

    const {t} = useTranslation();
    const [account, setAccount] = useState({
        username: "",
        email: "",
        firstName: "",
        lastName: ""
    });

    useEffect(() => {
        axios.get(`/account/${props.match.params.username}`)
            .then(response => {
                setAccount(response.data);
            }).catch(error => {
            Swal.fire(t("errors:common.header"),
                t(`errors:${error.response.data}`),
                "error");
        });
    }, [props.match.params.username, t]);

    return (
        <React.Fragment>
            <Breadcrumb>
                <LinkContainer to="/" exact>
                    <Breadcrumb.Item>Home</Breadcrumb.Item>
                </LinkContainer>
                <LinkContainer to="/listAccounts" exact>
                    <Breadcrumb.Item>Account list</Breadcrumb.Item>
                </LinkContainer>
                <Breadcrumb.Item active>{`${account.username}'s account details`}</Breadcrumb.Item>
            </Breadcrumb>
            <Container>
                <Row className="justify-content-center">
                    <Col sm={5} className="form-container">
                        <Form>
                            <FormGroup>
                                <FormLabel>{t("account.username")}</FormLabel>
                                <FormControl id="username"
                                             value={account.username}
                                             disabled/>
                            </FormGroup>
                            <FormGroup>
                                <FormLabel>{t("account.email")}</FormLabel>
                                <FormControl id="email"
                                             value={account.email}
                                             disabled/>
                            </FormGroup>
                            <FormGroup>
                                <FormLabel>{t("account.firstName")}</FormLabel>
                                <FormControl id="firstName"
                                             value={account.firstName}
                                             disabled/>
                            </FormGroup>
                            <FormGroup>
                                <FormLabel>{t("account.lastName")}</FormLabel>
                                <FormControl id="lastName"
                                             value={account.lastName}
                                             disabled/>
                            </FormGroup>
                        </Form>
                        <ButtonToolbar className="justify-content-center">
                            <Button id="back"
                                    variant="dark"
                                    className="button"
                                    onClick={() => props.history.goBack}>{t("navigation.back")}</Button>
                            <Button id="edit"
                                    variant="dark"
                                    className="button"
                                    onClick={() => props.history.push(`/editAccount/${account.username}`)}>{t("navigation.edit")}</Button>
                        </ButtonToolbar>
                    </Col>
                </Row>
            </Container>
        </React.Fragment>
    );
};
