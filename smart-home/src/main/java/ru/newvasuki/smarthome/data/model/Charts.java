package ru.newvasuki.smarthome.data.model;

import java.util.List;

public class Charts {
    private String date;
    private List<Chart> list;

    public Charts(String date, List<Chart> list) {
        this.date = date;
        this.list = list;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Chart> getList() {
        return list;
    }

    public void setList(List<Chart> list) {
        this.list = list;
    }
}
