import React from 'react';
import {Container, Row, Col} from 'react-bootstrap';
import AppNavbar from "./AppNavbar";
import logo from './images/logo192.png'

const Home = () => {
    return (
        <div>
            <AppNavbar />
            <Container fluid>
                <Row className="justify-content-md-center">
                    <Col md="auto" style={{marginTop:20}}>
                        <img src={logo} className="App-logo-big" alt="logo" />
                    </Col>
                </Row>
                <Row className="justify-content-md-center">
                    <Col md="auto" style={{marginTop:20}}>
                        <h1>Smart Home 2023 (c)</h1>
                    </Col>
                </Row>
            </Container>
        </div>
    );
}

export default Home;