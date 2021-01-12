import axios from "axios";
import "bootstrap/dist/css/bootstrap.min.css";
import React from "react";
import "react-bootstrap-table-next/dist/react-bootstrap-table2.min.css";
import "react-bootstrap-table2-paginator/dist/react-bootstrap-table2-paginator.min.css";
import "react-datepicker/dist/react-datepicker.css";
import ReactDOM from "react-dom";
import { App } from "./App";
import "./styles.css";
import "./utils/i18n";

axios.defaults.baseURL = process.env.REACT_APP_API;

ReactDOM.render(
    <App/>,
    document.getElementById("root")
);
