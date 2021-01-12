import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import en from 'date-fns/locale/en-GB';
import pl from 'date-fns/locale/pl';
import React, { useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import DatePicker, { registerLocale } from "react-datepicker";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import { date, object } from "yup";
import { isLanguagePolish } from "../../utils/i18n";
import { hoursBetween, nearestFullHour } from "../../utils/Time";
import { validate } from "../../utils/Validation";
import { EditFormGroup } from "../EditFormGroup";
import { Spinner } from "../Spinner";

export const AddReservation = props => {

    if (isLanguagePolish()) {
        registerLocale("pl", pl);
    } else {
        registerLocale("en", en);
    }

    const {t} = useTranslation();
    const schema = object().shape({
        startDate: date().required("reservation.startDate.required"),
        endDate: date().required("reservation.endDate.required")
    });
    const [reservation, setReservation] = useState({
        startDate: nearestFullHour(),
        endDate: nearestFullHour(),
        price: "0"
    });
    const [car, setCar] = useState({
        make: "",
        model: "",
        description: "",
        productionYear: "",
        price: "",
        active: false,
        reservations: []
    })
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
            Swal.fire(t("errors:common.header"),
                t(`errors:${error.response.data}`),
                "error");
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

        setReservation(tempReservation)
    }

    const handleSubmit = () => {
        console.log(reservation);
        if (validate(reservation, errors, setErrors, schema)) {
            // console.log(reservation);
            // axios.post(`/reservation`, reservation, {withCredentials: true})
            //     .then(() => {
            //         const alerts = [];
            //         alerts.push({
            //             title: t("register.password.header"),
            //             html: t("register.password.text1"),
            //             icon: "success"
            //         });
            //         Swal.queue(alerts);
            //         props.history.push(`/carDetails/${tempCar.number}`);
            //     }).catch(() => {
            //     Swal.fire(t("errors:common.header"),
            //         t("errors:common.text"),
            //         "error");
            // });
        }
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
                        <Col sm={5} className="form-container">
                            <Form>
                                <FormGroup>
                                    <FormLabel className="font-weight-bold">{t("reservation.car")}</FormLabel>
                                    <FormControl id="car"
                                                 value={`${car.make} ${car.model}`}
                                                 disabled
                                                 plaintext/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="font-weight-bold">{t("car.price")}</FormLabel>
                                    <FormControl id="price"
                                                 value={`${reservation.price} PLN`}
                                                 disabled
                                                 plaintext/>
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
                                    </div>
                                </FormGroup>
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        onClick={() => props.history.goBack()}>{t("navigation.back")}</Button>
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
