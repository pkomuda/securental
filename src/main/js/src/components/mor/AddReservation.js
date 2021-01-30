import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import en from 'date-fns/locale/en-GB';
import pl from 'date-fns/locale/pl';
import React, { useContext, useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Dropdown, DropdownButton, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import DatePicker, { registerLocale } from "react-datepicker";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import { date, mixed, object, string } from "yup";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { CURRENCY } from "../../utils/Constants";
import { hoursBetween, formatDate, isoDate, nearestFullHour } from "../../utils/DateTime";
import { isLanguagePolish } from "../../utils/i18n";
import { validate } from "../../utils/Validation";
import { EditFormGroup } from "../common/EditFormGroup";
import { Spinner } from "../common/Spinner";

export const AddReservation = props => {

    if (isLanguagePolish()) {
        registerLocale("pl", pl);
    } else {
        registerLocale("en", en);
    }

    const {t} = useTranslation();
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
        price: "0",
        clientDto: {},
        carDto: {}
    });
    const [car, setCar] = useState({
        number: "",
        make: "",
        model: "",
        price: ""
    });
    const [loaded, setLoaded] = useState(false);
    const [errors, setErrors] = useState({});
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
    }, [props.match.params.number, t]);

    const handleChangeDate = (value, property) => {
        const tempReservation = {...reservation};
        tempReservation[property] = value;

        if (tempReservation.endDate.getTime() > tempReservation.startDate.getTime()) {
            let price = hoursBetween(tempReservation.startDate, tempReservation.endDate) * parseFloat(car.price);
            tempReservation.price = price.toString();
        } else {
            tempReservation.price = "0";
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
            const tempReservation = {...reservation};
            tempReservation.startDate = isoDate(tempReservation.startDate);
            tempReservation.endDate = isoDate(tempReservation.endDate);
            tempReservation.price = tempReservation.price.replaceAll(",", ".");
            tempReservation.clientDto.username = userInfo.username;
            axios.post(`/reservation/${userInfo.username}`, tempReservation)
                .then(() => {
                    handleSuccess("reservation.add.success", "");
                    props.history.push(`/carDetails/${car.number}`);
                }).catch(error => {
                    handleError(error);
            });
        }
    };

    const renderUnavailableDates = () => {
        const dateStrings = [];
        for (let reservation of car.reservations) {
            dateStrings.push(formatDate(reservation.startDate) + " - " + formatDate(reservation.endDate));
        }

        const dates = [];
        for (let date of dateStrings.sort()) {
            dates.push(
                <Dropdown.Header>{date}</Dropdown.Header>
            );
        }
        return dates;
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
                                                    locale={isLanguagePolish() ? "pl" : "en"}
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
                                                    locale={isLanguagePolish() ? "pl" : "en"}
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
                                        onClick={() => props.history.goBack()}>{t("navigation.back")}</Button>
                                <DropdownButton id="unavailableDates"
                                                title={t("reservation.unavailableDates")}>
                                    {renderUnavailableDates()}
                                </DropdownButton>
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
