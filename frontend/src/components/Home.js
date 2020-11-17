import React from "react";
import { Form, FormControl, FormGroup, FormLabel } from "react-bootstrap";

export const Home = () => {

    return (
        <div>
            <h1>Home</h1>
            <Form>
                <FormGroup>
                    <FormLabel>Read only</FormLabel>
                    <FormControl readOnly value={"HeRb"}/>
                </FormGroup>
                <FormGroup>
                    <FormLabel>Disabled</FormLabel>
                    <FormControl disabled value={"HeRb"}/>
                </FormGroup>
            </Form>
        </div>
    );
};
