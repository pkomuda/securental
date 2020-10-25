import React from "react";
import { FormControl } from "react-bootstrap";

export const ErrorMessage = props => {

    return <FormControl.Feedback type="invalid">{props.value}</FormControl.Feedback>;
};
