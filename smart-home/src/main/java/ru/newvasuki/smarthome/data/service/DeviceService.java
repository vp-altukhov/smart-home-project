package ru.newvasuki.smarthome.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.newvasuki.smarthome.data.entity.Device;
import ru.newvasuki.smarthome.data.entity.DeviceValue;
import ru.newvasuki.smarthome.data.repository.DeviceRepository;
import ru.newvasuki.smarthome.data.type.DeviceType;
import ru.newvasuki.smarthome.echo.EchoData;
import ru.newvasuki.smarthome.echo.EchoValue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;
    private DeviceRepository repository;
    private DeviceValueService deviceValueService;

    public DeviceService(DeviceRepository repository, DeviceValueService deviceValueService) {
        this.repository = repository;
        this.deviceValueService = deviceValueService;
    }

    public Device findById(Integer id) {
        Optional<Device> entity = this.repository.findById(id);
        return entity.isPresent() ? entity.get() : null;
    }

    public List<Device> findAll() {
        return this.repository.findAll();
    }

    public Device registerDevice(EchoData echoData) {
        LOGGER.info("Receive echo data {}", echoData.toString());

        Device entity = this.repository.findByNameAndTypeAndAddressAndPort(echoData.getName(), echoData.getType(),
                echoData.getIpAddress(), echoData.getPort());

        if (entity == null) {
            List<String> uidList = echoData.getValues().stream().map(EchoValue::getUid).collect(Collectors.toList());
            LOGGER.info("Find device values by uid list {}", String.join(", ", uidList));
            List<DeviceValue> deiceValues = this.deviceValueService.findAllByUidIn(uidList);
            if (!deiceValues.isEmpty()) {
                LOGGER.info("Find device by uid list {}", String.join(", ", uidList));
                Device device = this.repository.findFirstByDeviceValuesIn(deiceValues);
                if (device != null) {
                    LOGGER.info("Exist device {} by uid list {}", device.toString(), String.join(", ", uidList));
                    device.setAddress(echoData.getIpAddress());
                    device.setPort(echoData.getPort());
                    entity = device;
                } else LOGGER.info("Device not exist by uid list {}", String.join(", ", uidList));
            } else LOGGER.info("Device values not found by uid list {}", String.join(", ", uidList));
        } else LOGGER.info("Exist device {} by echo data", entity.toString());

        LocalDateTime date = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        if (entity == null) {
            entity = new Device();
            entity.setName(echoData.getName());
            entity.setType(DeviceType.get(echoData.getType()));
            entity.setAddress(echoData.getIpAddress());
            entity.setPort(echoData.getPort());
            LOGGER.info("Register new device {}", entity.toString());
        }
        entity.setDateTime(date);
        entity.setActive(true);
        entity = update(entity);
        return entity;
    }

    public void remove(Device entity) {
        this.repository.delete(entity);
    }

    public Device update(Device entity) {
        return this.repository.save(entity);
    }
}
