import React from "react";
import { Button, Container } from "react-bootstrap";
import { useTranslation } from "react-i18next/src";
import { Jumbotron } from "./Jumbotron";

export const Home = props => {

    const {t} = useTranslation();

    return (
        <React.Fragment>
            <Jumbotron/>
            <Container>
                <h1>Lorem ipsum</h1>
                <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque in orci et tellus ullamcorper hendrerit vitae at felis. Nullam placerat venenatis eros, at mollis justo ultrices quis. Donec in finibus risus. Nullam facilisis diam sed lectus laoreet, sed convallis nibh fermentum. Integer non tincidunt nibh. Sed facilisis porta congue. Etiam eu posuere sapien, eget fringilla diam. Cras mattis varius est. Donec efficitur lobortis nibh non ultrices. Fusce ut mattis dui, eu placerat libero. Maecenas euismod risus id ex dictum, in cursus dolor pellentesque. Proin mollis pellentesque dolor, vitae semper felis consequat non. Maecenas feugiat elit sodales iaculis commodo. Donec turpis lorem, tristique ac mollis tempus, maximus quis sem.</p>
                <p>Sed vel orci a orci volutpat sodales. Curabitur egestas mauris nec ante scelerisque, nec facilisis mi pharetra. Aenean vel felis ac odio malesuada bibendum nec a lectus. Nulla in sodales ex, non vestibulum elit. Proin auctor, lectus id dictum sodales, urna arcu rhoncus mauris, et posuere urna tortor vehicula eros. Vestibulum semper nulla sed gravida venenatis. Praesent non sem eu risus vehicula vulputate pellentesque vitae eros. Mauris ut congue mauris. In ut ultricies ligula, ac sagittis metus. Etiam facilisis vel est sit amet facilisis.</p>
                <p>Donec orci sapien, rutrum nec risus quis, consequat commodo purus. Donec scelerisque tellus tellus, eget dignissim nisl gravida ut. Nulla vel diam ex. Nunc accumsan posuere elit sit amet aliquam. Ut ultrices sollicitudin nibh at pulvinar. Praesent tempor libero eu nunc dignissim, sed mattis elit faucibus. Aenean eu viverra odio, sit amet mattis urna. Aliquam erat volutpat. Etiam vel risus libero. Fusce arcu neque, vulputate ut vulputate eget, efficitur sit amet est. Ut malesuada nisi ac sapien maximus, quis dictum ex sagittis. Nullam vehicula elit quis lorem gravida tempus. Sed vitae nisl eu magna porttitor lobortis. Sed nec augue eu leo ultrices molestie sit amet eu augue. In imperdiet varius ante a porta.</p>
                <p>Praesent viverra iaculis nunc eget interdum. Cras et mi maximus, feugiat turpis vel, feugiat arcu. Duis laoreet congue hendrerit. Suspendisse eleifend congue turpis, id aliquet nibh semper a. Maecenas tempor magna quis tincidunt blandit. Quisque tristique quam sed lorem cursus egestas. Nam viverra euismod lectus efficitur iaculis.</p>
                <p>Aliquam facilisis leo in enim venenatis, sed iaculis leo bibendum. Donec ut massa et leo ornare dictum ac sed dui. Fusce ac dignissim nisi. Nulla ex arcu, malesuada in tortor sed, accumsan ornare lectus. Ut quis consequat quam. Nunc dictum tincidunt viverra. Curabitur a egestas ante, a porta ante. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Pellentesque eget posuere metus, eu interdum felis. Sed ac diam pharetra, luctus risus in, auctor risus.</p>
                <Button id="cars"
                        onClick={() => props.history.push("/listCars")}>{t("navigation.cars")}</Button>
            </Container>
        </React.Fragment>
    );
};
