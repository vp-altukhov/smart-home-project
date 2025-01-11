#include <WiFi.h>
#include <AsyncTCP.h>
#include <ESPAsyncWebServer.h>
//#include <WiFiUdp.h>
#include <AsyncUDP.h>
#include <OneWire.h>
#include <DallasTemperature.h>

// Set WiFi credentials
#define WIFI_SSID "home"
#define WIFI_PASS "secret"
#define UDP_PORT 8761

// Server
#define SERVER_PORT 8080
AsyncWebServer server(SERVER_PORT);
#define LOAD_BUS 17
const char* PARAM_INPUT = "load";
unsigned int loadValue = 0;
#define BUFFER_SIZE 320
char buffer[BUFFER_SIZE];

bool restart = false;

// UDP
//WiFiUDP UDP;
AsyncUDP UDP;

// Wire data GPIO15
#define ONE_WIRE_BUS 15
OneWire oneWire(ONE_WIRE_BUS);
// Pass our oneWire reference to Dallas Temperature sensor 
DallasTemperature sensors(&oneWire);

DeviceAddress sensor1 = { 0x28, 0xE7, 0xA3, 0x7, 0xD6, 0x1, 0x3C, 0x58 };
DeviceAddress sensor2 = { 0x28, 0xF, 0x4D, 0x7, 0xD6, 0x1, 0x3C, 0xA8 };

String uid1 = "28 E7 A3 7 D6 1 3C 58";
String temp1 = "000.00";
String uid2 = "28 F 4D 7 D6 1 3C A8";
String temp2 = "000.00";
String uid3 = "7bbb7ae1-c9fb-48ee-bafa-94222c1c9cdb";

String deviceName = "Управление котлом отопления";
String serverPort = String(SERVER_PORT);

unsigned long wakeUp = 600000;
unsigned long millis_loop;

void setup() {
  // Setup serial port
  Serial.begin(115200);
  Serial.println();
 
  // Begin WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASS);
 
  // Connecting to WiFi...
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  // Loop continuously while WiFi is not connected
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(100);
    Serial.print(".");
  }
 
  // Connected to WiFi
  Serial.println();
  Serial.print("Connected! IP address: ");
  Serial.println(WiFi.localIP());

  pinMode(LOAD_BUS, OUTPUT);
  digitalWrite(LOAD_BUS, LOW);

  server.on("/update", HTTP_GET, [] (AsyncWebServerRequest *request) {
    String inputMessage;
    if (request -> hasParam(PARAM_INPUT)) {
      inputMessage = request -> getParam(PARAM_INPUT) -> value();
      digitalWrite(LOAD_BUS, inputMessage.toInt());
      loadValue = inputMessage.toInt();
    }
    request -> send(200, "text/plain", "OK");
  });

  server.on("/get", HTTP_GET, [] (AsyncWebServerRequest *request) {
    getBuffer();
    request -> send(200, "application/json", buffer);
  });

  server.on("/restart", HTTP_GET, [] (AsyncWebServerRequest *request) {
    restart = true;
    millis_loop = millis() + 1000;
    request -> send(200, "text/plain", "OK");
  });
  server.begin();
}

void getBuffer() {
  sensors.requestTemperatures();
  temp1 = String(sensors.getTempC(sensor1), 2);
  temp2 = String(sensors.getTempC(sensor2), 2);
  snprintf(buffer, BUFFER_SIZE,
  "{\"name\":\"%s\",\"ipAddress\":\"\",\"port\":%s,\"type\":2,\"wakeUp\":%d,\"values\":[{\"uid\":\"%s\",\"value\":%s},{\"uid\":\"%s\",\"value\":%s},{\"uid\":\"%s\",\"value\":%d}]}",
  deviceName.c_str(), serverPort.c_str(), wakeUp, uid1.c_str(), temp1.c_str(), uid2.c_str(), temp2.c_str(), uid3.c_str(), loadValue);
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

void loop() {
  if (millis_loop < millis()) {
    if (restart) {
      ESP.restart();
      restart = false;
      millis_loop = millis() + 1000;      
    } else {
      // scanSensors();
      millis_loop = millis() + wakeUp;
      IPAddress broadcastIp = WiFi.localIP();
      broadcastIp[3] = 255;
      getBuffer();
      UDP.broadcastTo(buffer, UDP_PORT);
      // UDP.beginPacket(broadcastIp, UDP_PORT);
      // UDP.printf(buffer, BUFFER_SIZE);
      // UDP.endPacket();
    }
  }
}
