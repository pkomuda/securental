import React, { useContext } from "react";
import { Nav, Navbar, NavDropdown } from "react-bootstrap";
import { LinkContainer } from "react-router-bootstrap";
import { Link } from "react-router-dom";
import { AuthenticationContext } from "../utils/AuthenticationContext";

export const NavigationBar = () => {

    const {user, setUser} = useContext(AuthenticationContext);

    return (
        <Navbar expand="lg" className="navbar-dark">
            <Navbar.Brand id="home" as={Link} to="/">Securental</Navbar.Brand>
            <Navbar.Toggle id="toggle" aria-controls="basic-navbar-nav"/>
            <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="ml-auto">
                    <NavDropdown id="dropdown" title={user.username} alignRight>
                        <LinkContainer to="/login">
                            <NavDropdown.Item>Login</NavDropdown.Item>
                        </LinkContainer>

                        <LinkContainer to="/register">
                            <NavDropdown.Item>Register</NavDropdown.Item>
                        </LinkContainer>
                    </NavDropdown>
                </Nav>
            </Navbar.Collapse>
        </Navbar>
    );
};
