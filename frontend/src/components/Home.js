import React from "react";
import {
    Breadcrumb,
    Button,
    ButtonGroup,
    ButtonToolbar,
    Col, Container,
    Form,
    FormCheck,
    FormControl,
    FormGroup,
    FormLabel,
    Row
} from "react-bootstrap";
import { LinkContainer } from "react-router-bootstrap";
import { useTranslation } from "react-i18next";
import { Group } from "./Group";
import { ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_CLIENT, ACCESS_LEVEL_EMPLOYEE } from "../utils/Constants";

export const Home = () => {

    const {t} = useTranslation();

    const username = "john";

    const activeBreadcrumb = () => {
        switch (window.navigator.language) {
            case "pl":
                return `${t("breadcrumbs.accountDetails")} ${username}`;
            default:
                return `${username}'s ${t("breadcrumbs.accountDetails")}`;
        }
    };

    return (
        <React.Fragment>
            <Breadcrumb>
                <LinkContainer to="/" exact>
                    <Breadcrumb.Item>Home</Breadcrumb.Item>
                </LinkContainer>
                <Breadcrumb.Item active>{activeBreadcrumb()}</Breadcrumb.Item>
            </Breadcrumb>
            <Row>
            <Col sm={5} className="form-container">
                <Form>
                    <FormGroup>
                        <FormLabel>{t("account.activity")}</FormLabel>
                        <FormControl plaintext readOnly value={username}/>
                    </FormGroup>
                </Form>
            </Col>
            </Row>
        </React.Fragment>
    );
};
