package ru.newvasuki.smarthome.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.newvasuki.smarthome.data.entity.Device;
import ru.newvasuki.smarthome.data.entity.DeviceValue;

import java.util.List;

public interface DeviceValueRepository extends JpaRepository<DeviceValue, Integer> {
    DeviceValue findByDeviceAndUid(Device device, String uid);
    DeviceValue findByUid(String uid);
    List<DeviceValue> findAllByDevice(Device device);
    List<DeviceValue> findAllByUidIn(List<String> uidList);
}
