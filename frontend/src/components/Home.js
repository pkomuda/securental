import React, { useState } from "react";
import QRCode from "qrcode";

export const Home = () => {

    const [otpUrl, setOtpUrl] = useState("");

    const handleChange = (event) => {
        setOtpUrl(event.target.value);
        QRCode.toCanvas(document.getElementById("canvas"), event.target.value);
    };

    return (
        <div>
            <form>
                <input value={otpUrl} onChange={handleChange}/>
            </form>
            <canvas id="canvas"/>
        </div>
    );
};
