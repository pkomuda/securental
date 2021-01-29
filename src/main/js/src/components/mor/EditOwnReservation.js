import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import en from 'date-fns/locale/en-GB';
import pl from 'date-fns/locale/pl';
import React, { useContext, useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import DatePicker, { registerLocale } from "react-datepicker";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import { date, mixed, object, string } from "yup";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { CURRENCY } from "../../utils/Constants";
import { getDateFormat, getTimeFormat, hoursBetween, isoDate, nearestFullHour } from "../../utils/DateTime";
import { getLocale, isLanguagePolish } from "../../utils/i18n";
import { validate } from "../../utils/Validation";
import { FlatFormGroup } from "../common/FlatFormGroup";
import { Spinner } from "../common/Spinner";

export const EditOwnReservation = props => {

    if (isLanguagePolish()) {
        registerLocale("pl", pl);
    } else {
        registerLocale("en", en);
    }

    const {t} = useTranslation();
    const [userInfo] = useContext(AuthenticationContext);
    const schema = object().shape({
        number: string(),
        startDate: date().required("reservation.startDate.required"),
        endDate: date().required("reservation.endDate.required"),
        price: string(),
        status: string(),
        clientDto: mixed(),
        carDto: mixed(),
        signature: string()
    });
    const [reservation, setReservation] = useState({
        startDate: nearestFullHour(),
        endDate: nearestFullHour(),
    });
    const [loaded, setLoaded] = useState(false);
    const [errors, setErrors] = useState({});
    FlatFormGroup.defaultProps = {
        values: reservation
    };

    useEffect(() => {
        axios.get(`/reservation/${userInfo.username}/${props.match.params.number}`)
            .then(response => {
                const tempReservation = response.data;
                tempReservation.startDate = new Date(tempReservation.startDate);
                tempReservation.endDate = new Date(tempReservation.endDate);
                setReservation(tempReservation);
                setLoaded(true);
            }).catch(error => {
                handleError(error);
        });
    }, [props.match.params.number, t, userInfo.username]);

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
            console.log(tempReservation);
            axios.put(`/reservation/${userInfo.username}/${tempReservation.number}`, tempReservation)
                .then(() => {
                    handleSuccess("reservation.edit.success", "");
                    props.history.push(`/ownReservationDetails/${tempReservation.number}`);
                }).catch(error => {
                    handleError(error);
            });
        }
    };

    const handleChangeDate = (value, property) => {
        const tempReservation = {...reservation};
        tempReservation[property] = value;

        if (tempReservation.endDate.getTime() > tempReservation.startDate.getTime()) {
            let price = hoursBetween(tempReservation.startDate, tempReservation.endDate) * parseFloat(reservation.carDto.price);
            tempReservation.price = price.toString();
        } else {
            tempReservation.price = "0";
        }

        setReservation(tempReservation);
    }

    if (loaded) {
        return (
            <React.Fragment>
                <Breadcrumb>
                    <LinkContainer to="/" exact>
                        <Breadcrumb.Item>
                            <FontAwesomeIcon icon={faHome}/>
                        </Breadcrumb.Item>
                    </LinkContainer>
                    <LinkContainer to={"/listOwnReservations"} exact>
                        <Breadcrumb.Item>{t("breadcrumbs.listReservations")}</Breadcrumb.Item>
                    </LinkContainer>
                    <LinkContainer to={`/ownReservationDetails/${reservation.number}`} exact>
                        <Breadcrumb.Item>{t("breadcrumbs.reservationDetails")}</Breadcrumb.Item>
                    </LinkContainer>
                    <Breadcrumb.Item active>{t("breadcrumbs.editReservation")}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
                    <Row className="justify-content-center">
                        <Col sm={6} className="form-container">
                            <Form>
                                <FlatFormGroup id="number"
                                               label="reservation.number"/>
                                <FlatFormGroup id="carDto.number"
                                               label="car.number"/>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.car")}</FormLabel>
                                    <FormControl id="car"
                                                 value={`${reservation.carDto.make} ${reservation.carDto.model}`}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
                                <FlatFormGroup id="price"
                                               label="reservation.price"
                                               suffix={CURRENCY}/>
                                <FormGroup>
                                    <FormLabel className="font-weight-bold">{t("reservation.startDate")}</FormLabel>
                                    <div>
                                        <DatePicker selected={reservation.startDate}
                                                    onChange={date => handleChangeDate(date, "startDate")}
                                                    timeCaption={t("reservation.time")}
                                                    locale={getLocale()}
                                                    dateFormat={getDateFormat()}
                                                    timeFormat={getTimeFormat()}
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
                                                    timeCaption={t("reservation.time")}
                                                    locale={getLocale()}
                                                    dateFormat={getDateFormat()}
                                                    timeFormat={getTimeFormat()}
                                                    timeIntervals={60}
                                                    showTimeSelect/>
                                        <p id="dateBeforeNowFeedback" className="invalid" style={{display: "none"}}>{t("validation:reservation.date.before.now")}</p>
                                        <p id="startNotBeforeEndFeedback" className="invalid" style={{display: "none"}}>{t("validation:reservation.date.start.not.before.end")}</p>
                                    </div>
                                </FormGroup>
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        onClick={() => props.history.push(`/ownReservationDetails/${reservation.number}`)}>{t("navigation.back")}</Button>
                                <Button id="edit"
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
