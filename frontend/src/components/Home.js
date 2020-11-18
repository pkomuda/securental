import React from "react";
import { Breadcrumb } from "react-bootstrap";
import { LinkContainer } from "react-router-bootstrap";
import { useTranslation } from "react-i18next";

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
            <p>{t("breadcrumbs.accountDetails")}</p>
        </React.Fragment>
    );
};
