import React, { useState } from "react";
import { Button, FormControl, FormGroup } from "react-bootstrap";
import { useTranslation } from "react-i18next";

export const Home = () => {

    const {t} = useTranslation();
    const [text, setText] = useState("");

    return (
        <React.Fragment>
            <FormGroup>
                <FormControl value={text}
                             onChange={event => setText(event.target.value)}/>
            </FormGroup>
            <Button onClick={() => console.log(text)}>{t("navigation.submit")}</Button>
        </React.Fragment>
    );
};
