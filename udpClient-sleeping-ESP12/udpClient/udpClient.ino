#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <OneWire.h>
#include <DallasTemperature.h>

#define UDP_PORT 8761
#define DEVICE_NAME "Автономный термометр №2"
#define ONE_WIRE_BUS 5

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);
WiFiUDP UDP;

const int wakeUpInterval = 900 * 1000000ul;
const int wakeUp = wakeUpInterval / 1000;

const char* ssid = "home"; //enter ID of your Router
const char* password = "altukhov"; //enter password for your Router

//№1
//DeviceAddress sensor1 = { 0x28, 0x9F, 0x5C, 0x7, 0xD6, 0x1, 0x3C, 0xA8 };
//const String sens = "28 9F 5C 7 D6 1 3C A8";

//№2
DeviceAddress sensor1 = { 0x28, 0x83, 0xFF, 0x7, 0xD6, 0x1, 0x3C, 0xF3 };
const String sens = "28 83 FF 7 D6 1 3C F3";

void setup()
{
  Serial.begin(115200);
  while (!Serial);
  Serial.println();

  connectWiFi();
  sendData();
  
  // Enter deep sleep
  Serial.println("Going to sleep...");
  delay(1000); // Give some time for Serial output to complete
  WiFi.disconnect(); // Disconnect from Wi-Fi before entering deep sleep
  delay(100); // Give some time for disconnection

  // Configure deep sleep
  ESP.deepSleep(wakeUpInterval, WAKE_RF_DISABLED);
}

void loop() {
  //scanSensors();
}

void scanSensors() {
  byte i;
  byte addr[8];
 
  if (!oneWire.search(addr)) {
    Serial.println(" No more addresses.");
    Serial.println();
    oneWire.reset_search();
    delay(250);
    return;
  }
  Serial.print(" ROM =");
  for (i = 0; i < 8; i++) {
    Serial.write(' ');
    Serial.print(addr[i], HEX);
  }
}

void sendData() {
  IPAddress broadcastIp = WiFi.localIP();
  broadcastIp[3] = 255;
  sensors.requestTemperatures();
  String temp = String(sensors.getTempC(sensor1), 2);
  String reply = "{\"name\":\"" + String(DEVICE_NAME) + "\",\"ipAddress\":\"\",\"port\":8080,\"type\":1,\"wakeUp\":" + String(wakeUp) + ",\"values\":[{\"uid\":\"" + sens + "\",\"value\":" + temp + "}]}";
  Serial.println("Send data -> " + reply);
  UDP.beginPacket(broadcastIp, UDP_PORT);
  UDP.write(reply.c_str());
  UDP.endPacket();
}

void connectWiFi()
{
  delay(500);
  Serial.print("Connecting to WiFi...");
  WiFi.begin(ssid, password);
  int i = 0;
  while (WiFi.status() != WL_CONNECTED && i < 50)
  {
    delay(500);
    Serial.print(".");
    i++;
  }
  Serial.println("");
  if (WiFi.status() == WL_CONNECTED) Serial.println("Success connected to WiFi!");
  else Serial.println("Fail connected to WiFi!");
}