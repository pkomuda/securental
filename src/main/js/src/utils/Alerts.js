import Swal from "sweetalert2";
import i18n from "../utils/i18n";

const handleErrorInternal = (title, text) => {
    Swal.fire({
        titleText: i18n.t(`errors:${title}`),
        text: i18n.t(`errors:${text}`),
        icon: "error"
    }).then(() => {});
};

export const handleError = error => {
    if (!error.response) {
        handleErrorInternal("common.header", "common.text");
    } else {
        handleErrorInternal("common.header", error.response.data);
    }
}

export const handleSuccess = (title, text) => {
    Swal.fire({
        titleText: i18n.t(title),
        text: text,
        icon: "success"
    }).then(() => {});
};
