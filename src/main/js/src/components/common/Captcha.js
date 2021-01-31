import React from "react";
import ReCAPTCHA from "react-google-recaptcha";
import { useTranslation } from "react-i18next";
import { CAPTCHA_SITE_KEY } from "../../utils/Constants";

export const Captcha = props => {

    const {t} = useTranslation();

    return (
        <div className="text-center">
            <ReCAPTCHA sitekey={CAPTCHA_SITE_KEY}
                       onChange={props.onChange}
                       className="captcha"/>
            <p id="captchaFeedback" className="invalid text-center" style={{display: "none"}}>{t("validation:captcha.required")}</p>
        </div>
    );
};
