import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import {Nav, Navbar } from "react-bootstrap";
import { BsSpeedometer, BsHouseGear, BsQuestionCircle, BsHeartPulse, BsHeartbreak } from 'react-icons/bs';

const DeviceNavbar = () => {
    const [devices, setDevices] = useState([]);
    const [loading, setLoading] = useState(false);
    const {id} = useParams();

    useEffect(() => {
        let origin = window.location.origin;
        fetch(`${origin}/app/devices`)
            .then(response => {
                if (!response.ok) throw new Error(response.status);
                return response.json();
            })
            .then(data => {
                setDevices(data);
                setLoading(true);
            })
    }, []);

    const deviceType = (device) => {
        let icon;
        const size = 30;
        switch (device.type) {
            case 'SENSOR': icon = <BsSpeedometer size={size} style={device.id === parseInt(id) ? {color:'blue'} : {}} />;
                break;
            case 'EXECUTIVE': icon = <BsHouseGear size={size} style={device.id === parseInt(id) ? {color:'blue'} : {}} />;
                break;
            default: icon = <BsQuestionCircle size={size} />;
        }
        return icon;
    }

    const deviceList = devices.map(device => {
        return <div key={device.id} className="d-flex align-items-center">
            <Nav.Link href={"/device/" + device.id}>{deviceType(device)}{' '}{device.active ? <BsHeartPulse size={15} color={"green"}/> : <BsHeartbreak size={15} color={"red"} />}{' '}{device.description ? device.description : device.name}</Nav.Link>
        </div>
    });

    return !loading ? (
        <p>Loading...</p>
    ) : (
        <Navbar>
            <Nav className="flex-column">
                {deviceList}
            </Nav>
        </Navbar>
    );
}

export default DeviceNavbar;