import React, {useEffect, useState} from 'react';
import {Form} from "react-bootstrap";

const DeviceValueCombo = (args) => {
    const [loading, setLoading] = useState(false);
    const [values, setValues] = useState([]);

    useEffect(() => {
        fetch(`/app/device-values/0`)
            .then(response => {
                if (!response.ok) throw new Error(response.status);
                return response.json();
            })
            .then(data => {
                setValues(data);
                setLoading(true);
            })
    }, []);

    const valueList = values.map(value => {
        return <option key={value.id} value={value.id}>
            {value.device.description ? value.device.description : value.device.name}->{value.description ? value.description : value.uid}
        </option>
    });

    return !loading ? (
        <p>Loading...</p>
    ) : (
        <Form.Select size="sm" name="deviceValueCombo" defaultValue={args.defaultValue} expression={args.expression} onChange={args.onChange}>
            <option value={0}>Не выбрано</option>
            {valueList}
        </Form.Select>
    );
}

export default DeviceValueCombo;