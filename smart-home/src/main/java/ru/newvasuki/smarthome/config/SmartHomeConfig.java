package ru.newvasuki.smarthome.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.newvasuki.smarthome.data.entity.*;
import ru.newvasuki.smarthome.data.service.DeviceService;
import ru.newvasuki.smarthome.data.service.DeviceValueService;
import ru.newvasuki.smarthome.data.service.ProfileService;
import ru.newvasuki.smarthome.data.service.ValueService;
import ru.newvasuki.smarthome.data.type.DeviceType;
import ru.newvasuki.smarthome.data.type.ExpressionType;
import ru.newvasuki.smarthome.data.type.OperationType;
import ru.newvasuki.smarthome.echo.EchoData;
import ru.newvasuki.smarthome.echo.EchoServer;
import ru.newvasuki.smarthome.echo.EchoValue;

import java.net.SocketException;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableConfigurationProperties(SmartHomeConfigurationProperties.class)
@ComponentScan
@EnableScheduling
public class SmartHomeConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmartHomeConfig.class);
    private DeviceValueService deviceValueService;
    private ValueService valueService;
    private DeviceService deviceService;
    private ProfileService profileService;

    public SmartHomeConfig(SmartHomeConfigurationProperties config, DeviceService deviceService, DeviceValueService deviceValueService,
                           ValueService valueService, ProfileService profileService) {
        this.deviceValueService = deviceValueService;
        this.valueService = valueService;
        this.deviceService = deviceService;
        this.profileService = profileService;
        try {
            EchoServer echoServer = new EchoServer(deviceService, deviceValueService, valueService, config.getBroadcastPort());
            Thread thread = new Thread(echoServer);
            thread.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 60000)
    protected void executeDevices() {
        List<Device> devices = deviceService.findAll();
        for (Device device: devices) {
            Profile profile = profileService.getEnabled(device);
            if (profile != null) {
                for (Expression expression : profile.getExpressions()) {
                    String uid = expression.getDeviceValue().getUid();
                    //Последнее значение контрольного датчика
                    Value val = this.valueService.getLastByUid(uid);
                    Double value = val != null ? val.getValue() : null;
                    if (device.getType() != DeviceType.SENSOR) {
                        EchoData echoData = getData(device);
                        if (echoData != null) {
                            //Если датчик в составе опрошенного устройства, получаем свежее значение
                            Optional<EchoValue> echoValue = echoData.getValues().stream().filter(d -> d.getUid().equals(uid)).findFirst();
                            if (echoValue.isPresent()) value = echoValue.get().getValue();
                        }
                    }
                    if (value != null) {
                        //Отрабатываем изменения
                        if (expression.getOperation().equals(OperationType.VALUE)) sendResponse(expression, value);
                        else handleChangeValue(expression, value);
                    }
                }
            }
        }
    }

    private EchoData getData(Device device) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>("");
        String url = "http://" + device.getAddress() + (device.getPort().equals(80) ? "" : ":" + device.getPort()) + "/get";
        try {
            ResponseEntity<EchoData> response = restTemplate.exchange(url, HttpMethod.GET, request, EchoData.class);
            EchoData echoData = response.getBody();
            if (!device.getActive()) {
                device.setActive(true);
                deviceService.update(device);
            }
            if (echoData.getValues() != null && echoData.getValues().size() > 0) {
                for (EchoValue echoVal : echoData.getValues()) {
                    valueService.registerValue(echoVal);
                }
            }
            return echoData;
        } catch (HttpClientErrorException | ResourceAccessException ex) {
            if (device.getActive()) LOGGER.error(ex.toString());
            if (device.getActive()) {
                device.setActive(false);
                deviceService.update(device);
            }
        }
        return null;
    }

    private void sendResponse(Expression expression, Double value) {
        Device device = expression.getProfile().getDevice();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>("");
        String urlVal = "/";
        if (expression.getOperation().equals(OperationType.ON)) urlVal = "/update?load=1";
        if (expression.getOperation().equals(OperationType.OFF)) urlVal = "/update?load=0";
        if (expression.getOperation().equals(OperationType.VALUE)) {
            String val = String.format("%,.2f", value).replace(",", ".");
            urlVal = "/update?load=" + val;
        }
        String url = "http://" + device.getAddress() + (device.getPort().equals(80) ? "" : ":" + device.getPort()) + urlVal;
        try {
            restTemplate.exchange(url, HttpMethod.GET, request, Resource.class);
        } catch (HttpClientErrorException | ResourceAccessException ex) {
            if (device.getActive()) {
                device.setActive(false);
                deviceService.update(device);
            }
            LOGGER.error(ex.toString());
        }
    }

    private void handleChangeValue(Expression expression, Double value) {
        Double threshold = expression.getThreshold();
        if (expression.getType().equals(ExpressionType.LESS_OR_EQUALS)) {
            if (value <= threshold) sendResponse(expression, value);
        } else if (expression.getType().equals(ExpressionType.MORE_OR_EQUALS) && value >= threshold) {
            sendResponse(expression, value);
        }
    }
}
