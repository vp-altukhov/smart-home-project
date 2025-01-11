#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <OneWire.h>
#include <DallasTemperature.h>
 
// Set WiFi credentials
#define WIFI_SSID "home"
#define WIFI_PASS "altukhov"
#define UDP_PORT 8761
#define DEVICE_NAME "Термометр в спальне"
#define ONE_WIRE_BUS 2

unsigned long wakeUp = 600000;

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);
WiFiUDP UDP;

DeviceAddress sensor1 = { 0x28, 0x9C, 0xF4, 0x86, 0x43, 0x20, 0x1, 0xF1 };
String sens = "28 9C F4 86 43 20 1 F1";

void setup() {
  sensors.requestTemperatures();
  sensors.getTempC(sensor1);
  connectWiFi();
  sendData();
  goSleep();
}

String scanSensors() {
  byte i;
  byte addr[8];
 
  if (!oneWire.search(addr)) {
    oneWire.reset_search();
    delay(250);
    return "";
  }
  String s = "";
  for (i = 0; i < 8; i++) {
    s += " " + String(addr[i], HEX);
  }
  return s;
}

void sendData() {
  IPAddress broadcastIp = WiFi.localIP();
  broadcastIp[3] = 255;
  //String sens = scanSensors();
  sensors.requestTemperatures();
  String temp = String(sensors.getTempC(sensor1), 2);
  String reply = "{\"name\":\"" + String(DEVICE_NAME) + "\",\"ipAddress\":\"\",\"port\":8080,\"type\":1,\"wakeUp\":" + String(wakeUp) + ",\"values\":[{\"uid\":\"" + sens + "\",\"value\":" + temp + "}]}";
  UDP.beginPacket(broadcastIp, UDP_PORT);
  UDP.write(reply.c_str());
  UDP.endPacket(); 
  delay(100);   
}

void connectWiFi() {
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  while (WiFi.status() != WL_CONNECTED) delay(500);
  delay(100);
}

void goSleep() {
  WiFi.disconnect();
  WiFi.forceSleepBegin();  
}

void loop() {
  connectWiFi();
  sendData();
  goSleep();
  delay(wakeUp - 3000);
}
