import { faSignInAlt, faUser, faUserPlus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useContext } from "react";
import { Nav, Navbar, NavDropdown } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { LinkContainer } from "react-router-bootstrap";
import { Link, useHistory } from "react-router-dom";
import { AuthenticationContext, isAuthenticated } from "../utils/AuthenticationContext";

export const NavigationBar = props => {

    const {t} = useTranslation();
    const history = useHistory();
    const [userInfo, setUserInfo] = useContext(AuthenticationContext);

    if (isAuthenticated(userInfo)) {
        setTimeout(() => {
            // console.log(`Token expired at ${new Date()}`);
            setUserInfo({
                username: "",
                accessLevels: [],
                // currentAccessLevel: "",
                tokenExpiration: 0
            });
            history.push("/login/sessionExpired");
        }, userInfo.tokenExpiration - new Date().getTime());
    }

    const authenticatedDropdownTitle = () => {
        return (
            <div className="d-inline">
                <FontAwesomeIcon icon={faUser}/> {userInfo.username}
            </div>
        );
    };

    const renderNav = () => {
        if (isAuthenticated(userInfo)) {
            return (
                <Nav className="ml-auto">
                    <NavDropdown id="dropdown" title={authenticatedDropdownTitle()} alignRight>
                        <LinkContainer to="/addCar">
                            <NavDropdown.Item>Add car</NavDropdown.Item>
                        </LinkContainer>

                        <LinkContainer to="/listCars">
                            <NavDropdown.Item>Car list</NavDropdown.Item>
                        </LinkContainer>
                    </NavDropdown>
                </Nav>
            );
        } else {
            return (
                <Nav className="ml-auto">
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

    return (
        <Navbar expand="lg" className="navbar-dark">
            <Navbar.Brand id="home" as={Link} to="/">Securental</Navbar.Brand>
            <Navbar.Toggle id="toggle" aria-controls="basic-navbar-nav"/>
            <Navbar.Collapse id="basic-navbar-nav">
                {renderNav()}
            </Navbar.Collapse>
        </Navbar>
    );
};
