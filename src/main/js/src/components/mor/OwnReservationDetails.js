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
import { CURRENCY, IMAGE_BACK, IMAGE_FRONT, IMAGE_LEFT, IMAGE_RIGHT, RESERVATION_STATUS_CANCELLED, RESERVATION_STATUS_NEW } from "../../utils/Constants";
import { formatDate } from "../../utils/DateTime";
import { FlatFormGroup } from "../common/FlatFormGroup";
import { Spinner } from "../common/Spinner";

export const OwnReservationDetails = props => {

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
        receivedImageUrls: [],
        finishedImageUrls: [],
        clientDto: {}
    });
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        axios.get(`/reservation/${userInfo.username}/${props.match.params.number}`)
            .then(response => {
                setReservation(response.data);
                setLoaded(true);
            }).catch(error => {
                handleError(error);
        });
    }, [props.match.params.number, t, userInfo.username]);

    FlatFormGroup.defaultProps = {
        values: reservation
    };

    const handleCancel = () => {
        Swal.fire({
            titleText: t("login.otp.code"),
            input: "password",
            preConfirm: otpCode => {
                const tempReservation = {...reservation};
                tempReservation.status = RESERVATION_STATUS_CANCELLED;
                axios.put(`/reservationStatus/${userInfo.username}/${reservation.number}`,
                    tempReservation,
                    {headers: {"Otp-Code": otpCode}})
                    .then(() => {
                        handleSuccess("reservation.cancel.success", "");
                        props.history.push("/listOwnReservations");
                    }).catch(error => {
                    handleError(error);
                });
            }
        }).then(() => {});
    };

    const renderCancelButton = () => {
        if (reservation.status === RESERVATION_STATUS_NEW) {
            return (
                <Button id="cancel"
                        onClick={handleCancel}>{t("navigation.cancel")}</Button>
            );
        }
    };

    const renderActionButton = () => {
        const now = new Date().getTime();
        const start = new Date(reservation.startDate).getTime();
        const end = new Date(reservation.endDate).getTime();
        if (reservation.status === RESERVATION_STATUS_NEW && start <= now && now < end) {
            return <Button id="receive"
                           onClick={() => props.history.push(`/receiveOwnReservation/${reservation.number}`)}>{t("reservation.receive")}</Button>;
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

    const renderEditButton = () => {
        const now = new Date().getTime();
        const start = new Date(reservation.startDate).getTime();
        if (reservation.status === RESERVATION_STATUS_NEW && start <= now) {
            return (
                <Button id="edit"
                        onClick={() => props.history.push(`/editOwnReservation/${reservation.number}`)}>{t("navigation.edit")}</Button>
            );
        }
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
                    <LinkContainer to="/listOwnReservations" exact>
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
                                    <FormControl id="startDate"
                                                 value={formatDate(reservation.startDate)}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.endDate")}</FormLabel>
                                    <FormControl id="endDate"
                                                 value={formatDate(reservation.endDate)}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.status")}</FormLabel>
                                    <FormControl id="status"
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
                                {renderActionButton()}
                                {renderEditButton()}
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
