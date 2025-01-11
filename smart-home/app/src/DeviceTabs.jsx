import React, {useState, useEffect} from 'react';
import AppNavbar from "./AppNavbar";
import {useParams} from 'react-router-dom';
import {Container, Tabs, Tab, Row, Col} from 'react-bootstrap';
import {BsHeartPulse, BsHeartbreak} from 'react-icons/bs';
import DeviceValue from "./DeviceValue";
import DeviceChart from "./DeviceChart";
import Profiles from "./Profiles";

const DeviceTabs = () => {
    const [loading, setLoading] = useState(false);
    const [device, setDevice] = useState(false);
    const {id} = useParams();

    useEffect(() => {
        fetch(`/app/device/${id}`)
            .then(response => {
                if (!response.ok) throw new Error(response.status);
                return response.json();
            })
            .then(data => {
                setDevice(data);
                setLoading(true);
            })
    }, [id]);

    return !loading ? (
        <p>Loading...</p>
    ) : (
        <div>
            <AppNavbar />
            <Container fluid>
                <Row style={{marginTop:10}}>
                    <Col>
                        <h3>{device.description ? device.description : device.name}</h3>
                    </Col>
                    <Col md="auto">
                        {device.active ?
                            <BsHeartPulse size={30} color={"green"} /> :
                            <BsHeartbreak size={30} color={"red"} />
                        }
                        <span>{device.active ? '' : ' ' + device.message}</span>
                    </Col>
                </Row>
                <Tabs
                    defaultActiveKey="chartLists"
                    id="device-tabs"
                    className="mb-3"
                >
                    <Tab eventKey="chartLists" title="Графики">
                        <DeviceChart />
                    </Tab>
                    <Tab eventKey="names" title="Описания">
                        <DeviceValue />
                    </Tab>
                    <Tab eventKey="profiles" title="Профили">
                        <Profiles />
                    </Tab>
                </Tabs>
            </Container>
        </div>
    );
}

export default DeviceTabs;