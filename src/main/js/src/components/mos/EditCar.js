import { faHome } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Breadcrumb, Button, ButtonToolbar, Col, Container, Form, FormCheck, FormControl, FormGroup, FormLabel, Row } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import Swal from "sweetalert2";
import { array, bool, object, string } from "yup";
import { handleError, handleSuccess } from "../../utils/Alerts";
import { CAR_CATEGORIES, CURRENCY } from "../../utils/Constants";
import { MONEY_REGEX, STRING_REGEX, validate, YEAR_REGEX } from "../../utils/Validation";
import { EditFormGroup } from "../common/EditFormGroup";
import { Spinner } from "../common/Spinner";

export const EditCar = props => {

    const {t} = useTranslation();
    const schema = object().shape({
        number: string(),
        make: string().required("car.make.required").min(1, "car.make.min").max(32, "car.make.max").matches(STRING_REGEX, "car.make.invalid"),
        model: string().required("car.model.required").min(1, "car.model.min").max(32, "car.model.max").matches(STRING_REGEX, "car.model.invalid"),
        description: string().required("car.description.required").min(1, "car.description.min").max(255, "car.description.max").matches(STRING_REGEX, "car.description.invalid"),
        productionYear: string().required("car.productionYear.required").matches(YEAR_REGEX, "car.productionYear.invalid"),
        price: string().required("car.price.required").matches(MONEY_REGEX, "car.price.invalid"),
        category: string().required("car.category.required"),
        active: bool(),
        reservations: array(),
        signature: string()
    });
    const [car, setCar] = useState({
        make: "",
        model: "",
        description: "",
        productionYear: "",
        price: "",
        active: false
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
                setCar(response.data);
                setLoaded(true);
            }).catch(error => {
                handleError(error);
        });
    }, [props.match.params.number, t]);

    const handleChangeActive = event => {
        setCar({...car, [event.target.id]: !car[event.target.id]});
    };

    const handleSubmit = () => {
        if (validate(car, errors, setErrors, schema)) {
            Swal.fire({
                titleText: t("login.otp.code"),
                input: "password",
                preConfirm: otpCode => {
                    const tempCar = {...car};
                    tempCar.productionYear = parseInt(tempCar.productionYear, 10);
                    tempCar.price = tempCar.price.replaceAll(",", ".");
                    axios.put(`/editCar/${tempCar.number}`,
                        tempCar,
                        {headers: {"Otp-Code": otpCode}})
                        .then(() => {
                            handleSuccess("car.edit.success", "");
                            props.history.push(`/carDetails/${tempCar.number}`);
                        }).catch(error => {
                            handleError(error);
                    });
                }
            }).then(() => {});
        }
    };

    const validateCategory = object => {
        if (object.category) {
            document.getElementById("category").classList.remove("is-invalid");
            document.getElementById("categoryFeedback").style.display = "none";
            return true;
        } else {
            document.getElementById("category").classList.add("is-invalid");
            document.getElementById("categoryFeedback").style.display = "block";
            return false;
        }
    };

    const handleChangeCategory = event => {
        const temp = {...car, "category": event.target.value};
        setCar(temp);
        validateCategory(temp);
    };

    const renderCategorySelect = () => {
        const options = [];
        options.push(
            <option label={t("navigation.select")} selected disabled hidden/>
        );
        for (let category of CAR_CATEGORIES) {
            options.push(
                <option label={t(category)}>{category}</option>
            );
        }
        return (
            <FormGroup>
                <FormLabel className="font-weight-bold">{t("car.category")}</FormLabel>
                <FormControl id="category"
                             as="select"
                             value={car.category}
                             onChange={handleChangeCategory}>
                    {options}
                </FormControl>
                <p id="categoryFeedback" className="invalid" style={{display: "none"}}>{t("validation:car.category.required")}</p>
            </FormGroup>
        );
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
                        <Col sm={6} className="form-container">
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
                                               label="car.details.price"
                                               type="number"
                                               suffix={CURRENCY}
                                               required/>
                                {renderCategorySelect()}
                                <FormGroup>
                                    <FormLabel className="font-weight-bold">{t("car.activity")}</FormLabel>
                                    <FormCheck id="active" label={t("car.active")} onChange={handleChangeActive} defaultChecked={car.active}/>
                                </FormGroup>
                            </Form>
                            <ButtonToolbar className="justify-content-center">
                                <Button id="back"
                                        onClick={() => props.history.push(`/carDetails/${car.number}`)}>{t("navigation.back")}</Button>
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
