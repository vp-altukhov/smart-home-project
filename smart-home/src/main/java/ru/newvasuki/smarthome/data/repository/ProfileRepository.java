package ru.newvasuki.smarthome.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.newvasuki.smarthome.data.entity.Device;
import ru.newvasuki.smarthome.data.entity.Profile;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Profile findByNameAndDevice(String name, Device device);

    List<Profile> findAllByDevice(Device device);

    List<Profile> findAllByDeviceAndEnable(Device device, Boolean enable);
}
