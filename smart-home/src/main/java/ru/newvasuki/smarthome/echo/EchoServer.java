package ru.newvasuki.smarthome.echo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.newvasuki.smarthome.data.entity.Device;
import ru.newvasuki.smarthome.data.service.DeviceService;
import ru.newvasuki.smarthome.data.service.DeviceValueService;
import ru.newvasuki.smarthome.data.service.ValueService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class EchoServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoServer.class);
    private final DeviceService deviceService;
    private final DeviceValueService deviceValueService;
    private final ValueService valueService;

    private DatagramSocket socket;
    private boolean running = true;

    public EchoServer(DeviceService deviceService, DeviceValueService deviceValueService, ValueService valueService,
                      Integer port) throws SocketException {
        this.deviceService = deviceService;
        this.deviceValueService = deviceValueService;
        this.valueService = valueService;
        this.socket = new DatagramSocket(port);
        LOGGER.info("Echo server configured on port {}", port);
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        LOGGER.info("Echo server starts");

        ObjectMapper mapper = new ObjectMapper();

        while (this.running) {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                this.socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength());
                int i = received.indexOf('\u0000');
                received = received.substring(0, i);
                EchoData echoData = mapper.readValue(received, EchoData.class);
                echoData.setIpAddress(address.toString());
                Device device = deviceService.registerDevice(echoData);
                for (EchoValue echoValue: echoData.getValues()) {
                    this.deviceValueService.registerDeviceValue(device, echoValue);
                    this.valueService.registerValue(echoValue);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        socket.close();
    }
}
