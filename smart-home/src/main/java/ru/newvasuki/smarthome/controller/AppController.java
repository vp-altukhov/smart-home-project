package ru.newvasuki.smarthome.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.newvasuki.smarthome.data.entity.*;
import ru.newvasuki.smarthome.data.model.Chart;
import ru.newvasuki.smarthome.data.model.ChartList;
import ru.newvasuki.smarthome.data.model.Charts;
import ru.newvasuki.smarthome.data.model.User;
import ru.newvasuki.smarthome.data.service.*;
import ru.newvasuki.smarthome.data.type.ExpressionType;
import ru.newvasuki.smarthome.data.type.OperationType;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping(path = "/app")
public class AppController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

    private DeviceService deviceService;
    private DeviceValueService deviceValueService;
    private ValueService valueService;
    private ProfileService profileService;
    private ExpressionService expressionService;

    public AppController(DeviceService deviceService, DeviceValueService deviceValueService, ValueService valueService, ProfileService profileService, ExpressionService expressionService) {
        this.deviceService = deviceService;
        this.deviceValueService = deviceValueService;
        this.valueService = valueService;
        this.profileService = profileService;
        this.expressionService = expressionService;
    }

    @GetMapping("/auth")
    public ResponseEntity<User> currentUserName(Principal principal) {
        return new ResponseEntity<>(new User(principal), HttpStatus.OK);
    }

    @GetMapping("/devices")
    public ResponseEntity<List<Device>> devices() {
        return new ResponseEntity<>(this.deviceService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/device/{id}")
    public ResponseEntity<Device> device(@PathVariable Integer id) {
        Device entity = deviceService.findById(id);
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @GetMapping("/restart/{id}")
    public ResponseEntity<Device> restart(@PathVariable Integer id) {
        Device entity = deviceService.findById(id);
        HttpEntity<String> request = new HttpEntity<>(null);
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://" + entity.getAddress() + (entity.getPort().equals(80) ? "" : ":" + entity.getPort()) +
                "/restart";
        try {
            ResponseEntity<Resource> response = restTemplate.exchange(url, HttpMethod.GET, request, Resource.class);
        } catch (HttpClientErrorException ex) {
            LOGGER.error(ex.toString());
        }
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @DeleteMapping("/device/{id}")
    public ResponseEntity<String> deviceRemove(@PathVariable Integer id) {
        Device entity = deviceService.findById(id);
        if (entity != null) {
            deviceService.remove(entity);
        }
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    @GetMapping("/device-values/{id}")
    public ResponseEntity<List<DeviceValue>> deviceValues(@PathVariable Integer id) {
        Device entity = deviceService.findById(id);
        List<DeviceValue> values = entity != null ? deviceValueService.findAllByDevice(entity) : deviceValueService.findAll();
        return new ResponseEntity<>(values, HttpStatus.OK);
    }

    @PostMapping("/device-values/{id}")
    public ResponseEntity<String> deviceValues(@PathVariable Integer id,
                                               @RequestParam String description) {
        DeviceValue value = deviceValueService.findById(id);
        value.setDescription(description);
        deviceValueService.update(value);
        return new ResponseEntity<>(HttpStatus.OK.name(), HttpStatus.OK);
    }

    @GetMapping("/device-charts/{id}")
    public ResponseEntity<Charts> deviceCharts(@PathVariable Integer id,
                                               @RequestParam(required = false) String date) {
        if (date == null || date.equals("undefined")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.format(new Date());
        }
        List<Chart> result = new ArrayList<>();
        Device device = deviceService.findById(id);
        List<DeviceValue> deviceValues = deviceValueService.findAllByDevice(device);
        for (DeviceValue deviceValue: deviceValues) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(date + " 00:00:00", formatter);
            List<Value> values = deviceValue.getValues(dateTime, valueService);
            Collections.sort(values, Comparator.comparing(Value::getDateTime));
            List<ChartList> chartLists = new ArrayList<>();
            for (Value value: values) {
                chartLists.add(new ChartList(value));
            }
            result.add(new Chart(deviceValue, chartLists));
        }
        Charts charts = new Charts(date, result);
        return new ResponseEntity(charts, HttpStatus.OK);
    }

    @GetMapping("/profiles/{id}")
    public ResponseEntity<List<Profile>> profiles(@PathVariable Integer id) {
        Device dev = this.deviceService.findById(id);
        List<Profile> profiles = this.profileService.findAllByDevice(dev);
        return ResponseEntity.ok(profiles);
    }

    @PostMapping("/profile/{id}")
    public ResponseEntity<Profile> profile(@PathVariable Integer id,
                                           @RequestParam Boolean enabled,
                                           @RequestParam String name) {
        Device dev = this.deviceService.findById(id);
        Profile profile = this.profileService.findByNameAndDevice(name, dev);
        if (profile == null) profile = new Profile();
        profile.setDevice(dev);
        profile.setEnable(enabled);
        profile.setName(name);
        this.profileService.update(profile);
        if (enabled) this.profileService.enableProfile(profile);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/profile/{id}")
    public ResponseEntity<String> profileRemove(@PathVariable Integer id) {
        Profile profile = this.profileService.findById(id);
        if (profile != null) this.profileService.remove(profile);
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    @GetMapping("/expressions/{id}")
    public ResponseEntity<List<Expression>> expressions(@PathVariable Integer id) {
        Profile profile = this.profileService.findById(id);
        List<Expression> expressions = this.expressionService.findAllByProfile(profile);
        return ResponseEntity.ok(expressions);
    }

    @PostMapping("/expression/{id}")
    public ResponseEntity<Profile> expression(@PathVariable Integer id,
                                              @RequestParam String type,
                                              @RequestParam Integer deviceValueId,
                                              @RequestParam Double threshold,
                                              @RequestParam String operation) {
        Profile profile = this.profileService.findById(id);
        DeviceValue deviceValue = this.deviceValueService.findById(deviceValueId);
        Expression expression = this.expressionService.findByDeviceValueAndProfileAndOperation(deviceValue, profile, OperationType.valueOf(operation));
        if (expression == null) expression = new Expression();
        expression.setProfile(profile);
        expression.setType(ExpressionType.valueOf(type));
        expression.setDeviceValue(deviceValue);
        expression.setThreshold(threshold);
        expression.setOperation(OperationType.valueOf(operation));
        this.expressionService.update(expression);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/expression/{id}")
    public ResponseEntity<String> expressionRemove(@PathVariable Integer id) {
        Expression expression = this.expressionService.findById(id);
        if (expression != null) this.expressionService.remove(expression);
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }
}
