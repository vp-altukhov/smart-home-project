package ru.newvasuki.smarthome.echo;

import java.io.Serializable;

public class EchoValue implements Serializable {
    private String uid;
    private Double value;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{ " +
                "uid='" + uid + '\'' +
                ", value=" + value +
                " }";
    }
}
