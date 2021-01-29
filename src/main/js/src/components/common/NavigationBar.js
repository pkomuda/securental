import { faCar, faSignInAlt, faUser, faUserPlus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import React, { useContext, useEffect, useState } from "react";
import { Nav, Navbar, NavDropdown } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import { Link, useHistory } from "react-router-dom";
import { AuthenticationContext, isAuthenticated } from "../../utils/AuthenticationContext";
import { ACCESS_LEVEL_ADMIN, ACCESS_LEVEL_CLIENT, ACCESS_LEVEL_EMPLOYEE } from "../../utils/Constants";
import { Spinner } from "./Spinner";

export const NavigationBar = () => {

    const {t} = useTranslation();
    const history = useHistory();
    const [userInfo, setUserInfo] = useContext(AuthenticationContext);
    const [currentAccessLevel, setCurrentAccessLevel] = useState("");

    useEffect(() => {
        setCurrentAccessLevel(userInfo.currentAccessLevel);
    }, [userInfo.currentAccessLevel]);

    if (isAuthenticated(userInfo)) {
        setTimeout(() => {
            clearUserInfo();
            history.push("/login/sessionExpired");
        }, userInfo.tokenExpiration - new Date().getTime());
    }

    const clearUserInfo = () => {
        setUserInfo({
            username: "",
            accessLevels: [],
            currentAccessLevel: "",
            tokenPresent: false,
            tokenExpiration: 0
        });
    };

    const handleLogout = () => {
        axios.post("/logout")
            .then(() => {
                axios.get("/currentUser")
                    .then(() => {
                        clearUserInfo();
                        window.location.replace("/");
                    }).catch(() => {
                        clearUserInfo();
                        history.push("/");
                });
            }).catch(() => {
                clearUserInfo();
                window.location.replace("/");
        });
    };

    const carList = () => {
        return (
            <Nav.Item>
                <LinkContainer to="/listCars">
                    <div className="nav-icon-item">
                        <FontAwesomeIcon icon={faCar}/> {t("navbar.cars")}
                    </div>
                </LinkContainer>
            </Nav.Item>
        );
    };

    const accessLevelsDropdown = () => {
        if (userInfo.accessLevels.length > 1) {
            const accessLevels = [];
            for (let accessLevel of userInfo.accessLevels) {
                accessLevels.push(
                    <NavDropdown.Item key={accessLevel}
                                      onClick={() => {
                                          setUserInfo({...userInfo, currentAccessLevel: accessLevel});
                                          setCurrentAccessLevel(accessLevel);
                                          history.push("/");
                                      }}>
                        {t(accessLevel)}
                    </NavDropdown.Item>
                );
            }
            return (
                <NavDropdown id="accessLevels" title={t(currentAccessLevel)} alignRight style={{marginLeft: "1em"}}>
                    {accessLevels}
                </NavDropdown>
            );
        }
    };

    const authenticatedDropdownTitle = () => {
        return (
            <div className="d-inline">
                <FontAwesomeIcon icon={faUser}/> {userInfo.username}
            </div>
        );
    };

    const adminDropdown = () => {
        if (userInfo.currentAccessLevel === ACCESS_LEVEL_ADMIN) {
            return (
                <React.Fragment>
                    <LinkContainer to="/listAccounts">
                        <NavDropdown.Item>{t("breadcrumbs.listAccounts")}</NavDropdown.Item>
                    </LinkContainer>
                    <LinkContainer to="/addAccount">
                        <NavDropdown.Item>{t("breadcrumbs.addAccount")}</NavDropdown.Item>
                    </LinkContainer>
                </React.Fragment>
            );
        }
    };

    const employeeDropdown = () => {
        if (userInfo.currentAccessLevel === ACCESS_LEVEL_EMPLOYEE) {
            return (
                <React.Fragment>
                    <LinkContainer to="/addCar">
                        <NavDropdown.Item>{t("breadcrumbs.addCar")}</NavDropdown.Item>
                    </LinkContainer>
                    <LinkContainer to="/listReservations">
                        <NavDropdown.Item>{t("breadcrumbs.listReservations")}</NavDropdown.Item>
                    </LinkContainer>
                </React.Fragment>
            );
        }
    };

    const clientDropdown = () => {
        if (userInfo.currentAccessLevel === ACCESS_LEVEL_CLIENT) {
            return (
                <React.Fragment>
                    <LinkContainer to="/listOwnReservations">
                        <NavDropdown.Item>{t("breadcrumbs.listReservations")}</NavDropdown.Item>
                    </LinkContainer>
                </React.Fragment>
            );
        }
    };

    const renderNav = () => {
        if (isAuthenticated(userInfo)) {
            return (
                <Nav className="ml-auto">
                    {carList()}
                    {accessLevelsDropdown()}
                    <NavDropdown id="profile" title={authenticatedDropdownTitle()} alignRight style={{marginLeft: "1em"}}>
                        <LinkContainer to="/ownAccountDetails">
                            <NavDropdown.Item>{t("breadcrumbs.accountDetails")}</NavDropdown.Item>
                        </LinkContainer>
                        {adminDropdown()}
                        {employeeDropdown()}
                        {clientDropdown()}
                        <LinkContainer to="/#">
                            <NavDropdown.Item onClick={handleLogout}>{t("navbar.logout")}</NavDropdown.Item>
                        </LinkContainer>
                    </NavDropdown>
                </Nav>
            );
        } else {
            return (
                <Nav className="ml-auto">
                    {carList()}
                    <Nav.Item>
                        <LinkContainer to="/login">
                            <div className="nav-icon-item">
                                <FontAwesomeIcon icon={faSignInAlt}/> {t("navbar.login")}
                            </div>
                        </LinkContainer>
                    </Nav.Item>

                    <Nav.Item>
                        <LinkContainer to="/register">
                            <div className="nav-icon-item">
                                <FontAwesomeIcon icon={faUserPlus}/> {t("navbar.register")}
                            </div>
                        </LinkContainer>
                    </Nav.Item>
                </Nav>
            );
        }
    };

    if (!isAuthenticated(userInfo) && userInfo.tokenPresent) {
        return <Spinner/>;
    } else {
        return (
            <Navbar expand="lg" className="navbar-dark">
                <Navbar.Brand id="home" as={Link} to="/">Securental</Navbar.Brand>
                {/*<Navbar.Toggle id="toggle" aria-controls="basic-navbar-nav"/>*/}
                {/*<Navbar.Collapse id="basic-navbar-nav">*/}
                {renderNav()}
                {/*</Navbar.Collapse>*/}
            </Navbar>
        );
    }
};
