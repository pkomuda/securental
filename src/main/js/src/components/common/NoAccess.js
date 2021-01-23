import React from "react";
import { Button, ButtonToolbar, Col, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";

export const NoAccess = props => {

    const {t} = useTranslation();

    return (
        <Row className="justify-content-center">
            <Col sm={6} className="form-container">
                <h1 className="text-center">{t("navigation.noAccess")}</h1>
                <ButtonToolbar className="justify-content-center">
                    <Button id="ok"
                            style={{marginTop: "1em"}}
                            onClick={() => props.history.push("/")}>{t("navigation.ok")}</Button>
                </ButtonToolbar>
            </Col>
        </Row>
    );
};
