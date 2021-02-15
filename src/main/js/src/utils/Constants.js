export const ACCESS_LEVEL_ADMIN = "ADMIN";
export const ACCESS_LEVEL_EMPLOYEE = "EMPLOYEE";
export const ACCESS_LEVEL_CLIENT = "CLIENT";

export const CAR_CATEGORY_SEDAN = "SEDAN";
export const CAR_CATEGORY_KOMBI = "KOMBI";
export const CAR_CATEGORY_HATCHBACK = "HATCHBACK";
export const CAR_CATEGORY_COUPE = "COUPE";
export const CAR_CATEGORY_CABRIO = "CABRIO";
export const CAR_CATEGORY_SUV = "SUV";
export const CAR_CATEGORIES = [CAR_CATEGORY_SEDAN, CAR_CATEGORY_KOMBI, CAR_CATEGORY_HATCHBACK, CAR_CATEGORY_COUPE, CAR_CATEGORY_CABRIO, CAR_CATEGORY_SUV];

export const RESERVATION_STATUS_NEW = "NEW";
export const RESERVATION_STATUS_CANCELLED = "CANCELLED";
export const RESERVATION_STATUS_RECEIVED = "RECEIVED";
export const RESERVATION_STATUS_FINISHED = "FINISHED";

export const IMAGE_FRONT = "front";
export const IMAGE_RIGHT = "right";
export const IMAGE_BACK = "back";
export const IMAGE_LEFT = "left";

export const ACTION_RECEIVE = "receive";
export const ACTION_FINISH = "finish";

export const CAPTCHA_SITE_KEY = process.env.REACT_APP_CAPTCHA_SITE_KEY;
export const CURRENCY = "PLN";

export const MAX_FILE_SIZE = 10485760;

export const PAGINATION_SIZES = [{
    text: "10", value: 10
}, {
    text: "25", value: 25
}, {
    text: "50", value: 50
}, {
    text: "100", value: 100
}];
