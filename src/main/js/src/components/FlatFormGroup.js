import React from "react";
import { FormControl, FormGroup, FormLabel } from "react-bootstrap";
import { useTranslation } from "react-i18next";

export const FlatFormGroup = props => {

    const {t} = useTranslation();

    const property = () => {
        if (props.id.includes(".")) {
            return props.values[props.id.split(".")[0]][props.id.split(".")[1]]
        } else {
            return props.values[props.id];
        }
    };

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
                         value={property() + suffix()}
                         disabled
                         plaintext/>
            {!props.last && <hr/>}
        </FormGroup>
    );
};
