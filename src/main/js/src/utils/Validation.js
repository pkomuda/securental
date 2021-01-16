export const STRING_REGEX = /^[a-zA-Z0-9!@#$%^ąćęłńóśźżĄĆĘŁŃÓŚŹŻ,. ]+$/;

export const EMAIL_REGEX = /^[a-zA-Z0-9-_]+(\.[a-zA-Z0-9-_]+)*@[a-zA-Z0-9-]+(\.[a-zA-Z0-9]+)*(\.[a-zA-Z]{2,})$/;

export const YEAR_REGEX = /^[12][0-9]{3}$/;

export const MONEY_REGEX = /^(?=.*[1-9])[0-9]*[.,]?[0-9]{1,2}$/;

export const validate = (values, errors, setErrors, schema) => {
    const newErrors = {};
    for (const key of Object.keys(values)) {
        try {
            schema.validateSyncAt(key, values);
        }
        catch (err) {
            newErrors[key] = err.message;
        }
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
};
