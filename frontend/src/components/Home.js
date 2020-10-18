import React, { Component } from "react";
import QRCode from 'qrcode';

export default class Home extends Component {

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
        return (
            <div>
                <form>
                    <input value={this.state.otpUrl} onChange={this.handleChange}/>
                </form>
                <canvas id="canvas"/>
            </div>
        )
    }
}
