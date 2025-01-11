package ru.newvasuki.smarthome.data.service;

import org.springframework.stereotype.Service;
import ru.newvasuki.smarthome.data.entity.Device;
import ru.newvasuki.smarthome.data.entity.DeviceValue;
import ru.newvasuki.smarthome.data.repository.DeviceValueRepository;
import ru.newvasuki.smarthome.echo.EchoValue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceValueService {
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;
    private DeviceValueRepository repository;

    public DeviceValueService(DeviceValueRepository repository) {
        this.repository = repository;
    }

    public DeviceValue findById(Integer id) {
        Optional<DeviceValue> deviceValue = this.repository.findById(id);
        return deviceValue.isPresent() ? deviceValue.get() : null;
    }

    public List<DeviceValue> findAll() {
        return this.repository.findAll();
    }

    public List<DeviceValue> findAllByDevice(Device device) {
        return repository.findAllByDevice(device);
    }

    public DeviceValue findByUid(String uid) {
        return this.repository.findByUid(uid);
    }

    public List<DeviceValue> findAllByUidIn(List<String> uidList) {
        return this.repository.findAllByUidIn(uidList);
    }

    public DeviceValue registerDeviceValue(Device device, EchoValue echoValue) {
        DeviceValue entity = this.repository.findByDeviceAndUid(device, echoValue.getUid());
        if (entity == null) {
            entity = new DeviceValue();
            entity.setDevice(device);
            entity.setUid(echoValue.getUid());
            return update(entity);
        }
        return entity;
    }

    public DeviceValue update(DeviceValue entity) {
        return this.repository.save(entity);
    }

    public void remove(DeviceValue entity) {
        this.repository.delete(entity);
    }
}
