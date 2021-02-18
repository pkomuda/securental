import { faHome, faSearch } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useContext, useEffect, useState } from "react";
import { Breadcrumb, Button, Container, FormCheck, FormControl, FormGroup, FormLabel, InputGroup } from "react-bootstrap";
import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import { useTranslation } from "react-i18next";
import { LinkContainer } from 'react-router-bootstrap';
import { handleError } from "../../utils/Alerts";
import { AuthenticationContext } from "../../utils/AuthenticationContext";
import { PAGINATION_SIZES, RESERVATION_STATUSES } from "../../utils/Constants";
import { formatDate } from "../../utils/DateTime";
import { Spinner } from "../common/Spinner";

export const ListOwnReservations = props => {

    const {t} = useTranslation();
    const [userInfo] = useContext(AuthenticationContext);
    const [reservations, setReservations] = useState([]);
    const [statuses, setStatuses] = useState(RESERVATION_STATUSES);
    const [page, setPage] = useState(1);
    const [sizePerPage, setSizePerPage] = useState(10);
    const [totalSize, setTotalSize] = useState(0);
    const [sortField, setSortField] = useState("");
    const [sortOrder, setSortOrder] = useState("");
    const [filter, setFilter] = useState("");
    const [loaded, setLoaded] = useState(false);
    const columns = [{
        dataField: "number",
        text: t("reservation.number"),
        sort: true,
        style: {wordBreak: "break-all"}
    }, {
        dataField: "carDto.make",
        text: t("reservation.carMake"),
        sort: true
    }, {
        dataField: "carDto.model",
        text: t("reservation.carModel"),
        sort: true
    }, {
        dataField: "startDate",
        text: t("reservation.startDate"),
        sort: true,
        formatter: (cell, row) => {
            return formatDate(row["startDate"], userInfo);
        }
    }, {
        dataField: "endDate",
        text: t("reservation.endDate"),
        sort: true,
        formatter: (cell, row) => {
            return formatDate(row["endDate"], userInfo);
        }
    }, {
        dataField: "status",
        text: t("reservation.status"),
        sort: true,
        formatter: (cell, row) => {
            return t(row["status"]);
        }
    }, {
        dataField: "details",
        text: t("navigation.details"),
        isDummyField: true,
        formatter: (cell, row) => {
            const handleDetails = number => {
                props.history.push(`/ownReservationDetails/${number}`)
            };
            return <Button onClick={() => handleDetails(row["number"])}>{t("navigation.details")}</Button>;
        }
    }];

    useEffect(() => {
        const url = () => {
            if (filter) {
                if (sortField) {
                    return `/ownReservations/${userInfo.username}/${filter}/${page - 1}/${sizePerPage}/${sortField}/${sortOrder}`;
                } else {
                    return `/ownReservations/${userInfo.username}/${filter}/${page - 1}/${sizePerPage}`;
                }
            } else {
                if (sortField) {
                    return `/ownReservations/${userInfo.username}/${page - 1}/${sizePerPage}/${sortField}/${sortOrder}`;
                } else {
                    return `/ownReservations/${userInfo.username}/${page - 1}/${sizePerPage}`;
                }
            }
        };
        axios.get(url(), {params: {"statuses": statuses.join(",")}})
            .then(response => {
                if (response.data.empty) {
                    if (response.data.totalPages === 0) {
                        setPage(1);
                    } else {
                        setPage(response.data.totalPages);
                    }
                }
                setReservations(response.data.content);
                setTotalSize(response.data.totalElements);
                setLoaded(true);
            }).catch(error => {
                handleError(error);
        });
    }, [filter, page, sizePerPage, sortField, sortOrder, statuses, t, userInfo.username]);

    const handleTableChange = (type, { page, sizePerPage, sortField, sortOrder }) => {
        setPage(page);
        setSizePerPage(sizePerPage);
        setSortField(sortField);
        setSortOrder(sortOrder);
    };

    const handleChangeStatus = status => {
        if (statuses.includes(status)) {
            setStatuses(statuses.filter(element => element !== status));
        } else {
            setStatuses([...statuses, status]);
        }
    };

    const renderStatusSelect = () => {
        const statuses = [];
        for (let status of RESERVATION_STATUSES) {
            statuses.push(
                <FormCheck inline
                           defaultChecked
                           label={t(status)}
                           onChange={() => handleChangeStatus(status)}/>
            );
        }
        return (
            <FormGroup>
                <FormLabel className="flat-form-label" style={{marginRight: "1em"}}>{t("reservation.status")}</FormLabel>
                {statuses}
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
                    <Breadcrumb.Item active>{t("breadcrumbs.listReservations")}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
                    {renderStatusSelect()}
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
                                    data={reservations}
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
