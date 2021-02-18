import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import en from 'date-fns/locale/en-GB';
import pl from 'date-fns/locale/pl';
import React, { useContext, useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, OverlayTrigger, Row, Tooltip } from "react-bootstrap";
import DatePicker, { registerLocale } from "react-datepicker";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { date, mixed, object, string } from "yup";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { CURRENCY } from "../../utils/Constants";
import { formatDate, hoursBetween, isAfter, isBefore, isEqual, isoDate, minuteOfDay, nearestFullHour, parseDate } from "../../utils/DateTime";
import { addDecimalPlaces, isLanguagePolish } from "../../utils/i18n";
import { validate } from "../../utils/Validation";
import { EditFormGroup } from "../common/EditFormGroup";
import { Spinner } from "../common/Spinner";

export const AddReservation = props => {

    const {t} = useTranslation();
    const popup = withReactContent(Swal);
    const [userInfo] = useContext(AuthenticationContext);
    const schema = object().shape({
        startDate: date().required("reservation.startDate.required"),
        endDate: date().required("reservation.endDate.required"),
        price: string(),
        clientDto: mixed(),
        carDto: mixed()
    });
    const [reservation, setReservation] = useState({
        startDate: nearestFullHour(),
        endDate: nearestFullHour(),
        price: addDecimalPlaces(0, userInfo),
        clientDto: {},
        carDto: {}
    });
    const [car, setCar] = useState({
        number: "",
        make: "",
        model: "",
        price: ""
    });
    const [unavailableDates, setUnavailableDates] = useState([]);
    const [loaded, setLoaded] = useState(false);
    const [errors, setErrors] = useState({});
    if (isLanguagePolish(userInfo)) {
        registerLocale("pl", pl);
    } else {
        registerLocale("en", en);
    }
    EditFormGroup.defaultProps = {
        schema: schema,
        values: reservation,
        errors: errors,
        setValues: newReservation => setReservation(newReservation),
        setErrors: newErrors => setErrors(newErrors)
    };

    useEffect(() => {
        axios.get(`/car/${props.match.params.number}`)
            .then(response => {
                console.log(response.data);
                setCar(response.data);
                setLoaded(true);
            }).catch(error => {
                handleError(error);
        });
    }, [props.match.params.number]);
    
    useEffect(() => {
        if (loaded) {
            const temp = [];
            for (let reservation of car.reservations) {
                temp.push({
                    startDate: new Date(reservation.startDate),
                    endDate: new Date(reservation.endDate)
                });
            }
            setUnavailableDates(temp);
        }
    }, [car.reservations, loaded]);
    
    const handleChangeDate = (value, property) => {
        const tempReservation = {...reservation};
        tempReservation[property] = value;

        if (tempReservation.endDate.getTime() > tempReservation.startDate.getTime()) {
            let price = hoursBetween(tempReservation.startDate, tempReservation.endDate) * parseFloat(car.price);
            tempReservation.price = addDecimalPlaces(price, userInfo);
        } else {
            tempReservation.price = addDecimalPlaces(0, userInfo);
        }

        setReservation(tempReservation);
    };

    const validateDates = object => {
        const now = new Date();
        if (object.startDate.getTime() < now.getTime()
            || object.endDate.getTime() < now.getTime()) {
            document.getElementById("dateBeforeNowFeedback").style.display = "block";
            document.getElementById("startNotBeforeEndFeedback").style.display = "none";
            return false;
        } else {
            document.getElementById("dateBeforeNowFeedback").style.display = "none";
            if (object.startDate.getTime() < object.endDate.getTime()) {
                document.getElementById("startNotBeforeEndFeedback").style.display = "none";
                return true;
            } else {
                document.getElementById("startNotBeforeEndFeedback").style.display = "block";
                return false;
            }
        }
    };

    const handleSubmit = () => {
        if (!!(validate(reservation, errors, setErrors, schema) & validateDates(reservation))) {
            Swal.fire({
                titleText: t("login.otp.code"),
                input: "password",
                preConfirm: otpCode => {
                    const tempReservation = {...reservation};
                    tempReservation.startDate = isoDate(tempReservation.startDate);
                    tempReservation.endDate = isoDate(tempReservation.endDate);
                    tempReservation.price = tempReservation.price.replaceAll(",", ".");
                    tempReservation.clientDto.username = userInfo.username;
                    tempReservation.carDto.number = car.number;
                    tempReservation.carDto.signature = car.signature;
                    axios.post(`/reservation/${userInfo.username}`,
                        tempReservation,
                        {headers: {"Otp-Code": otpCode}})
                        .then(() => {
                            handleSuccess("reservation.add.success", "");
                            props.history.push(`/carDetails/${car.number}`);
                        }).catch(error => {
                            handleError(error);
                    });
                }
            }).then(() => {});
        }
    };

    const prependZero = number => {
        if (number < 10) {
            return `0${number}`;
        } else {
            return `${number}`;
        }
    };

    const handleContextMenu = event => {
        event.preventDefault();
        popup.fire({
            html:
                <div>
                    {schedule(unavailableDates, parseDate(event.target.getAttribute("aria-label")))}
                </div>
        }).then(() => {});
    };

    const addEventListeners = () => {
        for (let i = 1; i <= 31; i++) {
            const elements = document.getElementsByClassName(`react-datepicker__day--0${prependZero(i)}`);
            if (elements.length === 1) {
                let element = elements[0];
                element.addEventListener("contextmenu", handleContextMenu);
                element.style["user-select"] = "none";
            }
        }
    };

    const schedule = (dates, selectedDate) => {
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
                        <div style={{position: "absolute"}}>00:00</div>
                        <div style={{marginTop: "30px", position: "absolute"}}>01:00</div>
                        <div style={{marginTop: "60px", position: "absolute"}}>02:00</div>
                        <div style={{marginTop: "90px", position: "absolute"}}>03:00</div>
                        <div style={{marginTop: "120px", position: "absolute"}}>04:00</div>
                        <div style={{marginTop: "150px", position: "absolute"}}>05:00</div>
                        <div style={{marginTop: "180px", position: "absolute"}}>06:00</div>
                        <div style={{marginTop: "210px", position: "absolute"}}>07:00</div>
                        <div style={{marginTop: "240px", position: "absolute"}}>08:00</div>
                        <div style={{marginTop: "270px", position: "absolute"}}>09:00</div>
                        <div style={{marginTop: "300px", position: "absolute"}}>10:00</div>
                        <div style={{marginTop: "330px", position: "absolute"}}>11:00</div>
                        <div style={{marginTop: "360px", position: "absolute"}}>12:00</div>
                        <div style={{marginTop: "390px", position: "absolute"}}>13:00</div>
                        <div style={{marginTop: "420px", position: "absolute"}}>14:00</div>
                        <div style={{marginTop: "450px", position: "absolute"}}>15:00</div>
                        <div style={{marginTop: "480px", position: "absolute"}}>16:00</div>
                        <div style={{marginTop: "510px", position: "absolute"}}>17:00</div>
                        <div style={{marginTop: "540px", position: "absolute"}}>18:00</div>
                        <div style={{marginTop: "570px", position: "absolute"}}>19:00</div>
                        <div style={{marginTop: "600px", position: "absolute"}}>20:00</div>
                        <div style={{marginTop: "630px", position: "absolute"}}>21:00</div>
                        <div style={{marginTop: "660px", position: "absolute"}}>22:00</div>
                        <div style={{marginTop: "690px", position: "absolute"}}>23:00</div>
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

    if (loaded) {
        return (
            <React.Fragment>
                <Breadcrumb>
                    <LinkContainer to="/" exact>
                        <Breadcrumb.Item>
                            <FontAwesomeIcon icon={faHome}/>
                        </Breadcrumb.Item>
                    </LinkContainer>
                    <LinkContainer to="/listCars" exact>
                        <Breadcrumb.Item>{t("breadcrumbs.listCars")}</Breadcrumb.Item>
                    </LinkContainer>
                    <LinkContainer to={`/carDetails/${car.number}`} exact>
                        <Breadcrumb.Item>{t("breadcrumbs.carDetails")}</Breadcrumb.Item>
                    </LinkContainer>
                    <Breadcrumb.Item active>{t("breadcrumbs.addReservation")}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
                    <Row className="justify-content-center">
                        <Col sm={6} className="form-container">
                            <Form>
                                <FormGroup>
                                    <FormLabel className="font-weight-bold">{t("reservation.car")}</FormLabel>
                                    <FormControl id="car"
                                                 value={`${car.make} ${car.model}`}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="font-weight-bold">{t("reservation.price")}</FormLabel>
                                    <FormControl id="price"
                                                 value={`${reservation.price} ${CURRENCY}`}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="font-weight-bold">{t("reservation.startDate")}</FormLabel>
                                    <div>
                                        <DatePicker selected={reservation.startDate}
                                                    onChange={date => handleChangeDate(date, "startDate")}
                                                    onCalendarOpen={addEventListeners}
                                                    locale={isLanguagePolish(userInfo) ? "pl" : "en"}
                                                    timeCaption={t("reservation.time")}
                                                    timeFormat="HH:mm"
                                                    dateFormat="yyyy.MM.dd HH:mm"
                                                    timeIntervals={60}
                                                    showTimeSelect/>
                                    </div>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="font-weight-bold">{t("reservation.endDate")}</FormLabel>
                                    <div>
                                        <DatePicker selected={reservation.endDate}
                                                    onChange={date => handleChangeDate(date, "endDate")}
                                                    onCalendarOpen={addEventListeners}
                                                    locale={isLanguagePolish(userInfo) ? "pl" : "en"}
                                                    timeCaption={t("reservation.time")}
                                                    timeFormat="HH:mm"
                                                    dateFormat="yyyy.MM.dd HH:mm"
                                                    timeIntervals={60}
                                                    showTimeSelect/>
                                        <p id="dateBeforeNowFeedback" className="invalid" style={{display: "none"}}>{t("validation:reservation.date.before.now")}</p>
                                        <p id="startNotBeforeEndFeedback" className="invalid" style={{display: "none"}}>{t("validation:reservation.date.start.not.before.end")}</p>
                                    </div>
                                </FormGroup>
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        onClick={() => props.history.push(`/carDetails/${car.number}`)}>{t("navigation.back")}</Button>
                                <Button id="submit"
                                        onClick={handleSubmit}>{t("navigation.submit")}</Button>
                            </ButtonToolbar>
                        </Col>
                    </Row>
                </Container>
            </React.Fragment>
        );
    } else {
        return <Spinner/>;
    }
};
