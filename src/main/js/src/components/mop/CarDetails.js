import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useContext, useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import { handleError } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { ACCESS_LEVEL_CLIENT, ACCESS_LEVEL_EMPLOYEE, CURRENCY } from "../../utils/Constants";
import { FlatFormGroup } from "../common/FlatFormGroup";
import { Spinner } from "../common/Spinner";

export const CarDetails = props => {

    const {t} = useTranslation();
    const [userInfo] = useContext(AuthenticationContext);
    const [car, setCar] = useState({
        number: "",
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
                setCar(response.data);
                setLoaded(true);
            }).catch(error => {
                handleError(error);
                props.history.push("/listCars");
        });
    }, [props.history, props.match.params.number, t]);

    FlatFormGroup.defaultProps = {
        values: car
    };

    const clientButtons = () => {
        if (userInfo.currentAccessLevel === ACCESS_LEVEL_CLIENT) {
            return <Button id="reserve"
                           disabled={!car.active}
                           onClick={() => props.history.push(`/addReservation/${car.number}`)}>{t("reservation.reserve")}</Button>;
        }
    };

    const employeeButtons = () => {
        if (userInfo.currentAccessLevel === ACCESS_LEVEL_EMPLOYEE) {
            return <Button id="edit"
                           onClick={() => props.history.push(`/editCar/${car.number}`)}>{t("navigation.edit")}</Button>;
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
                    <Breadcrumb.Item active>{t("breadcrumbs.carDetails")}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
                    <Row className="justify-content-center">
                        <Col sm={6} className="form-container">
                            <Form>
                                <FlatFormGroup id="number"
                                               label="car.number"/>
                                <FlatFormGroup id="make"
                                               label="car.make"/>
                                <FlatFormGroup id="model"
                                               label="car.model"/>
                                <FlatFormGroup id="description"
                                               label="car.description"/>
                                <FlatFormGroup id="productionYear"
                                               label="car.productionYear"/>
                                <FlatFormGroup id="price"
                                               label="car.details.price"
                                               suffix={CURRENCY}/>
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
                                {employeeButtons()}
                                {clientButtons()}
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
