import React from "react";
import ReactDOM from "react-dom";
import axios from "axios";
import App from "./App";
import "./index.css";
import "bootstrap/dist/css/bootstrap.min.css";

axios.defaults.baseURL = process.env.REACT_APP_API;

ReactDOM.render(
    <App/>,
    document.getElementById("root")
);
