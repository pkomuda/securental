import React from "react";
import { Carousel, Container } from "react-bootstrap";
import { Jumbotron } from "./Jumbotron";

export const Home = () => {

    const carouselStyle = {
        margin: "0 auto",
        maxWidth: "1280px",
        height: "720px"
    };

    return (
        <React.Fragment>
            <Jumbotron/>
            <Container>
                <Carousel style={{marginBottom: "2em"}}>
                    <Carousel.Item>
                        <img className="d-block w-100"
                             src="https://securental.s3.eu-central-1.amazonaws.com/audi.jpg"
                             alt="audi"
                             style={carouselStyle}/>
                    </Carousel.Item>
                    <Carousel.Item>
                        <img className="d-block w-100"
                             src="https://securental.s3.eu-central-1.amazonaws.com/ford.jpg"
                             alt="ford"
                             style={carouselStyle}/>
                    </Carousel.Item>
                    <Carousel.Item>
                        <img className="d-block w-100"
                             src="https://securental.s3.eu-central-1.amazonaws.com/tesla.jpg"
                             alt="tesla"
                             style={carouselStyle}/>
                    </Carousel.Item>
                </Carousel>
            </Container>
        </React.Fragment>
    );
};
