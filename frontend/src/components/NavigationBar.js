import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Nav, Navbar, NavDropdown } from 'react-bootstrap';
import "./styles/NavigationBar.css";

export default class NavigationBar extends Component {

    render() {
        return (
            <Navbar expand="lg" className="navbar-dark">
                <Navbar.Brand id="home" as={Link} to="/">Securental</Navbar.Brand>
                <Navbar.Toggle id="toggle" aria-controls="basic-navbar-nav"/>
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="ml-auto">
                        <NavDropdown id="dropdown" title="Placeholder">
                            <NavDropdown.Item>
                                <Nav.Link>
                                    <Link id="login" to="/login">Login</Link>
                                </Nav.Link>
                            </NavDropdown.Item>

                            <NavDropdown.Item>
                                <Nav.Link>
                                    <Link id="register" to="/register">Register</Link>
                                </Nav.Link>
                            </NavDropdown.Item>
                        </NavDropdown>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        );
    }
}
