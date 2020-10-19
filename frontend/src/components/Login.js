import React, { Component } from "react";
import axios from "axios";
import { Button, Form, FormControl, FormGroup } from "react-bootstrap";

export default class Login extends Component {

    constructor(props) {
        super(props);
        this.state = {
            authRequest: {"username": "", "combination": [], "characters": [], "totpCode": ""},
            stage: 1
        };
    }

    handleChangeProperty = (event, property) => {
        let tempAuthRequest = {...this.state.authRequest};
        tempAuthRequest[property] = event.target.value;
        this.setState({authRequest: tempAuthRequest});
    };

    handleFirstStage = () => {
        axios.get("/initializeLogin/" + this.state.username)
            .then(response => {
                let tempAuthRequest = {...this.state.authRequest};
                tempAuthRequest["combination"] = response.data;
                this.setState({authRequest: tempAuthRequest, stage: 2});
            }).catch(error => {
                alert(error.response.data);
        });
    };

    handleSecondStage = () => {
        let chars = [];
        for (let i = 0; i < this.state.authRequest.combination.length - 1; i++) {
            chars[i] = i + " ";
        }
        let tempAuthRequest = {...this.state.authRequest};
        tempAuthRequest["characters"] = chars;
        this.setState({authRequest: tempAuthRequest, stage: 3});
    };

    handleThirdStage = () => {
        axios.post("/login", this.state.authRequest)
            .then(response => {
                alert(response.data);
            }).catch(error => {
            alert(error.response.data);
        });
    };

    renderFirstStage = () => {
        if (this.state.stage === 1) {
            return (
                <div>
                    <h2 style={{textAlign: "center"}}>Username</h2>
                    <Form style={{textAlign: "center"}}>
                        <FormGroup>
                            <FormControl id="username" value={this.state.authRequest["username"]} onChange={(event) => this.handleChangeProperty(event, "username")} style={{width: "20%", display: "inline-block"}}/>
                        </FormGroup>
                        <Button id="submit1" variant="dark" onClick={this.handleFirstStage}>Next</Button>
                    </Form>
                </div>
            );
        }
    };

    renderSecondStage = () => {
        if (this.state.stage === 2) {
            let boxes = [];
            for (let i = 0; i < this.state.authRequest.combination[this.state.authRequest.combination.length - 1] + 1; i++) {
                if (this.state.authRequest.combination.includes(i)) {
                    boxes.push(
                        <div key={i} style={{display: "inline-block", position: "relative", marginBottom: "1em"}}>
                            <FormControl style={{width: "3em", marginRight: "1em"}} id={"textbox" + i} value={this.state.authRequest.characters[i]}
                                         onChange={(event) => this.handleChangeProperty(event, "characters")}
                                         disabled={false}/>
                            <p style={{position: "absolute", marginLeft: "30%"}}>{i + 1}</p>
                        </div>
                    );
                } else {
                    boxes.push(
                        <div key={i} style={{display: "inline-block", position: "relative", marginBottom: "1em"}}>
                            <FormControl style={{width: "3em", marginRight: "1em"}} id={"textbox" + i} onChange={(event) => this.handleChangeProperty(event, "characters")}
                                         disabled={true}/>
                            <p style={{position: "absolute", marginLeft: "30%"}}>{i + 1}</p>
                        </div>
                    );
                }
            }
            return (
                <div>
                    <h2 style={{textAlign: "center"}}>Password characters</h2>
                    <Form style={{textAlign: "center"}}>
                        {boxes}
                        <br/>
                        <br/>
                        <Button id="submit2" variant="dark" onClick={this.handleSecondStage}>Next</Button>
                    </Form>
                </div>
            );
        }
    }

    renderThirdStage = () => {
        if (this.state.stage === 3) {
            return (
                <div>
                    <h2 style={{textAlign: "center"}}>Code</h2>
                    <Form style={{textAlign: "center"}}>
                        <FormGroup>
                            <FormControl id="code" style={{width: "20%", display: "inline-block"}}/>
                        </FormGroup>
                        <Button id="submit3" variant="dark" onClick={this.handleThirdStage}>Submit</Button>
                    </Form>
                </div>
            );
        }
    };

    render() {
        return (
            <div>
                <h1 style={{textAlign: "center"}}>Login</h1>
                {this.renderFirstStage()}
                {this.renderSecondStage()}
                {this.renderThirdStage()}
                <Button style={{marginTop: "5px"}} id="back" variant="dark" onClick={this.props.history.goBack}>Back</Button>
            </div>
        )
    }
}
