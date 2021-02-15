import { faHome, faSearch } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Breadcrumb, Button, Container, FormCheck, FormControl, FormGroup, FormLabel, InputGroup } from "react-bootstrap";
import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import { useTranslation } from "react-i18next";
import { LinkContainer } from 'react-router-bootstrap';
import { handleError } from "../../utils/Alerts";
import { CAR_CATEGORIES, CURRENCY, PAGINATION_SIZES } from "../../utils/Constants";
import { formatDecimal } from "../../utils/i18n";
import { Spinner } from "../common/Spinner";

export const ListCars = props => {

    const {t} = useTranslation();
    const [cars, setCars] = useState([]);
    const [categories, setCategories] = useState(CAR_CATEGORIES);
    const [page, setPage] = useState(1);
    const [sizePerPage, setSizePerPage] = useState(10);
    const [totalSize, setTotalSize] = useState(0);
    const [sortField, setSortField] = useState("");
    const [sortOrder, setSortOrder] = useState("");
    const [filter, setFilter] = useState("");
    const [loaded, setLoaded] = useState(false);
    const columns = [{
        dataField: "make",
        text: t("car.make"),
        sort: true
    }, {
        dataField: "model",
        text: t("car.model"),
        sort: true
    }, {
        dataField: "category",
        text: t("car.category"),
        sort: true,
        formatter: (cell, row) => {
            return t(row["category"]);
        }
    }, {
        dataField: "productionYear",
        text: t("car.productionYear"),
        sort: true
    }, {
        dataField: "price",
        text: t("car.list.price"),
        sort: true,
        formatter: (cell, row) => {
            return `${formatDecimal(row["price"])} ${CURRENCY}`;
        }
    }, {
        dataField: "details",
        text: t("navigation.details"),
        isDummyField: true,
        formatter: (cell, row) => {
            const handleDetails = number => {
                props.history.push(`/carDetails/${number}`)
            };
            return <Button onClick={() => handleDetails(row["number"])}>{t("navigation.details")}</Button>;
        }
    }];

    useEffect(() => {
        const url = () => {
            if (filter) {
                if (sortField) {
                    return `/cars/${filter}/${page - 1}/${sizePerPage}/${sortField}/${sortOrder}`;
                } else {
                    return `/cars/${filter}/${page - 1}/${sizePerPage}`;
                }
            } else {
                if (sortField) {
                    return `/cars/${page - 1}/${sizePerPage}/${sortField}/${sortOrder}`;
                } else {
                    return `/cars/${page - 1}/${sizePerPage}`;
                }
            }
        };
        axios.get(url(), {params: {"categories": categories.join(",")}})
            .then(response => {
                if (response.data.empty) {
                    if (response.data.totalPages === 0) {
                        setPage(1);
                    } else {
                        setPage(response.data.totalPages);
                    }
                }
                setCars(response.data.content);
                setTotalSize(response.data.totalElements);
                setLoaded(true);
            }).catch(error => {
                handleError(error);
        });
    }, [categories, filter, page, sizePerPage, sortField, sortOrder, t]);

    const handleTableChange = (type, { page, sizePerPage, sortField, sortOrder }) => {
        setPage(page);
        setSizePerPage(sizePerPage);
        setSortField(sortField);
        setSortOrder(sortOrder);
    };

    const handleChangeCategory = (event, category) => {
        if (categories.includes(category)) {
            setCategories(categories.filter(element => element !== category));
        } else {
            setCategories([...categories, category]);
        }
    };

    const renderCategorySelect = () => {
        const categories = [];
        for (let category of CAR_CATEGORIES) {
            categories.push(
                <FormCheck inline
                           defaultChecked
                           label={t(category)}
                           onChange={event => handleChangeCategory(event, category)}/>
            );
        }
        return (
            <FormGroup>
                <FormLabel className="flat-form-label" style={{marginRight: "1em"}}>{t("car.category")}</FormLabel>
                {categories}
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
                    <Breadcrumb.Item active>{t("breadcrumbs.listCars")}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
                    {renderCategorySelect()}
                    <InputGroup>
                        <InputGroup.Prepend>
                            <InputGroup.Text>
                                <FontAwesomeIcon icon={faSearch}/>
                            </InputGroup.Text>
                        </InputGroup.Prepend>
                        <FormControl id="filter"
                                     placeholder={t("navigation.search")}
                                     value={filter}
                                     onChange={event => setFilter(event.target.value)}/>
                    </InputGroup>
                    <BootstrapTable remote
                                    bootstrap4
                                    keyField="number"
                                    data={cars}
                                    columns={columns}
                                    pagination={paginationFactory({page, sizePerPage, totalSize, sizePerPageList: PAGINATION_SIZES})}
                                    onTableChange={handleTableChange}/>
                </Container>
            </React.Fragment>
        );
    } else {
        return <Spinner/>;
    }
};
