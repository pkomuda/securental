import React, { useContext } from "react";
import { FormControl, FormGroup, FormLabel } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { CURRENCY } from "../../utils/Constants";
import { formatDecimal } from "../../utils/i18n";

export const FlatFormGroup = props => {

    const {t} = useTranslation();
    const [userInfo] = useContext(AuthenticationContext);

    const value = () => {
        if (props.suffix === CURRENCY) {
            return formatDecimal(property(), userInfo) + suffix();
        } else {
            return property() + suffix();
        }
    };

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
                         value={value()}
                         disabled
                         plaintext/>
            {!props.last && <hr/>}
        </FormGroup>
    );
};
