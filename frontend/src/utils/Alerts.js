import Swal from "sweetalert2";
import i18n from "../utils/i18n";

export const error = (title, text) => {
    Swal.fire({
        titleText: i18n.t(`errors:${title}`),
        text: i18n.t(`errors:${text}`),
        icon: "error"
    }).then(() => {});
};

export const info = (title, text) => {
    Swal.fire({
        titleText: i18n.t(title),
        text: i18n.t(text),
        icon: "info"
    }).then(() => {});
};

export const success = (title, text) => {
    Swal.fire({
        titleText: i18n.t(title),
        text: i18n.t(text),
        icon: "success"
    }).then(() => {});
};
