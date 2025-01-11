import React from 'react'
import { Container, Row, Col, Form, Button } from "react-bootstrap";

class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        this.innerRef = React.createRef();
    }

    componentDidMount() {
        setTimeout(() => {
            this.innerRef.current.focus();
        }, 1);
    }

    render() {
        return (
            <div>
                <Container>
                    <Row className="justify-content-md-center">
                        <Col md="auto">
                            <h1 style={{marginTop: 20}}>SmartHome</h1>
                            <Form action="perform_login" method="POST">
                                <Form.Group className="mb-3" controlId="username">
                                    <Form.Control ref={this.innerRef} type="text" name="username" placeholder="Логин" />
                                </Form.Group>
                                <Form.Group className="mb-3" controlId="password">
                                    <Form.Control type="password" name="password" placeholder="Пароль" />
                                </Form.Group>
                                <div className="d-grid gap-2">
                                    <Button variant="primary" type="submit" size="lg">
                                        Вход
                                    </Button>
                                </div>
                            </Form>
                        </Col>
                    </Row>
                </Container>
            </div>
        )
    }
}

export default LoginForm;