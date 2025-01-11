import React, {useState, useEffect} from 'react';
import {Container, Button, ButtonGroup, Form, Row, Col, Accordion} from 'react-bootstrap'
import {useParams} from "react-router-dom";
import axios from "axios";
import Expression from "./Expression";

const Profile = () => {
    const [loading, setLoading] = useState(false);
    const [profiles, setProfiles] = useState([]);
    const {id} = useParams();

    useEffect(() => {
        fetch(`/app/profiles/${id}`)
            .then(response => {
                if (!response.ok) throw new Error(response.status);
                return response.json();
            })
            .then(data => {
                setProfiles(data);
                setLoading(true);
            })
    }, [id]);

    const handleEnabledChange = (event) => {
        let sId = event.target.attributes.getNamedItem('profile').value;
        if (event.target.checked === true) [...profiles].forEach(p => p.enable = false);
        let val = [...profiles].filter(i => i.id === parseInt(sId))[0];
        val.enable = event.target.checked;
        handleSave(null, sId);
    }

    const handleNameChange = (event) => {
        let vId = event.target.attributes.getNamedItem('profile').value;
        let val = [...profiles].filter(i => i.id === parseInt(vId))[0];
        val.name = event.target.value;
    }

    const handleAdd = () => {
        let set = [...profiles];
        if (set.filter(i => i.id === 0).length > 0) return;
        set.push({id: 0, device: id, enable: false, name: 'Новый профиль'})
        setProfiles(set);
    }

    const handleRemove = (event) => {
        let sId = event.target.attributes.getNamedItem('profile').value;
        fetch(`/app/profile/${sId}`, {
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
                let set = [...profiles].filter(i => i.id !== parseInt(sId));
                setProfiles(set);
            })
    }

    const handleSave = (event, sId) => {
        let vId = event ? event.target.attributes.getNamedItem('profile').value : sId;
        let val = [...profiles].filter(i => i.id === parseInt(vId))[0];
        const axUrl = `/app/profile/${id}`;
        const formData = new FormData();
        formData.append('enabled', val.enable);
        formData.append('name', val.name);
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

    if (!loading) return (<p>Loading...</p>)

    const profileList = profiles.map(set => {
        return (
            <Accordion.Item eventKey={set.id} key={set.id}>
                <Accordion.Header>
                    <Row>
                        <Col md="auto">
                            <Form.Check type="switch" defaultChecked={set.enable} disabled={set.id === 0} profile={set.id} onChange={handleEnabledChange}/>
                        </Col>
                        <Col>
                            <strong>{set.name}</strong>
                        </Col>
                    </Row>
                </Accordion.Header>
                <Accordion.Body>
                    <Row style={{margin: 0, marginBottom: 10}}>
                        <Col>
                            <Form.Control size="sm" type="text" defaultValue={set.name} profile={set.id} onChange={handleNameChange} />
                        </Col>
                    </Row>
                    {
                        set.id !== 0 ? (
                            <Expression id={set.id} />
                        ) : (null)
                    }
                    <Row style={{margin: 0, marginTop: 10}}>
                        <Col></Col>
                        <Col md="auto">
                            <ButtonGroup size="sm">
                                <Button size="sm" variant="warning" style={{paddingTop:0, paddingBottom:0}} profile={set.id} onClick={handleSave}>Сохранить профиль</Button>
                                <Button size="sm" variant="danger" style={{paddingTop:0, paddingBottom:0}} profile={set.id} onClick={handleRemove} disabled={set.id === 0}>Удалить профиль</Button>
                            </ButtonGroup>
                        </Col>
                    </Row>
                </Accordion.Body>
            </Accordion.Item>
        )
    });

    const newProfile = profiles.filter(p => p.id === 0);

    return (
        <Container fluid>
            <Row style={{marginBottom: 20}}>
                <Col></Col>
                <Col md="auto">
                    <ButtonGroup size="sm">
                        <Button size="sm" variant="success" style={{paddingTop:0, paddingBottom:0}} disabled={newProfile.length > 0} onClick={handleAdd}>Добавить профиль</Button>
                    </ButtonGroup>
                </Col>
            </Row>
            <Accordion>
                {profileList}
            </Accordion>
        </Container>
    )
}

export default Profile;