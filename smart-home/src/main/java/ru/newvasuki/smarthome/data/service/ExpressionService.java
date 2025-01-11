package ru.newvasuki.smarthome.data.service;

import org.springframework.stereotype.Service;
import ru.newvasuki.smarthome.data.entity.Device;
import ru.newvasuki.smarthome.data.entity.DeviceValue;
import ru.newvasuki.smarthome.data.entity.Expression;
import ru.newvasuki.smarthome.data.entity.Profile;
import ru.newvasuki.smarthome.data.repository.ExpressionRepository;
import ru.newvasuki.smarthome.data.type.OperationType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.util.List;
import java.util.Optional;

@Service
public class ExpressionService {
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;
    private ExpressionRepository repository;

    public ExpressionService(ExpressionRepository repository) {
        this.repository = repository;
    }

    public Expression findById(Integer id) {
        Optional<Expression> entity = this.repository.findById(id);
        return entity.isPresent() ? entity.get() : null;
    }

    public List<Expression> findAll() {
        return this.repository.findAll();
    }

    public Expression findByDeviceValueAndProfileAndOperation(DeviceValue deviceValue, Profile profile, OperationType operationType) {
        return this.repository.findByDeviceValueAndProfileAndOperation(deviceValue, profile, operationType.getId());
    }

    public List<Expression> findAllByProfile(Profile profile) {
        return repository.findAllByProfile(profile);
    }

    public void remove(Expression entity) {
        this.repository.delete(entity);
    }

    public Expression update(Expression entity) {
        return this.repository.save(entity);
    }
}
