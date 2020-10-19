import React, {Component} from "react";
import Spinner from "react-bootstrap/Spinner";
import "./styles/Spin.css"

export default class Spin extends Component {

    render() {
        return <Spinner animation="border"/>;
    }
}
