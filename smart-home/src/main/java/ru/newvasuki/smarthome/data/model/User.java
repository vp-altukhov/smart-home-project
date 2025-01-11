package ru.newvasuki.smarthome.data.model;

import java.io.Serializable;
import java.security.Principal;

public class User implements Serializable {
    private Boolean authenticated;
    private String name;

    public User(Principal principal) {
        this.authenticated = principal != null;
        this.name = principal == null ? null : principal.getName();
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
