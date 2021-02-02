import Swal from "sweetalert2";
import i18n from "../utils/i18n";

const handleErrorInternal = (title, text) => {
    Swal.fire({
        titleText: i18n.t(`errors:${title}`),
        text: i18n.t(`errors:${text}`),
        icon: "error"
    }).then(() => {});
};

const handleValidationErrorInternal = (title, messages) => {
    let text = "";
    for (let message of messages) {
        text += i18n.t(`validation:${message}`) + "<br/>";
    }
    Swal.fire({
        titleText: i18n.t(`errors:${title}`),
        html: text,
        icon: "error"
    }).then(() => {});
}

export const handleError = error => {
    if (!error.response || error.response.status === 500) {
        handleErrorInternal("error.default", "error.default.text");
    } else {
        if (error.response.status === 422) {
            handleValidationErrorInternal("error.default", error.response.data);
        } else {
            handleErrorInternal("error.default", error.response.data);
        }
    }
}

export const handleSuccess = (title, text) => {
    Swal.fire({
        titleText: i18n.t(title),
        text: text,
        icon: "success"
    }).then(() => {});
};
