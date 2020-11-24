import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormCheck, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import { bool, number, object, string } from "yup";
import { FOUR_DIGIT_INTEGER_REGEX, validate } from "../../utils/Validation";
import { EditFormGroup } from "../EditFormGroup";
import { Spinner } from "../Spinner";

export const EditCar = props => {

    const {t} = useTranslation();
    const schema = object().shape({
        make: string().required("car.make.required").min(1, "car.make.min").max(32, "car.make.max"),
        model: string().required("car.model.required").min(1, "car.model.min").max(32, "car.model.max"),
        description: string().required("car.description.required").min(1, "car.description.min").max(255, "car.description.max"),
        productionYear: string().required("car.productionYear.required").matches(FOUR_DIGIT_INTEGER_REGEX, "car.productionYear.invalid"),
        price: number().required(),
        active: bool()
    });
    const [car, setCar] = useState({
        make: "",
        model: "",
        description: "",
        productionYear: "",
        price: "",
        active: false,
    });
    const [loaded, setLoaded] = useState(false);
    const [errors, setErrors] = useState({});
    EditFormGroup.defaultProps = {
        schema: schema,
        values: car,
        errors: errors,
        setValues: newCar => setCar(newCar),
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

    const handleChangeActive = event => {
        setCar({...car, [event.target.id]: !car[event.target.id]});
    };

    const handleSubmit = () => {
        if (validate(car, errors, setErrors, schema)) {
            const tempCar = {...car};
            tempCar.active = parseInt(tempCar.active, 10);
            console.log(tempCar);
            axios.put(`/car/${tempCar.number}`, tempCar)
                .then(() => {
                    const alerts = [];
                    alerts.push({
                        title: t("register.password.header"),
                        html: t("register.password.text1"),
                        icon: "success"
                    });
                    Swal.queue(alerts);
                    props.history.push(`/carDetails/${tempCar.number}`);
                }).catch(() => {
                Swal.fire(t("errors:common.header"),
                    t("errors:common.text"),
                    "error");
            });
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
                    <Breadcrumb.Item active>{t("breadcrumbs.editCar")}</Breadcrumb.Item>
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
                                               required
                                               textarea/>
                                <EditFormGroup id="productionYear"
                                               label="car.productionYear"
                                               type="number"
                                               required/>
                                <EditFormGroup id="price"
                                               label="car.price"
                                               type="number"
                                               suffix="zł"
                                               required/>
                                <FormGroup>
                                    <FormLabel className="font-weight-bold">{t("car.activity")}</FormLabel>
                                    <FormCheck id="active" label={t("car.active")} onChange={handleChangeActive} defaultChecked={car.active}/>
                                </FormGroup>
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        className="button"
                                        onClick={() => props.history.goBack()}>{t("navigation.back")}</Button>
                                <Button id="edit"
                                        className="button"
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
