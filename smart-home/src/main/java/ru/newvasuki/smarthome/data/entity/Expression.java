package ru.newvasuki.smarthome.data.entity;

import lombok.EqualsAndHashCode;
import ru.newvasuki.smarthome.data.type.ExpressionType;
import ru.newvasuki.smarthome.data.type.OperationType;

import javax.persistence.*;

@Entity
@Table(schema = "public", name = "expression", uniqueConstraints = @UniqueConstraint(columnNames = {"device_value_id", "profile_id", "operation"}))
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Expression {
    @Id
    @EqualsAndHashCode.Include
    @SequenceGenerator(schema = "public", name="settings_id_seq", sequenceName="settings_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "settings_id_seq")
    @Column(nullable = false)
    private Integer id;

    @EqualsAndHashCode.Include
    @ManyToOne(optional = false)
    private Profile profile;

    @Column(nullable = false)
    private Integer type;

    @EqualsAndHashCode.Include
    @ManyToOne(optional = false)
    private DeviceValue deviceValue;

    @Column(nullable = false)
    private Double threshold;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private Integer operation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public ExpressionType getType() {
        return ExpressionType.get(type);
    }

    public void setType(ExpressionType type) {
        this.type = type.getId();
    }

    public DeviceValue getDeviceValue() {
        return deviceValue;
    }

    public void setDeviceValue(DeviceValue deviceValue) {
        this.deviceValue = deviceValue;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public OperationType getOperation() {
        return OperationType.get(operation);
    }

    public void setOperation(OperationType operation) {
        this.operation = operation.getId();
    }
}
