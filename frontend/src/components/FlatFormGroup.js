import React from "react";
import { FormControl, FormGroup, FormLabel } from "react-bootstrap";
import { useTranslation } from "react-i18next";

export const FlatFormGroup = props => {

    const {t} = useTranslation();

    const suffix = () => {
        if (props.suffix) {
            return " " + props.suffix;
        } else {
            return "";
        }
    };

    return (
        <FormGroup>
            <FormLabel className="flat-form-label">{t(`${props.label}`)}</FormLabel>
            <FormControl id={props.id}
                         value={props.values[props.id] + suffix()}
                         disabled
                         plaintext/>
        </FormGroup>
    );
};
