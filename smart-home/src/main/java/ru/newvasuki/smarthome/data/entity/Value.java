package ru.newvasuki.smarthome.data.entity;

import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "value")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Value {
    @Id
    @EqualsAndHashCode.Include
    @SequenceGenerator(schema = "public", name="value_id_seq", sequenceName="value_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "value_id_seq")
    @Column(nullable = false)
    private Integer id;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String uid;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private Double val;

    @EqualsAndHashCode.Include
    @Column(nullable = false, columnDefinition = "timestamp without time zone NOT NULL default now()")
    private LocalDateTime dateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Double getValue() {
        return val;
    }

    public void setValue(Double value) {
        this.val = value;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
