import React from "react";
import { FormControl, FormGroup as Group, FormLabel } from "react-bootstrap";
import { useTranslation } from "react-i18next";

export const FormGroup = props => {

    const {t} = useTranslation("validation");

    const handleChange = event => {
        const key = event.target.id;
        const value = event.target.value;
        const newErrors = {...props.errors};
        const newValues = {...props.values};
        newValues[key] = value;
        props.setValues(newValues);
        try {
            props.schema.validateSyncAt(key, newValues);
            if (props.errors.hasOwnProperty(key)) {
                delete newErrors[key];
                props.setErrors(newErrors);
            }
        } catch (err) {
            newErrors[key] = err.message;
            props.setErrors(newErrors);
        }
    };

    return (
        <Group>
            <FormLabel style={{textAlign: "left"}}>{t("common:" + props.label)} {props.required && "*"}</FormLabel>
            <FormControl id={props.id}
                         value={props.values[props.id]}
                         onChange={handleChange}
                         isInvalid={props.errors.hasOwnProperty(props.id)}
                         type={props.password && "password"}/>
            <FormControl.Feedback type="invalid">{props.errors.hasOwnProperty(props.id) && t(props.errors[props.id])}</FormControl.Feedback>
        </Group>
    );
};
