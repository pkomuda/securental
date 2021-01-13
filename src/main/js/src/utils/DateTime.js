export const formatDate = (date) => {
    return date.slice(0, -3).replaceAll("-", ".").replace("T", " ");
}

export const hoursBetween = (start, end) => {
    return Math.ceil((end.getTime() - start.getTime())/1000/60/60);
};

export const nearestFullHour = () => {
    let date = new Date();
    date.setHours(date.getHours() + Math.ceil(date.getMinutes()/60));
    date.setMinutes(0, 0, 0);
    return date;
};
