package ru.newvasuki.smarthome.data.service;

import org.springframework.stereotype.Service;
import ru.newvasuki.smarthome.data.entity.Value;
import ru.newvasuki.smarthome.data.repository.ValueRepository;
import ru.newvasuki.smarthome.echo.EchoValue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class ValueService {
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;
    private ValueRepository repository;

    public ValueService(ValueRepository repository) {
        this.repository = repository;
    }

    public List<Value> getValuesByUid(String uid) {
        return this.repository.findAllByUid(uid);
    }

    public List<Value> findAllByDateTimeAfterAndUid(LocalDateTime dateTime, String uid) {
        return this.repository.findAllByDateTimeAfterAndUid(dateTime, uid);
    }

    public Value getLastByUid(String uid) {
        return this.repository.getLastByUid(uid);
    }

    public List<Value> findAllByDateTimeBetweenAndUid(LocalDateTime startTime, LocalDateTime endTime, String uid) {
        return repository.findAllByDateTimeBetweenAndUid(startTime, endTime, uid);
    }

    public Value registerValue(EchoValue echoValue) {
        Value entity = new Value();
        entity.setUid(echoValue.getUid());
        entity.setValue(echoValue.getValue());
        entity.setDateTime(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()));
        return update(entity);
    }

    public Value update(Value entity) {
        return this.repository.save(entity);
    }

    public void remove(Value entity) {
        this.repository.delete(entity);
    }
}
