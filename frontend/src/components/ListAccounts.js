import { faHome ,faSearch } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Breadcrumb, Button, Container, FormControl, InputGroup } from "react-bootstrap";
import BootstrapTable from 'react-bootstrap-table-next';
import "react-bootstrap-table-next/dist/react-bootstrap-table2.min.css";
import paginationFactory from 'react-bootstrap-table2-paginator';
import "react-bootstrap-table2-paginator/dist/react-bootstrap-table2-paginator.min.css";
import { useTranslation } from "react-i18next";
import { LinkContainer } from 'react-router-bootstrap';
import Swal from "sweetalert2";
import "../styles/Table.css";
import { PAGINATION_SIZES } from "../utils/Constants";
import { Spinner } from "./Spinner";

export const ListAccounts = props => {

    const {t} = useTranslation();
    const [accounts, setAccounts] = useState([]);
    const [page, setPage] = useState(1);
    const [sizePerPage, setSizePerPage] = useState(5);
    const [totalSize, setTotalSize] = useState(0);
    const [sortField, setSortField] = useState("");
    const [sortOrder, setSortOrder] = useState("");
    const [filter, setFilter] = useState("");
    const [loaded, setLoaded] = useState(false);
    const columns = [{
        dataField: "username",
        text: t("account.username"),
        sort: true
    }, {
        dataField: "email",
        text: t("account.email"),
        sort: true
    }, {
        dataField: "firstName",
        text: t("account.firstName"),
        sort: true
    }, {
        dataField: "lastName",
        text: t("account.lastName"),
        sort: true
    }, {
        dataField: "details",
        text: t("navigation.details"),
        isDummyField: true,
        formatter: (cell, row) => {
            const handleDetails = username => {
                props.history.push(`/accountDetails/${username}`)
            };
            return <Button onClick={() => handleDetails(row["username"])}>{t("navigation.details")}</Button>
        }
    }];

    useEffect(() => {
        const url = () => {
            if (filter) {
                if (sortField) {
                    return `/accounts/${filter}/${page - 1}/${sizePerPage}/${sortField}/${sortOrder}`;
                } else {
                    return `/accounts/${filter}/${page - 1}/${sizePerPage}`;
                }
            } else {
                if (sortField) {
                    return `/accounts/${page - 1}/${sizePerPage}/${sortField}/${sortOrder}`;
                } else {
                    return `/accounts/${page - 1}/${sizePerPage}`;
                }
            }
        };
        axios.get(url())
            .then(response => {
                if (response.data.empty) {
                    if (response.data.totalPages === 0) {
                        setPage(1);
                    } else {
                        setPage(response.data.totalPages);
                    }
                }
                setAccounts(response.data.content);
                setTotalSize(response.data.totalElements);
                setLoaded(true);
            }).catch(error => {
                console.log(error);
            Swal.fire(t("errors:common.header"),
                t(`errors:${error.response.data}`),
                "error");
        });
    }, [filter, page, sizePerPage, sortField, sortOrder, t]);

    const handleTableChange = (type, { page, sizePerPage, sortField, sortOrder }) => {
        setPage(page);
        setSizePerPage(sizePerPage);
        setSortField(sortField);
        setSortOrder(sortOrder);
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
                    <Breadcrumb.Item active>{t("breadcrumbs.listAccounts")}</Breadcrumb.Item>
                </Breadcrumb>
                <Container>
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
                                    keyField="email"
                                    data={accounts}
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
