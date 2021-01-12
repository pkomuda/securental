import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import Backend from "i18next-http-backend";
import LanguageDetector from "i18next-browser-languagedetector";

export const isLanguagePolish = () => {
    return window.navigator.language === "pl";
};

i18n.use(initReactI18next)
    .use(Backend)
    .use(LanguageDetector)
    .init({
        // debug: true,
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
