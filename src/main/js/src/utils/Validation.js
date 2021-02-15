export const EMAIL_REGEX = /^[a-zA-Z0-9-_]+(\.[a-zA-Z0-9-_]+)*@[a-zA-Z0-9-]+(\.[a-zA-Z0-9]+)*(\.[a-zA-Z]{2,})$/;
export const MONEY_REGEX = /^(?=.*[1-9])[0-9]*[.,]?[0-9]{1,2}$/;
export const NAME_REGEX = /^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+$/;
export const PASSWORD_REGEX = /^((?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%])(?=.*[A-Z]).{8,64})$/;
export const STRING_REGEX = /^[a-zA-Z0-9!@#$%ąćęłńóśźżĄĆĘŁŃÓŚŹŻ,. ]+$/;
export const YEAR_REGEX = /^(19|20)[0-9]{2}$/;

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
    // console.log(newErrors);
    return Object.keys(newErrors).length === 0;
};
