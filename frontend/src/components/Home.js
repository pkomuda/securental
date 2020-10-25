import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import QRCode from "qrcode";

export const Home = () => {

    const {t} = useTranslation();
    const [otpUrl, setOtpUrl] = useState("");

    const handleChange = (event) => {
        setOtpUrl(event.target.value);
        QRCode.toCanvas(document.getElementById("canvas"), event.target.value);
    };

    return (
        <div>
            <span>{t("key1")}</span>
            <form>
                <input value={otpUrl} onChange={handleChange}/>
            </form>
            <canvas id="canvas"/>
        </div>
    );
};
