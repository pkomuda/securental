import React, { Component } from "react";
import { withTranslation } from "react-i18next";
import QRCode from 'qrcode';

class Home extends Component {

    constructor(props) {
        super(props);
        this.state = {
            otpUrl: ""
        }
    }

    handleChange = (event) => {
        this.setState({otpUrl: event.target.value});
        QRCode.toCanvas(document.getElementById("canvas"), event.target.value);
    };

    render() {
        const {t} = this.props;
        return (
            <div>
                <span>{t("key1")}</span>
                <form>
                    <input value={this.state.otpUrl} onChange={this.handleChange}/>
                </form>
                <canvas id="canvas"/>
            </div>
        )
    }
}

export default withTranslation()(Home);
