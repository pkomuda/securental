import { Component } from "react";

export default class ErrorBoundary extends Component {

    componentDidCatch(error, errorInfo) {
        window.location.replace("/error");
    }

    render() {
        return this.props.children;
    }
}
