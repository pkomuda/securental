import React from "react";
import { useTranslation } from "react-i18next";

export const Home = () => {

    const {t} = useTranslation();

    return (
        <React.Fragment>
            <h1>{t("breadcrumbs.home")}</h1>
        </React.Fragment>
    );
};
