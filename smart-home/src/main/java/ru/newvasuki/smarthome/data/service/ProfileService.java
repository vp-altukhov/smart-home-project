package ru.newvasuki.smarthome.data.service;

import org.springframework.stereotype.Service;
import ru.newvasuki.smarthome.data.entity.Device;
import ru.newvasuki.smarthome.data.entity.Profile;
import ru.newvasuki.smarthome.data.repository.ProfileRepository;
import ru.newvasuki.smarthome.echo.EchoData;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;
    private ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }

    public Profile findById(Integer id) {
        Optional<Profile> entity = this.repository.findById(id);
        return entity.isPresent() ? entity.get() : null;
    }

    public List<Profile> findAll() {
        return this.repository.findAll();
    }

    public Profile findByNameAndDevice(String name, Device device) {
        return repository.findByNameAndDevice(name, device);
    }

    public List<Profile> findAllByDevice(Device device) {
        return repository.findAllByDevice(device);
    }

    public List<Profile> findAllByDeviceAndEnable(Device device, Boolean enable) {
        return repository.findAllByDeviceAndEnable(device, enable);
    }

    public Profile getEnabled(Device device) {
        List<Profile> profiles = repository.findAllByDeviceAndEnable(device, true);
        return profiles.isEmpty() ? null : profiles.get(0);
    }

    public void enableProfile(Profile profile) {
        List<Profile> profiles = repository.findAllByDeviceAndEnable(profile.getDevice(), true);
        for (Profile p: profiles) {
            p.setEnable(false);
            repository.save(p);
        }
        profile.setEnable(true);
        repository.save(profile);
    }

    public void remove(Profile entity) {
        this.repository.delete(entity);
    }

    public Profile update(Profile entity) {
        return this.repository.save(entity);
    }
}
