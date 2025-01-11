package ru.newvasuki.smarthome.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("smart-home")
public class SmartHomeConfigurationProperties {
    private Integer broadcastPort = 8761;
    private String token;
    private String botUsername;
    private List<Long> accessIsAllowed = new ArrayList<>();

    public Integer getBroadcastPort() {
        return broadcastPort;
    }

    public void setBroadcastPort(Integer broadcastPort) {
        this.broadcastPort = broadcastPort;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    public List<Long> getAccessIsAllowed() {
        return accessIsAllowed;
    }

    public void setAccessIsAllowed(List<Long> accessIsAllowed) {
        this.accessIsAllowed = accessIsAllowed;
    }
}
