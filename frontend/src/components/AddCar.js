import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormCheck, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import { bool, object, string, number } from "yup";
import { validate } from "../utils/Validation";
import { EditFormGroup } from "./EditFormGroup";

export const AddCar = props => {

    const {t} = useTranslation();
    const schema = object().shape({
        make: string().required("account.username.required").min(1, "account.username.min").max(32, "account.username.max"),
        model: string().required("account.email.required").min(1, "account.username.min").max(32, "account.username.max"),
        description: string().required("account.firstName.required").min(1, "account.firstName.min").max(255, "account.firstName.max"),
        productionYear: number(),
        price: number(),
        active: bool()
    });
    const [car, setCar] = useState({
        make: "",
        model: "",
        description: "",
        productionYear: 0,
        price: 0,
        active: false,
    });
    const [errors, setErrors] = useState({});
    EditFormGroup.defaultProps = {
        schema: schema,
        values: car,
        errors: errors,
        setValues: newCar => setCar(newCar),
        setErrors: newErrors => setErrors(newErrors)
    };

    const handleSubmit = () => {
        if (validate(car, errors, setErrors, schema)) {
            axios.post("/car", car)
                .then(() => {
                    Swal.fire(t("errors:common.header"),
                        t("errors:common.text"),
                        "success");
                    props.history.push("/");
                }).catch(() => {
                    Swal.fire(t("errors:common.header"),
                        t("errors:common.text"),
                        "error");
            });
        }
    };

    const handleChangeActive = event => {
        setCar({...car, [event.target.id]: !car[event.target.id]});
    };

    return (
        <React.Fragment>
            <Breadcrumb>
                <LinkContainer to="/" exact>
                    <Breadcrumb.Item>
                        <FontAwesomeIcon icon={faHome}/>
                    </Breadcrumb.Item>
                </LinkContainer>
                <Breadcrumb.Item active>{t("breadcrumbs.addAccount")}</Breadcrumb.Item>
            </Breadcrumb>
            <Container>
                <Row className="justify-content-center">
                    <Col sm={5} className="form-container">
                        <Form>
                            <EditFormGroup id="make"
                                           label="car.make"
                                           required/>
                            <EditFormGroup id="model"
                                           label="car.model"
                                           required/>
                            <EditFormGroup id="description"
                                           label="car.description"
                                           required/>
                            <EditFormGroup id="productionYear"
                                           label="car.productionYear"
                                           type="number"
                                           required/>
                            <EditFormGroup id="price"
                                           label="car.price"
                                           type="number"
                                           required/>
                            <FormGroup>
                                <FormLabel className="font-weight-bold">{t("car.activity")}</FormLabel>
                                <FormCheck id="active" label={t("car.active")} onChange={handleChangeActive}/>
                            </FormGroup>
                        </Form>
                        <ButtonToolbar className="justify-content-center">
                            <Button id="back"
                                    className="button"
                                    onClick={() => props.history.goBack}>{t("navigation.back")}</Button>
                            <Button id="submit"
                                    className="button"
                                    onClick={handleSubmit}>{t("navigation.submit")}</Button>
                        </ButtonToolbar>
                    </Col>
                </Row>
            </Container>
        </React.Fragment>
    );
};
