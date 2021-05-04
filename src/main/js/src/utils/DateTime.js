import React from "react";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

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

export const prependZero = number => {
    if (number < 10) {
        return `0${number}`;
    } else {
        return `${number}`;
    }
};

const slots = () => {
    const divs = [];
    for (let i = 0; i < 24; i++) {
        divs.push(
            <div style={{marginTop: `${i*30}px`, position: "absolute"}}>{`0${i}:00`}</div>
        );
    }
    return divs;
};

export const schedule = (dates, selectedDate) => {
    const periods = [];
    for (let date of dates) {
        if (isBefore(date.startDate, selectedDate)
            && isAfter(date.endDate, selectedDate)) {
            periods.push({
                startDate: date.startDate,
                endDate: date.endDate,
                startMinute: 0,
                endMinute: 1440
            });
        } else if (isEqual(date.startDate, selectedDate)
            && isEqual(date.endDate, selectedDate)) {
            periods.push({
                startDate: date.startDate,
                endDate: date.endDate,
                startMinute: minuteOfDay(date.startDate),
                endMinute: minuteOfDay(date.endDate)
            });
        } else if (isEqual(date.startDate, selectedDate)
            && isAfter(date.endDate, selectedDate)) {
            periods.push({
                startDate: date.startDate,
                endDate: date.endDate,
                startMinute: minuteOfDay(date.startDate),
                endMinute: 1440
            });
        } else if (isBefore(date.startDate, selectedDate)
            && isEqual(date.endDate, selectedDate)) {
            periods.push({
                startDate: date.startDate,
                endDate: date.endDate,
                startMinute: 0,
                endMinute: minuteOfDay(date.endDate)
            });
        }
    }

    const blocks = [];
    for (let period of periods) {
        blocks.push(
            <OverlayTrigger placement="right"
                            delay={{show: 250, hide: 400}}
                            overlay={<Tooltip id={period.startMinute}>{`${formatDate(isoDate(period.startDate))} - ${formatDate(isoDate(period.endDate))}`}</Tooltip>}>
                <div style={{backgroundColor: "#216ba5", border: "1px solid #333a41", width: "198px", height: `${(period.endMinute - period.startMinute)/2}px`, marginTop: `${period.startMinute/2}px`, position: "absolute", borderRadius: "10px"}}/>
            </OverlayTrigger>
        );
    }

    return (
        <table>
            <td>
                <div style={{width: "50px", height: "720px", position: "relative"}}>
                    {slots()}
                </div>
            </td>
            <td>
                <div style={{backgroundColor: "lightgrey", border: "1px solid #333a41", width: "200px", height: "720px", position: "relative", borderRadius: "10px"}}>
                    {blocks}
                </div>
            </td>
        </table>
    );
};