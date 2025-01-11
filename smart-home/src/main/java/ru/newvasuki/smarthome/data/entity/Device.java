package ru.newvasuki.smarthome.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import ru.newvasuki.smarthome.data.type.DeviceType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Entity
@Table(schema = "public", name = "device", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "type", "address", "port"}))
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Device {
    @Id
    @EqualsAndHashCode.Include
    @SequenceGenerator(schema = "public", name="device_id_seq", sequenceName="device_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "device_id_seq")
    @Column(nullable = false)
    private Integer id;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String name;

    @EqualsAndHashCode.Include
    @Column(name = "type", nullable = false)
    private Integer type;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String address;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private Integer port;

    private String description;

    @Column(nullable = false, columnDefinition = "timestamp without time zone NOT NULL default now()")
    private LocalDateTime dateTime;

    @Column(nullable = false, columnDefinition = "boolean NOT NULL default false")
    private Boolean active;

    @JsonIgnore
    @OneToMany(targetEntity = Profile.class, mappedBy = "device", cascade = CascadeType.REMOVE)
    private List<Profile> profiles;

    @JsonIgnore
    @OneToMany(targetEntity = DeviceValue.class, mappedBy = "device", cascade = CascadeType.REMOVE)
    private List<DeviceValue> deviceValues;

    @Transient
    private String message;

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

    public DeviceType getType() {
        return DeviceType.get(type);
    }

    public void setType(DeviceType type) {
        this.type = type.getId();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public List<DeviceValue> getDeviceValues() {
        return deviceValues;
    }

    public void setDeviceValues(List<DeviceValue> deviceValues) {
        this.deviceValues = deviceValues;
    }

    public String getMessage() {
        if (Boolean.FALSE.equals(getActive())) {
            Date lastDate = Date.from(this.dateTime.atZone(ZoneId.systemDefault()).toInstant());
            Long period = System.currentTimeMillis() - lastDate.getTime();
            Long days = period / 86400000;
            Long hours = (period - (days * 86400000)) / 3600000;
            Long minutes = (period - (days * 86400000) - (hours * 3600000)) / 60000;
            Long seconds = (period - (days * 86400000) - (hours * 3600000) - (minutes * 60000)) / 1000;
            return String.format("Устройство не активно %d дн. %d час. %d мин. %d сек.", days, hours, minutes, seconds);
        }
        return "Устройство активно";
    }

    @Override
    public String toString() {
        return "Device{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}
