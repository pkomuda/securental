import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useContext, useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { CURRENCY, IMAGE_BACK, IMAGE_FRONT, IMAGE_LEFT, IMAGE_RIGHT, RESERVATION_STATUS_CANCELLED, RESERVATION_STATUS_NEW, RESERVATION_STATUS_RECEPTION_ACCEPTED, RESERVATION_STATUS_RECEPTION_PENDING } from "../../utils/Constants";
import { formatDate } from "../../utils/DateTime";
import { FlatFormGroup } from "../common/FlatFormGroup";
import { Spinner } from "../common/Spinner";

export const ReservationDetails = props => {

    const {t} = useTranslation();
    const popup = withReactContent(Swal);
    const [userInfo] = useContext(AuthenticationContext);
    const [reservation, setReservation] = useState({
        number: "",
        startDate: "",
        endDate: "",
        price: "",
        status: "",
        carDto: {},
        clientDto: {}
    });
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        axios.get(`/reservation/${props.match.params.number}`)
            .then(response => {
                setReservation(response.data);
                setLoaded(true);
            }).catch(error => {
                handleError(error);
        });
    }, [props.match.params.number, t]);

    FlatFormGroup.defaultProps = {
        values: reservation
    };

    const handleChangeStatus = value => {
        Swal.fire({
            titleText: t("login.otp.code"),
            input: "password",
            preConfirm: otpCode => {
                const tempReservation = {...reservation};
                tempReservation.status = value;
                axios.put(`/reservationStatus/${reservation.number}`,
                    tempReservation,
                    {headers: {"Otp-Code": otpCode}})
                    .then(() => {
                        handleSuccess("reservation.status.change.success", "");
                        props.history.push("/listReservations");
                    }).catch(error => {
                    handleError(error);
                });
            }
        }).then(() => {});
    };

    const renderCancelButton = () => {
        if (reservation.status === RESERVATION_STATUS_NEW && reservation.clientDto.username !== userInfo.username) {
            return (
                <Button id="cancel"
                        onClick={() => handleChangeStatus(RESERVATION_STATUS_CANCELLED)}>{t("navigation.cancel")}</Button>
            );
        }
    };

    const renderAcceptButton = () => {
        if (reservation.status === RESERVATION_STATUS_RECEPTION_PENDING && reservation.clientDto.username !== userInfo.username) {
            return (
                <Button id="accept"
                        onClick={() => handleChangeStatus(RESERVATION_STATUS_RECEPTION_ACCEPTED)}>{t("navigation.accept")}</Button>
            );
        }
    };

    const renderActionButton = () => {
        const now = new Date().getTime();
        const end = new Date(reservation.endDate).getTime();
        if (reservation.status === RESERVATION_STATUS_RECEPTION_ACCEPTED && end <= now && reservation.clientDto.username !== userInfo.username) {
            return <Button id="finish"
                           onClick={() => props.history.push(`/finishReservation/${reservation.number}`)}>{t("reservation.finish")}</Button>;
        }
    };

    const findImage = (images, side) => {
        return images.filter(image => image.includes(side));
    };

    const handleImages = images => {
        popup.fire({
            html:
                <div>
                    <FormGroup>
                        <FormLabel className="font-weight-bold">{t("reservation.image.front")}</FormLabel>
                        <img id="frontImage" src={findImage(images, IMAGE_FRONT)} alt="frontAlt" style={{margin: "0 auto", maxWidth: "400px", maxHeight: "200px"}}/>
                        <hr/>
                    </FormGroup>
                    <FormGroup>
                        <FormLabel className="font-weight-bold">{t("reservation.image.back")}</FormLabel>
                        <img id="backImage" src={findImage(images, IMAGE_BACK)} alt="backAlt" style={{margin: "0 auto", maxWidth: "400px", maxHeight: "200px"}}/>
                        <hr/>
                    </FormGroup>
                    <FormGroup>
                        <FormLabel className="font-weight-bold">{t("reservation.image.right")}</FormLabel>
                        <img id="rightImage" src={findImage(images, IMAGE_RIGHT)} alt="rightAlt" style={{margin: "0 auto", maxWidth: "400px", maxHeight: "200px"}}/>
                        <hr/>
                    </FormGroup>
                    <FormGroup>
                        <FormLabel className="font-weight-bold">{t("reservation.image.left")}</FormLabel>
                        <img id="leftImage" src={findImage(images, IMAGE_LEFT)} alt="leftAlt" style={{margin: "0 auto", maxWidth: "400px", maxHeight: "200px"}}/>
                    </FormGroup>
                </div>
        }).then(() => {});
    };

    const renderImagesButtons = () => {
        const buttons = [];
        if (reservation.receivedImageUrls.length !== 0) {
            buttons.push(
                <Button id="received"
                        onClick={() => handleImages(reservation.receivedImageUrls)}>{t("reservation.images.received")}</Button>
            );
        }
        if (reservation.finishedImageUrls.length !== 0) {
            buttons.push(
                <Button id="received"
                        onClick={() => handleImages(reservation.finishedImageUrls)}>{t("reservation.images.finished")}</Button>
            );
        }
        return buttons;
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
                    <LinkContainer to="/listReservations" exact>
                        <Breadcrumb.Item>{t("breadcrumbs.listReservations")}</Breadcrumb.Item>
                    </LinkContainer>
                    <Breadcrumb.Item active>{t("breadcrumbs.reservationDetails")}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
                    <Row className="justify-content-center">
                        <Col sm={6} className="form-container">
                            <Form>
                                <FlatFormGroup id="number"
                                               label="reservation.number"/>
                                <FlatFormGroup id="clientDto.username"
                                               label="account.username"/>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.clientName")}</FormLabel>
                                    <FormControl id="clientName"
                                                 value={`${reservation.clientDto.firstName} ${reservation.clientDto.lastName}`}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
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
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.startDate")}</FormLabel>
                                    <FormControl id="car"
                                                 value={formatDate(reservation.startDate)}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.endDate")}</FormLabel>
                                    <FormControl id="car"
                                                 value={formatDate(reservation.endDate)}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.status")}</FormLabel>
                                    <FormControl id="car"
                                                 value={t(reservation.status)}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
                                <FlatFormGroup id="price"
                                               label="reservation.price"
                                               suffix={CURRENCY}
                                               last/>
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        onClick={() => props.history.push("/listReservations")}>{t("navigation.back")}</Button>
                                {renderCancelButton()}
                                {renderAcceptButton()}
                                {renderActionButton()}
                                {renderImagesButtons()}
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
