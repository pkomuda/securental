import React, { Component } from "react";
import axios from "axios";
import { Button, Form, FormControl, FormGroup, FormLabel } from "react-bootstrap";

export default class Login extends Component {

    constructor(props) {
        super(props);
        this.state = {
            authRequest: {"username": "", "combination": "", "characters": ""},
        };
    }

    handleChangeProperty = (event, property) => {
        let tempAuthRequest = {...this.state.authRequest};
        tempAuthRequest[property] = event.target.value;
        this.setState({authRequest: tempAuthRequest});
    };

    handleSubmit = () => {
        axios.post("/login", this.state.authRequest)
            .then(response => {
                alert(response.data["jwt"]);
            }).catch(error => {
                alert(error.response.data);
        });
    };

    render() {
        return (
            <div>
                <h1>Login</h1>
                <hr/>
                <Form>
                    <FormGroup>
                        <FormLabel>Username</FormLabel>
                        <FormControl id="username" value={this.state.authRequest["username"]} onChange={(event) => this.handleChangeProperty(event, "username")}/>
                    </FormGroup>

                    <FormGroup>
                        <FormLabel>Combination</FormLabel>
                        <FormControl id="combination" value={this.state.authRequest["combination"]} onChange={(event) => this.handleChangeProperty(event, "combination")}/>
                    </FormGroup>

                    <FormGroup>
                        <FormLabel>Characters</FormLabel>
                        <FormControl id="characters" value={this.state.authRequest["characters"]} onChange={(event) => this.handleChangeProperty(event, "characters")}/>
                    </FormGroup>
                    <hr/>
                    <Button id="submit" variant="dark" onClick={this.handleSubmit}>Submit</Button>
                </Form>
                <Button style={{"margin-top": "5px"}} id="back" variant="dark" onClick={this.props.history.goBack}>Back</Button>
            </div>
        )
    }
}
