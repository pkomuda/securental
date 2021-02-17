const months = ["January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"];

export const parseDate = text => {
    const monthDayYear = text.substring(text.indexOf(",") + 1).substring(1).split(" ");
    const month = months.indexOf(monthDayYear[0]);
    const day = parseInt(monthDayYear[1].slice(0, -2));
    const year = parseInt(monthDayYear[2]);
    return new Date(year, month, day);
};

export const isBefore = (date, selectedDate) => {
    return (date.getFullYear() < selectedDate.getFullYear())
        || (date.getFullYear() <= selectedDate.getFullYear() && date.getMonth() < selectedDate.getMonth())
        || (date.getFullYear() <= selectedDate.getFullYear() && date.getMonth() === selectedDate.getMonth() && date.getDate() < selectedDate.getDate());
};

export const isAfter = (date, selectedDate) => {
    return (date.getFullYear() > selectedDate.getFullYear())
        || (date.getFullYear() >= selectedDate.getFullYear() && date.getMonth() > selectedDate.getMonth())
        || (date.getFullYear() >= selectedDate.getFullYear() && date.getMonth() === selectedDate.getMonth() && date.getDate() > selectedDate.getDate());
};

export const isEqual = (date, selectedDate) => {
    return date.getFullYear() === selectedDate.getFullYear()
        && date.getMonth() === selectedDate.getMonth()
        && date.getDate() === selectedDate.getDate();
};

export const formatDate = date => {
    return date.substring(0, 16).replaceAll("-", ".").replace("T", " ");
}

export const isoDate = date => {
    return `${date.toISOString().split("T")[0]}T${date.toLocaleTimeString()}`;
}

export const hoursBetween = (start, end) => {
    return Math.ceil((end.getTime() - start.getTime())/1000/60/60);
};

export const minuteOfDay = date => {
    return (date.getHours() * 60) + date.getMinutes();
};

export const getTimeFormat = () => {
    return "HH:mm";
};

export const getDateFormat = () => {
    return "yyyy.MM.dd HH:mm";
};

export const nearestFullHour = () => {
    let date = new Date();
    date.setHours(date.getHours() + Math.ceil(date.getMinutes()/60));
    date.setMinutes(0, 0, 0);
    return date;
};
