import React from "react";
import { Carousel, Container } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { Jumbotron } from "./Jumbotron";

export const Home = () => {

    const {t} = useTranslation();

    return (
        <React.Fragment>
            <Jumbotron/>
            <Container>
                <Carousel style={{marginBottom: "2em"}}>
                    <Carousel.Item>
                        <img className="d-block w-100"
                             src="https://bi.im-g.pl/im/60/c8/17/z24937568Q,Opel-Corsa-2020.jpg"
                             alt="First slide"/>
                        <Carousel.Caption>
                            <h3>{t("First slide label")}</h3>
                            <p>Nulla vitae elit libero, a pharetra augue mollis interdum.</p>
                        </Carousel.Caption>
                    </Carousel.Item>
                    <Carousel.Item>
                        <img className="d-block w-100"
                             src="https://d-mf.ppstatic.pl/art/1q/n2/o1ovs2cgk80g440440go0/galeria01.1200.jpg"
                             alt="Second slide"/>
                        <Carousel.Caption>
                            <h3>Second slide label</h3>
                            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</p>
                        </Carousel.Caption>
                    </Carousel.Item>
                    <Carousel.Item>
                        <img className="d-block w-100"
                             src="https://www.bmw-frankcars.pl/www/media/mediapool/homepage_bmw5_limusine_lci2020.jpg"
                             alt="Third slide"/>
                        <Carousel.Caption>
                            <h3>Third slide label</h3>
                            <p>Praesent commodo cursus magna, vel scelerisque nisl consectetur.</p>
                        </Carousel.Caption>
                    </Carousel.Item>
                </Carousel>
            </Container>
        </React.Fragment>
    );
};
