import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import { FlatFormGroup } from "../FlatFormGroup";
import { Spinner } from "../Spinner";

export const CarDetails = props => {

    const {t} = useTranslation();
    const [car, setCar] = useState({
        make: "",
        model: "",
        description: "",
        productionYear: "",
        price: 0,
        active: false
    });
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        axios.get(`/car/${props.match.params.number}`)
            .then(response => {
                const tempCar = response.data
                setCar(response.data);
                setLoaded(true);
            }).catch(error => {
            Swal.fire(t("errors:common.header"),
                t(`errors:${error.response.data}`),
                "error");
        });
    }, [props.match.params.number, t]);

    FlatFormGroup.defaultProps = {
        values: car
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
                    <Breadcrumb.Item active>{t("breadcrumbs.carDetails")}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
                    <Row className="justify-content-center">
                        <Col sm={5} className="form-container">
                            <Form>
                                <FlatFormGroup id="make"
                                               label="car.make"/>
                                <FlatFormGroup id="model"
                                               label="car.model"/>
                                <FlatFormGroup id="description"
                                               label="car.description"/>
                                <FlatFormGroup id="productionYear"
                                               label="car.productionYear"/>
                                <FlatFormGroup id="price"
                                               label="car.price"
                                               suffix="PLN"/>
                                <FormGroup>
                                    <FormLabel className="flat-form-label">{t("car.activity")}</FormLabel>
                                    <FormControl id="active"
                                                 value={car.active ? t("car.active") : t("account.inactive")}
                                                 disabled
                                                 plaintext/>
                                </FormGroup>
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        onClick={() => props.history.push("/listCars")}>{t("navigation.back")}</Button>
                                <Button id="edit"
                                        onClick={() => props.history.push(`/editCar/${car.number}`)}>{t("navigation.edit")}</Button>
                                <Button id="reserve"
                                        onClick={() => props.history.push(`/addReservation/${car.number}`)}>{t("reservation.reserve")}</Button>
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
