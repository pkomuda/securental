import { faHome, faSearch } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Breadcrumb, Container, FormControl, InputGroup } from "react-bootstrap";
import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import { useTranslation } from "react-i18next";
import { LinkContainer } from 'react-router-bootstrap';
import { handleError } from "../../utils/Alerts";
import { PAGINATION_SIZES } from "../../utils/Constants";
import { Spinner } from "../common/Spinner";

export const ListLogs = () => {

    const {t} = useTranslation();
    const [logs, setLogs] = useState([]);
    const [page, setPage] = useState(1);
    const [sizePerPage, setSizePerPage] = useState(10);
    const [totalSize, setTotalSize] = useState(0);
    const [filter, setFilter] = useState("");
    const [loaded, setLoaded] = useState(false);
    const columns = [{
        dataField: "message",
        text: t("log.message")
    }];

    useEffect(() => {
        const url = () => {
            if (filter) {
                return `/logs/${filter}/${page - 1}/${sizePerPage}`;
            } else {
                return `/logs/${page - 1}/${sizePerPage}`;
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
                setLogs(response.data.content);
                setTotalSize(response.data.totalElements);
                setLoaded(true);
            }).catch(error => {
            handleError(error);
        });
    }, [filter, page, sizePerPage, t]);

    const handleTableChange = (type, { page, sizePerPage }) => {
        setPage(page);
        setSizePerPage(sizePerPage);
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
                    <Breadcrumb.Item active>{t("breadcrumbs.listLogs")}</Breadcrumb.Item>
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
                                    keyField="message"
                                    data={logs}
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
