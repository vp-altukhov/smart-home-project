package ru.newvasuki.smarthome.data.entity;

import lombok.EqualsAndHashCode;
import ru.newvasuki.smarthome.data.service.ValueService;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(schema = "public", name = "device_value", uniqueConstraints = @UniqueConstraint(columnNames = {"uid", "device_id"}))
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DeviceValue {
    @Id
    @EqualsAndHashCode.Include
    @SequenceGenerator(schema = "public", name="device_value_id_seq", sequenceName="device_value_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "device_value_id_seq")
    @Column(nullable = false)
    private Integer id;

    @EqualsAndHashCode.Include
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Device device;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String uid;

    @Transient
    private List<Value> values;

    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Value> getValues(LocalDateTime dateTime, ValueService valueService) {
        return valueService.findAllByDateTimeBetweenAndUid(dateTime, dateTime.plusHours(24),this.uid);
    }
}
