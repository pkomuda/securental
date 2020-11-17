import React, { useEffect, useState } from "react";
import axios from "axios";
import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import { useTranslation } from "react-i18next";
import { Breadcrumb, Button, ButtonToolbar, Container, FormControl } from "react-bootstrap";
import { LinkContainer } from 'react-router-bootstrap';
import Swal from "sweetalert2";
import "react-bootstrap-table-next/dist/react-bootstrap-table2.min.css";
import "react-bootstrap-table2-paginator/dist/react-bootstrap-table2-paginator.min.css";
import "../styles/Table.css";
import { sizes } from "../utils/Constants";

export const ListAccounts = props => {

    const {t} = useTranslation();
    const [accounts, setAccounts] = useState([]);
    const [page, setPage] = useState(1);
    const [sizePerPage, setSizePerPage] = useState(5);
    const [totalSize, setTotalSize] = useState(0);
    const [sortField, setSortField] = useState("");
    const [sortOrder, setSortOrder] = useState("");
    const [filter, setFilter] = useState("");
    const columns = [{
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
            const handleDetails = email => {
                alert(email);
            };
            return <Button onClick={() => handleDetails(row["email"])}>{t("navigation.details")}</Button>
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
                    setPage(response.data.totalPages);
                }
                setAccounts(response.data.content);
                setTotalSize(response.data.totalElements);
            }).catch(error => {
            Swal.fire(t("errors:common.header"),
                t("errors:" + error.response.data),
                "error");
        });
    }, [filter, page, sizePerPage, sortField, sortOrder, t]);

    const handleTableChange = (type, { page, sizePerPage, sortField, sortOrder }) => {
        setPage(page);
        setSizePerPage(sizePerPage);
        setSortField(sortField);
        setSortOrder(sortOrder);
    };

    return (
        <React.Fragment>
            <Breadcrumb>
                <LinkContainer to="/" exact>
                    <Breadcrumb.Item>Home</Breadcrumb.Item>
                </LinkContainer>
                <Breadcrumb.Item active>Account list</Breadcrumb.Item>
            </Breadcrumb>
            <Container>
                <FormControl id="filter"
                             placeholder="Search"
                             value={filter}
                             onChange={event => setFilter(event.target.value)}/>
                <BootstrapTable remote
                                bootstrap4
                                keyField="email"
                                data={accounts}
                                columns={columns}
                                pagination={paginationFactory({page, sizePerPage, totalSize, sizePerPageList: sizes})}
                                onTableChange={handleTableChange}/>
                <ButtonToolbar className="justify-content-center">
                    <Button id="back"
                            variant="dark"
                            className="button"
                            onClick={() => props.history.goBack}>{t("navigation.back")}</Button>
                </ButtonToolbar>
            </Container>
        </React.Fragment>
    );
};