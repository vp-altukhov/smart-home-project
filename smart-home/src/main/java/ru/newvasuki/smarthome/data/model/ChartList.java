package ru.newvasuki.smarthome.data.model;

import ru.newvasuki.smarthome.data.entity.Value;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class ChartList implements Serializable {
    private String name;
    private Double value;

    public ChartList(Value value) {
        this.name = value.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.value = value.getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
