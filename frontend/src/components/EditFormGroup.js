import React from "react";
import { FormControl, FormGroup, FormLabel, InputGroup } from "react-bootstrap";
import { useTranslation } from "react-i18next";

export const EditFormGroup = props => {

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

    const renderFormControl = () => {
        return <FormControl id={props.id}
                            value={props.values[props.id]}
                            onChange={handleChange}
                            isInvalid={props.errors.hasOwnProperty(props.id)}
                            type={props.type}
                            as={props.textarea && "textarea"}
                            rows={props.textarea && 3}/>;
    };

    const renderInputGroup = () => {
        if (props.suffix) {
            return (
                <InputGroup>
                    {renderFormControl()}
                    <InputGroup.Append>
                        <InputGroup.Text>{props.suffix}</InputGroup.Text>
                    </InputGroup.Append>
                </InputGroup>
            );
        } else {
            return renderFormControl();
        }
    };

    return (
        <FormGroup>
            <FormLabel className="font-weight-bold">{t(`common:${props.label}`)} {props.required && "*"}</FormLabel>
            {renderInputGroup()}
            <FormControl.Feedback type="invalid">{props.errors.hasOwnProperty(props.id) && t(props.errors[props.id])}</FormControl.Feedback>
        </FormGroup>
    );
};
