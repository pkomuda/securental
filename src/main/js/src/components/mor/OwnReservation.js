import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useContext, useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { ACTION_FINISH, ACTION_RECEIVE, IMAGE_BACK, IMAGE_FRONT, IMAGE_LEFT, IMAGE_RIGHT, MAX_FILE_SIZE } from "../../utils/Constants";
import { formatDate } from "../../utils/DateTime";
import { FlatFormGroup } from "../common/FlatFormGroup";
import { Spinner } from "../common/Spinner";

export const OwnReservation = props => {

    const {t} = useTranslation();
    const [userInfo] = useContext(AuthenticationContext);
    const [front, setFront] = useState();
    const [right, setRight] = useState();
    const [back, setBack] = useState();
    const [left, setLeft] = useState();
    const [reservation, setReservation] = useState({
        number: "",
        startDate: "",
        endDate: "",
        carDto: {},
        signature: ""
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

    const handleSubmit = () => {
        if (front && back && right && left) {
            document.getElementById("imageAmountFeedback").style.display = "none";
            Swal.fire({
                titleText: t("login.otp.code"),
                input: "password",
                preConfirm: otpCode => {
                    const formData = new FormData();
                    formData.append("signature", reservation.signature);
                    formData.append(IMAGE_FRONT, front);
                    formData.append(IMAGE_RIGHT, right);
                    formData.append(IMAGE_BACK, back);
                    formData.append(IMAGE_LEFT, left);
                    axios.put(`/receiveReservation/${userInfo.username}/${reservation.number}`,
                        formData,
                        {headers: {"Otp-Code": otpCode}})
                        .then(() => {
                            handleSuccess(`reservation.${props.match.params.action}.success`, "");
                            props.history.push(`/ownReservationDetails/${reservation.number}`);
                        }).catch(error => {
                        handleError(error);
                    });
                }
            }).then(() => {});
        } else {
            document.getElementById("imageAmountFeedback").style.display = "block";
        }
    };

    const renderActiveBreadcrumb = () => {
        if (props.match.params.action === ACTION_RECEIVE) {
            return <Breadcrumb.Item active>{t("breadcrumbs.receiveReservation")}</Breadcrumb.Item>;
        } else if (props.match.params.action === ACTION_FINISH) {
            return <Breadcrumb.Item active>{t("breadcrumbs.finishReservation")}</Breadcrumb.Item>;
        } else {
            return <Breadcrumb.Item active/>;
        }
    };

    const handleUpload = (event, name) => {
        const image = event.target.files[0];
        if (image.size > MAX_FILE_SIZE) {
            document.getElementById(`${name}SizeFeedback`).style.display = "block";
            return;
        } else {
            document.getElementById(`${name}SizeFeedback`).style.display = "none";
        }
        switch (name) {
            case IMAGE_FRONT:
                setFront(image);
                break;
            case IMAGE_RIGHT:
                setRight(image);
                break;
            case IMAGE_BACK:
                setBack(image);
                break;
            case IMAGE_LEFT:
                setLeft(image);
                break;
            default:
                return;
        }
        const fileReader = new FileReader();
        fileReader.readAsDataURL(image);
        fileReader.onload = e => {
            document.getElementById(`${name}Image`).src = e.target.result;
            document.getElementById(`${name}Image`).style.display = "block";
        };
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
                    <LinkContainer to="/listOwnReservations" exact>
                        <Breadcrumb.Item>{t("breadcrumbs.listReservations")}</Breadcrumb.Item>
                    </LinkContainer>
                    <LinkContainer to={`/ownReservationDetails/${reservation.number}`} exact>
                        <Breadcrumb.Item>{t("breadcrumbs.reservationDetails")}</Breadcrumb.Item>
                    </LinkContainer>
                    {renderActiveBreadcrumb()}
                </Breadcrumb>
                <Container>
                    <Row className="justify-content-center">
                        <Col sm={6} className="form-container">
                            <h1 className="text-center">{t(`breadcrumbs.${props.match.params.action}Reservation`)}</h1>
                            <Form style={{marginTop: "2em"}}>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.car")}</FormLabel>
                                    <FormControl id="car"
                                                 value={`${reservation.carDto.make} ${reservation.carDto.model}`}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.date")}</FormLabel>
                                    <FormControl id="date"
                                                 value={`${formatDate(reservation.startDate)} - ${formatDate(reservation.endDate)}`}
                                                 disabled
                                                 plaintext/>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.image.front")} *</FormLabel>
                                    <FormControl id="frontUpload"
                                                 type="file"
                                                 accept="image/*"
                                                 style={{margin: "1em 0"}}
                                                 onChange={event => handleUpload(event, IMAGE_FRONT)}/>
                                    <img id="frontImage" src="#" alt="frontAlt" style={{margin: "0 auto", display: "none", maxWidth: "400px", maxHeight: "200px"}}/>
                                    <p id="frontSizeFeedback" className="invalid" style={{display: "none"}}>{t("validation:file.size.max")}</p>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.image.back")} *</FormLabel>
                                    <FormControl id="backUpload"
                                                 type="file"
                                                 accept="image/*"
                                                 style={{margin: "1em 0"}}
                                                 onChange={event => handleUpload(event, IMAGE_BACK)}/>
                                    <img id="backImage" src="#" alt="backAlt" style={{margin: "0 auto", display: "none", maxWidth: "400px", maxHeight: "200px"}}/>
                                    <p id="backSizeFeedback" className="invalid" style={{display: "none"}}>{t("validation:file.size.max")}</p>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.image.right")} *</FormLabel>
                                    <FormControl id="rightUpload"
                                                 type="file"
                                                 accept="image/*"
                                                 style={{margin: "1em 0"}}
                                                 onChange={event => handleUpload(event, IMAGE_RIGHT)}/>
                                    <img id="rightImage" src="#" alt="rightAlt" style={{margin: "0 auto", display: "none", maxWidth: "400px", maxHeight: "200px"}}/>
                                    <p id="rightSizeFeedback" className="invalid" style={{display: "none"}}>{t("validation:file.size.max")}</p>
                                    <hr/>
                                </FormGroup>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("reservation.image.left")} *</FormLabel>
                                    <FormControl id="leftUpload"
                                                 type="file"
                                                 accept="image/*"
                                                 style={{margin: "1em 0"}}
                                                 onChange={event => handleUpload(event, IMAGE_LEFT)}/>
                                    <img id="leftImage" src="#" alt="leftAlt" style={{margin: "0 auto", display: "none", maxWidth: "400px", maxHeight: "200px"}}/>
                                    <p id="leftSizeFeedback" className="invalid" style={{display: "none"}}>{t("validation:file.size.max")}</p>
                                    <p id="imageAmountFeedback" className="invalid" style={{display: "none"}}>{t("validation:file.amount.invalid")}</p>
                                </FormGroup>
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        onClick={() => props.history.push(`/ownReservationDetails/${reservation.number}`)}>{t("navigation.back")}</Button>
                                <Button id="submit"
                                        onClick={handleSubmit}>{t("navigation.submit")}</Button>
                                <Button id="log"
                                        onClick={() => console.log(front)}>Log</Button>
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
