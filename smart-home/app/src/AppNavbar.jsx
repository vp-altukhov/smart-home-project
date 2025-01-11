import React, { useState } from "react";
import { Navbar, Nav, Button, Offcanvas } from 'react-bootstrap'
import logo from './images/logo64.png'
import {BsList} from 'react-icons/bs'
import DeviceNavbar from "./DeviceNavbar";

const AppNavbar = () => {
    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    return (
        <>
            <Navbar bg="dark" variant="dark">
                <Navbar.Brand href="/">
                    <img style={{marginLeft: 10, marginRight: 10}} src={logo} className="App-logo" alt="logo" />
                    Smart Home
                </Navbar.Brand>
                <Nav className="me-auto" />
                <Button variant="dark" onClick={handleShow}>
                    <BsList size="50" />
                </Button>
            </Navbar>
            <Offcanvas placement="end" show={show} onHide={handleClose} {...{backdrop:true, scroll:false}}>
                <Offcanvas.Header closeButton>
                    <Offcanvas.Title>Устройства</Offcanvas.Title>
                </Offcanvas.Header>
                <Offcanvas.Body>
                    <DeviceNavbar />
                </Offcanvas.Body>
            </Offcanvas>
        </>
    );
}

export default AppNavbar;