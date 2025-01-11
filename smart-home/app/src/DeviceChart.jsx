import React, {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import {Container, Row, Col, Form, Button, ButtonGroup} from 'react-bootstrap';
import { CartesianGrid, XAxis, YAxis, Tooltip, AreaChart, Area } from 'recharts';

const DeviceChart = () => {
    const [loading, setLoading] = useState(false);
    const [charts, setCharts] = useState([]);
    const [date, setDate] = useState();
    const {id} = useParams();

    const handleChangeDate = (event) => {
        setDate(event.target.value);
    }

    const handleRemove = () => {
        fetch(`/app/device/${id}`, {
            method: 'DELETE',
            requests: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(response => {
            if (!response.ok) throw new Error(response.status);
            return response.json();
        })
        .then(data => {
            window.location = "/";
        })
    }

    const handleRestart = () => {
        fetch(`/app/restart/${id}`).then(response => {
            if (!response.ok) throw new Error(response.status);
            return response.json();
        })
            .then(data => {
                window.location.reload();
            })
    }

    useEffect(() => {
        fetch(`/app/device-charts/${id}?date=${date}`)
            .then(response => {
                if (!response.ok) throw new Error(response.status);
                return response.json();
            })
            .then(data => {
                setCharts(data);
                setLoading(true);
            })
    }, [id, date]);

    if (!loading) return (<p>Loading...</p>)

    const chartList = charts.list.map(chart => {
        return <Col md="auto" key={chart.id}>
            <h5>{chart.name ? chart.name : chart.uid}</h5>
            <AreaChart width={700} height={250} data={chart.list}
                       margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                <defs>
                    <linearGradient id={"colorValue" + chart.id} x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#fffc33" stopOpacity={0.8}/>
                        <stop offset="95%" stopColor="#fffc33" stopOpacity={0}/>
                    </linearGradient>
                </defs>
                <XAxis dataKey="name" />
                <YAxis />
                <CartesianGrid strokeDasharray="3 3" />
                <Tooltip />
                <Area type="monotone" dataKey="value" stroke="#0000aa" fillOpacity={1} fill={"url(#colorValue" + chart.id + ")"} />
            </AreaChart>
        </Col>
    });

    return (
        <Container fluid>
            <Row style={{marginBottom:10}}>
                <Col md="auto">
                    <Form.Control type="date" onChange={handleChangeDate} defaultValue={charts.date} />
                </Col>
                <Col>
                    Периодичность: {charts.wakeUp}
                </Col>
                <Col md="auto">
                    <ButtonGroup>
                        <Button size="sm" variant="warning" style={{paddingTop:0, paddingBottom:0}} onClick={handleRestart}>Перезагрузить</Button>
                        <Button size="sm" variant="danger" style={{paddingTop:0, paddingBottom:0}} onClick={handleRemove}>Удалить</Button>
                    </ButtonGroup>
                </Col>
            </Row>
            <Row>
                {chartList}
            </Row>
        </Container>
    );
}

export default DeviceChart;