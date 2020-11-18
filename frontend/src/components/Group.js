import React from "react";
import { FormControl, FormGroup, FormLabel } from "react-bootstrap";
import { useTranslation } from "react-i18next";

export const Group = props => {

    const {t} = useTranslation("validation");

    const handleChange = event => {
        const value = event.target.value;
        const newErrors = {...props.errors};
        const newValues = {...props.values};
        newValues[props.id] = value;
        props.setValues(newValues);
        try {
            props.schema.validateSyncAt(props.id, newValues);
            if (props.errors.hasOwnProperty(props.id)) {
                delete newErrors[props.id];
                props.setErrors(newErrors);
            }
        } catch (err) {
            newErrors[props.id] = err.message;
            props.setErrors(newErrors);
        }
    };

    return (
        <FormGroup>
            <FormLabel style={{textAlign: "left"}}>{t(`common:${props.label}`)} {props.required && "*"}</FormLabel>
            <FormControl id={props.id}
                         value={props.values[props.id]}
                         onChange={handleChange}
                         isInvalid={props.errors.hasOwnProperty(props.id)}
                         type={props.password && "password"}/>
            <FormControl.Feedback type="invalid">{props.errors.hasOwnProperty(props.id) && t(props.errors[props.id])}</FormControl.Feedback>
        </FormGroup>
    );
};
