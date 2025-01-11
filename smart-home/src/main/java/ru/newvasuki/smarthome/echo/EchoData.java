package ru.newvasuki.smarthome.echo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EchoData implements Serializable {
    private String name;
    private String ipAddress;
    private Integer port;
    private Integer type;
    private Long wakeUp;
    private List<EchoValue> values = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        if (ipAddress.startsWith("/")) ipAddress = ipAddress.substring(1);
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getWakeUp() {
        return wakeUp;
    }

    public void setWakeUp(Long wakeUp) {
        this.wakeUp = wakeUp;
    }

    public List<EchoValue> getValues() {
        return values;
    }

    public void setValues(List<EchoValue> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        String val = "[ " + String.join(", ", values.stream().map(v -> v.toString()).collect(Collectors.toList())) + " ]";
        return "{ " +
                "name='" + name + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", type=" + type +
                ", values=" + val +
                " }";
    }
}
