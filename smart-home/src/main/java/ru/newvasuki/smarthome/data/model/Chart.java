package ru.newvasuki.smarthome.data.model;

import ru.newvasuki.smarthome.data.entity.DeviceValue;

import java.io.Serializable;
import java.util.List;

public class Chart implements Serializable {
    private Integer id;
    private String name;
    private String uid;
    private List<ChartList> list;

    public Chart(DeviceValue deviceValue, List<ChartList> list) {
        this.id = deviceValue.getId();
        this.name = deviceValue.getDescription();
        this.uid = deviceValue.getUid();
        this.list = list;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<ChartList> getList() {
        return list;
    }

    public void setList(List<ChartList> list) {
        this.list = list;
    }
}
