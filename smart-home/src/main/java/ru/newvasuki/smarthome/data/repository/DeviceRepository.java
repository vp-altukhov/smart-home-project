package ru.newvasuki.smarthome.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.newvasuki.smarthome.data.entity.Device;
import ru.newvasuki.smarthome.data.entity.DeviceValue;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Integer> {
    Device findByNameAndTypeAndAddressAndPort(String name, Integer type, String ipAddress, Integer port);
    Device findFirstByDeviceValuesIn(List<DeviceValue> deviceValues);
    List<Device> findAll();
}
