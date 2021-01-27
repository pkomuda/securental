import Swal from "sweetalert2";
import i18n from "../utils/i18n";

const error = (title, text) => {
    Swal.fire({
        titleText: i18n.t(`errors:${title}`),
        text: i18n.t(`errors:${text}`),
        icon: "error"
    }).then(() => {});
};

export const handleError = e => {
    if (!e.response) {
        error("common.header", "common.text");
    } else {
        error("common.header", e.response.data);
    }
}

export const handleInfo = (title, text) => {
    Swal.fire({
        titleText: i18n.t(title),
        text: text,
        icon: "info"
    }).then(() => {});
};

export const handleSuccess = (title, text) => {
    Swal.fire({
        titleText: i18n.t(title),
        text: text,
        icon: "success"
    }).then(() => {});
};
