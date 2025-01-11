package ru.newvasuki.smarthome.bot;

import org.springframework.stereotype.Service;
import ru.newvasuki.smarthome.data.entity.*;
import ru.newvasuki.smarthome.data.service.DeviceService;
import ru.newvasuki.smarthome.data.service.DeviceValueService;
import ru.newvasuki.smarthome.data.service.ProfileService;
import ru.newvasuki.smarthome.data.service.ValueService;
import ru.newvasuki.smarthome.data.type.ExpressionType;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatusService {
    private DeviceService deviceService;
    private DeviceValueService deviceValueService;
    private ValueService valueService;
    private ProfileService profileService;

    public StatusService(DeviceService deviceService, DeviceValueService deviceValueService, ValueService valueService, ProfileService profileService) {
        this.deviceService = deviceService;
        this.deviceValueService = deviceValueService;
        this.valueService = valueService;
        this.profileService = profileService;
    }

    public void profileEnable(String name) {
        String[] spl = name.split(":");
        Integer id = Integer.parseInt(spl[0]);
        Profile profile = this.profileService.findById(id);
        this.profileService.enableProfile(profile);
    }

    public List<Device> getDeviceList() {
        return this.deviceService.findAll();
    }

    public List<Profile> getProfileList(String name) {
        String[] spl = name.split(":");
        Integer id = Integer.parseInt(spl[0]);
        Device device = this.deviceService.findById(id);
        return this.profileService.findAllByDevice(device);
    }

    public String getState() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<Device> devices = this.deviceService.findAll();
        for (Device device: devices) {
            sb.append("Устройство: \"" + device.getName() + "\" " + (device.getActive() ? "активно" : "пассивно"));
            sb.append("\n");
            sb.append("Описание: " + device.getDescription() + "\n");
            List<DeviceValue> deviceValues = this.deviceValueService.findAllByDevice(device);
            for (DeviceValue deviceValue: deviceValues) {
                Value value = this.valueService.getLastByUid(deviceValue.getUid());
                if (value == null) sb.append("Датчик: \"" + deviceValue.getDescription() + "\" значений нет\n");
                else sb.append("Датчик: \"" + deviceValue.getDescription() + "\" значение: "
                        + String.format("%,.2f", value.getValue()) + " время: "
                        + value.getDateTime().format(formatter) + "\n");
            }
            List<Profile> profiles = this.profileService.findAllByDeviceAndEnable(device, true);
            if (!profiles.isEmpty()) {
                sb.append("Активный профиль: " + profiles.get(0).getName() + "\n");
                for (Expression exp: profiles.get(0).getExpressions()) {
                    sb.append("Выражение: " + exp.getDeviceValue().getDescription() + " "
                            + (exp.getType().equals(ExpressionType.MORE_OR_EQUALS) ? ">=" : "<=")
                            + " " + String.format("%,.2f", exp.getThreshold())
                            + " " + exp.getOperation().getName() + "\n");
                }
            } else sb.append("Нет активных профилей");
            sb.append("\n");
        }
        return sb.toString();
    }
}
