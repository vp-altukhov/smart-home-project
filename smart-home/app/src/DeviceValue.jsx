import React, {useEffect, useState} from 'react';
import {useParams} from "react-router-dom";
import {Table, ButtonGroup, Button, Form} from 'react-bootstrap';
import axios from 'axios';

const DeviceValue = () => {
    const [loading, setLoading] = useState(false);
    const [values, setValues] = useState([]);
    const {id} = useParams();

    const handleDescriptionChange = (event) => {
        let vId = event.target.attributes.getNamedItem('id').value;
        let val = [...values].filter(i => i.id === parseInt(vId))[0];
        val.description = event.target.value;
    }

    const handleEditSubmit = (event) => {
        let vId = event.target.attributes.getNamedItem('id').value;
        let val = [...values].filter(i => i.id === parseInt(vId))[0];
        event.preventDefault()
        const axUrl = `/app/device-values/${vId}`;
        const formData = new FormData();
        formData.append('description', val.description);
        const config = {
            requests: {
                'content-type': 'multipart/form-data',
            },
        };
        axios.post(axUrl, formData, config).then((response) => {
        });
    }

    useEffect(() => {
        fetch(`/app/device-values/${id}`)
            .then(response => {
                if (!response.ok) throw new Error(response.status);
                return response.json();
            })
            .then(data => {
                setValues(data);
                setLoading(true);
            })
    }, [id]);

    const valueList = values.map(value => {
        return <tr key={value.id}>
            <td>{value.uid}</td>
            <td>
                <Form.Control size="sm" type="text" defaultValue={value.description} id={value.id} onChange={handleDescriptionChange} />
            </td>
            <td>
                <ButtonGroup size="sm">
                    <Button size="sm" variant="success" style={{paddingTop:0, paddingBottom:0}} id={value.id} onClick={handleEditSubmit}>Сохранить</Button>
                </ButtonGroup>
            </td>
        </tr>
    });

    return !loading ? (
        <p>Loading...</p>
    ) : (
        <Table striped hover size="sm">
            <thead>
            <tr>
                <th>UID</th>
                <th>Описание</th>
                <th width="5%">Действие</th>
            </tr>
            </thead>
            <tbody>
            {valueList}
            </tbody>
        </Table>
    );
}

export default DeviceValue;