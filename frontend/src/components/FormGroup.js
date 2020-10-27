import React from "react";
import { FormControl, FormGroup as Group, FormLabel } from "react-bootstrap";
import {useTranslation} from "react-i18next";

export const FormGroup = props => {

    const {t} = useTranslation("validation");

    const handleChange = event => {
        const key = event.target.id;
        const value = event.target.value;
        const newValues = {...props.values};
        newValues[key] = value;
        props.setValues(newValues);
        try {
            props.schema.validateSyncAt(key, newValues);
            document.getElementById(key).classList.remove("is-invalid");
        } catch (err) {
            if (props.errors.some(e => e.path === err.path)) {
                const newErrors = props.errors.filter(e => e.path !== err.path);
                props.setErrors([...newErrors, {path: err.path, message: err.message}]);
            } else {
                props.setErrors([...props.errors, {path: err.path, message: err.message}]);
            }
            document.getElementById(key).classList.add("is-invalid");
        }
    };

    return (
        <Group>
            <FormLabel>{t("common:" + props.label)}</FormLabel>
            <FormControl id={props.id}
                         value={props.values[props.id]}
                         onChange={handleChange}/>
            <FormControl.Feedback type="invalid">{props.errors.some(e => e.path === props.id) && t(props.errors.find(e => e.path === props.id).message)}</FormControl.Feedback>
        </Group>
    );
};
