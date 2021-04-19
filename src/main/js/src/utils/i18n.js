import i18n from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import Backend from "i18next-http-backend";
import { initReactI18next } from "react-i18next";
import { isAuthenticated } from "./AuthenticationContext";

export const isLanguagePolish = userInfo => {
    if (isAuthenticated(userInfo)) {
        return userInfo.preferredLanguage === "pl";
    } else {
        return window.navigator.language === "pl";
    }
};

export const getLocale = userInfo => {
    return isLanguagePolish(userInfo) ? "pl" : "en";
};

export const formatDecimal = (value, userInfo) => {
    if (isLanguagePolish(userInfo)) {
        return value.replace(".", ",");
    } else {
        return value;
    }
};

export const addDecimalPlaces = (value, userInfo) => {
    if (isLanguagePolish(userInfo)) {
        return value.toFixed(2).toString().replace(".", ",");
    } else {
        return value.toFixed(2).toString();
    }
};

i18n.use(initReactI18next)
    .use(Backend)
    .use(LanguageDetector)
    .init({
        backend: {
            loadPath: "/locales/{{lng}}/{{ns}}.json"
        },
        defaultNS: "common",
        detection: {
            order: ["navigator"]
        },
        fallbackLng: "en",
        keySeparator: false,
        ns: ["common", "errors", "validation"]
    }).then(() => {});

export default i18n;
