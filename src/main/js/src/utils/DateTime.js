export const formatDate = date => {
    return date.substring(0, 16).replaceAll("-", ".").replace("T", " ");
}

export const isoDate = date => {
    return `${date.toISOString().split("T")[0]}T${date.toLocaleTimeString()}`;
}

export const hoursBetween = (start, end) => {
    return Math.ceil((end.getTime() - start.getTime())/1000/60/60);
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
