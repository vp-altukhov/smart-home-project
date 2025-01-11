import React, {useState, useEffect} from 'react';
import {Container, Button, ButtonGroup, Form, Row, Col, Table} from 'react-bootstrap'
import axios from "axios";
import DeviceValueCombo from "./DeviceValueCombo";

const Expression = ({id}) => {
    const [loading, setLoading] = useState(false);
    const [expressions, setExpressions] = useState([]);

    useEffect(() => {
        fetch(`/app/expressions/${id}`)
            .then(response => {
                if (!response.ok) throw new Error(response.status);
                return response.json();
            })
            .then(data => {
                setExpressions(data);
                setLoading(true);
            })
    }, [id]);

    const handleAdd = () => {
        let set = [...expressions];
        if (set.filter(i => i.id === 0).length > 0) return;
        set.push({id:0, profile: {id: id}, type: 'MORE_OR_EQUALS', deviceValue: {id: 0}, threshold: 0.0, operation: 'ON'})
        setExpressions(set);
    }

    const handleSave = (event) => {
        let sId = event.target.attributes.getNamedItem('expression').value;
        let val = [...expressions].filter(i => i.id === parseInt(sId))[0];
        const axUrl = `/app/expression/${id}`;
        const formData = new FormData();
        formData.append('type', val.type);
        formData.append('deviceValueId', val.deviceValue.id);
        formData.append('threshold', val.threshold);
        formData.append('operation', val.operation);
        const config = {
            requests: {
                'content-type': 'multipart/form-data',
            },
        };
        setLoading(false);
        axios.post(axUrl, formData, config).then((response) => {
            val.id = response.data.id;
            setLoading(true);
        });
    }

    const handleDeviceValueChange = (event) => {
        let sId = event.target.attributes.getNamedItem('expression').value;
        let val = [...expressions].filter(i => i.id === parseInt(sId))[0];
        val.deviceValue.id = parseInt(event.target.value);
        setExpressions([...expressions]);
    }

    const handleThresholdTypeChange = (event) => {
        let sId = event.target.attributes.getNamedItem('expression').value;
        let val = [...expressions].filter(i => i.id === parseInt(sId))[0];
        val.type = event.target.value;
        setExpressions([...expressions]);
    }

    const handleThresholdChange = (event) => {
        let sId = event.target.attributes.getNamedItem('expression').value;
        let val = [...expressions].filter(i => i.id === parseInt(sId))[0];
        val.threshold = event.target.value ? parseFloat(event.target.value) : 0.0;
        setExpressions([...expressions]);
    }

    const handleThresholdOperationChange = (event) => {
        let sId = event.target.attributes.getNamedItem('expression').value;
        let val = [...expressions].filter(i => i.id === parseInt(sId))[0];
        val.operation = event.target.value;
        setExpressions([...expressions]);
    }

    const handleRemove = (event) => {
        let sId = event.target.attributes.getNamedItem('expression').value;
        fetch(`/app/expression/${sId}`, {
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
                let set = [...expressions].filter(i => i.id !== parseInt(sId));
                setExpressions(set);
            })
    }

    const notValid = () => {
        let nv = false;
        expressions.forEach(val => {
            if (val.deviceValue.id === 0) nv = true;
        });
        return nv;
    }

    if (!loading) return (<p>Loading...</p>)

    const expressionList = expressions.map(set => {
        return <tr key={set.id}>
            <td>
                <DeviceValueCombo defaultValue={set.deviceValue.id} expression={set.id} onChange={handleDeviceValueChange}/>
            </td>
            <td>
                <Form.Select size="sm" name="thresholdType" defaultValue={set.type} expression={set.id} onChange={handleThresholdTypeChange}>
                    <option value="MORE_OR_EQUALS">Больше или равно</option>
                    <option value="LESS_OR_EQUALS">Меньше или равно</option>
                </Form.Select>
            </td>
            <td>
                <Form.Control size="sm" type="number" step="0.01" defaultValue={set.threshold} expression={set.id} onChange={handleThresholdChange} />
            </td>
            <td>
                <Form.Select size="sm" name="operation" defaultValue={set.operation} expression={set.id} onChange={handleThresholdOperationChange}>
                    <option value="ON">Включить</option>
                    <option value="OFF">Выключить</option>
                    <option value="VALUE">Получать значение</option>
                </Form.Select>
            </td>
            <td>
                <ButtonGroup size="sm">
                    <Button size="sm" variant="warning" style={{paddingTop:0, paddingBottom:0}} expression={set.id} onClick={handleSave} disabled={notValid()}>Сохранить</Button>
                    <Button size="sm" variant="danger" style={{paddingTop:0, paddingBottom:0}} expression={set.id} onClick={handleRemove} disabled={set.id === 0}>Удалить</Button>
                </ButtonGroup>
            </td>
        </tr>
    });

    const newExpression = expressions.filter(p => p.id === 0);

    return (
        <Container fluid className="border card">
            <Row style={{margin: 0, marginTop: 10}}>
                <Col></Col>
                <Col md="auto">
                    <ButtonGroup size="sm">
                        <Button size="sm" variant="success" style={{paddingTop:0, paddingBottom:0}} disabled={newExpression.length > 0} onClick={handleAdd}>Добавить выражение</Button>
                    </ButtonGroup>
                </Col>
            </Row>
            <Row style={{margin: 0}}>
                <Table striped hover size="sm">
                    <thead>
                    <tr>
                        <th>Датчик</th>
                        <th>Тип выражения</th>
                        <th>Порог</th>
                        <th>Операция</th>
                        <th>Действие</th>
                    </tr>
                    </thead>
                    <tbody>
                        {expressionList}
                    </tbody>
                </Table>
            </Row>
        </Container>
    )
}

export default Expression;