package ru.newvasuki.smarthome.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(schema = "public", name = "profile", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "device_id"}))
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Profile {
    @Id
    @EqualsAndHashCode.Include
    @SequenceGenerator(schema = "public", name="profile_id_seq", sequenceName="profile_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "profile_id_seq")
    @Column(nullable = false)
    private Integer id;

    @EqualsAndHashCode.Include
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Device device;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "boolean NOT NULL default false")
    private Boolean enable = false;

    @JsonIgnore
    @OneToMany(targetEntity = Expression.class, mappedBy = "profile", cascade = CascadeType.REMOVE)
    private List<Expression> expressions;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }
}
