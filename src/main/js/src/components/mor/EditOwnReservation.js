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
import { bool, object, string } from "yup";
import { hoursBetween } from "../../utils/DateTime";
import { isLanguagePolish } from "../../utils/i18n";
import { MONEY_REGEX, STRING_REGEX, validate, YEAR_REGEX } from "../../utils/Validation";
import { EditFormGroup } from "../EditFormGroup";
import { FlatFormGroup } from "../FlatFormGroup";
import { Spinner } from "../Spinner";

export const EditOwnReservation = props => {

    if (isLanguagePolish()) {
        registerLocale("pl", pl);
    } else {
        registerLocale("en", en);
    }

    const {t} = useTranslation();
    // const [userInfo] = useContext(AuthenticationContext);
    const schema = object().shape({
        number: string(),
        make: string().required("car.make.required").min(1, "car.make.min").max(32, "car.make.max").matches(STRING_REGEX, "car.make.invalid"),
        model: string().required("car.model.required").min(1, "car.model.min").max(32, "car.model.max").matches(STRING_REGEX, "car.model.invalid"),
        description: string().required("car.description.required").min(1, "car.description.min").max(255, "car.description.max").matches(STRING_REGEX, "car.description.invalid"),
        productionYear: string().required("car.productionYear.required").matches(YEAR_REGEX, "car.productionYear.invalid"),
        price: string().required("car.price.required").matches(MONEY_REGEX, "car.price.invalid"),
        active: bool()
    });
    const [reservation, setReservation] = useState({
        make: "",
        model: "",
        description: "",
        productionYear: "",
        price: "",
        active: false
    });
    const [loaded, setLoaded] = useState(false);
    const [errors, setErrors] = useState({});
    FlatFormGroup.defaultProps = {
        values: reservation
    };
    EditFormGroup.defaultProps = {
        schema: schema,
        values: reservation,
        errors: errors,
        setValues: newReservation => setReservation(newReservation),
        setErrors: newErrors => setErrors(newErrors)
    };

    useEffect(() => {
        axios.get(`/reservation/herbson/${props.match.params.number}`)
            .then(response => {
                const tempReservation = response.data;
                console.log(tempReservation);
                tempReservation.startDate = new Date(tempReservation.startDate);
                tempReservation.endDate = new Date(tempReservation.endDate);
                setReservation(tempReservation);
                setLoaded(true);
            }).catch(error => {
            Swal.fire(t("errors:common.header"),
                t(`errors:${error.response.data}`),
                "error");
        });
    }, [props.match.params.number, t]);

    const handleSubmit = () => {
        if (validate(reservation, errors, setErrors, schema)) {
            const tempReservation = {...reservation};
            tempReservation.productionYear = parseInt(tempReservation.productionYear, 10);
            tempReservation.price = tempReservation.price.replaceAll(",", ".");
            console.log(tempReservation);
            axios.put(`/reservation/herbson/${tempReservation.number}`, tempReservation, {withCredentials: true})
                .then(() => {
                    Swal.fire(t("errors:common.header"),
                        t("errors:common.text"),
                        "success");
                    props.history.push(`/ownReservationDetails/${tempReservation.number}`);
                }).catch(() => {
                Swal.fire(t("errors:common.header"),
                    t("errors:common.text"),
                    "error");
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

        setReservation(tempReservation)
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
                    <LinkContainer to="/listCars" exact>
                        <Breadcrumb.Item>{t("breadcrumbs.listReservations")}</Breadcrumb.Item>
                    </LinkContainer>
                    <LinkContainer to={`/carDetails/${reservation.number}`} exact>
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
                                               suffix="PLN"/>
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
                                    </div>
                                </FormGroup>
                                {/*FLAT*/}
                                {/*<FormGroup>*/}
                                {/*    <FormLabel className="flat-form-label">{t("reservation.startDate")}</FormLabel>*/}
                                {/*    <FormControl id="car"*/}
                                {/*                 value={formatDate(reservation.startDate)}*/}
                                {/*                 disabled*/}
                                {/*                 plaintext/>*/}
                                {/*    <hr/>*/}
                                {/*</FormGroup>*/}
                                {/*<FormGroup>*/}
                                {/*    <FormLabel className="flat-form-label">{t("reservation.endDate")}</FormLabel>*/}
                                {/*    <FormControl id="car"*/}
                                {/*                 value={formatDate(reservation.endDate)}*/}
                                {/*                 disabled*/}
                                {/*                 plaintext/>*/}
                                {/*    <hr/>*/}
                                {/*</FormGroup>*/}
                                {/*FLAT*/}
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
