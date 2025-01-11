#include <MD_Parola.h>
#include <MD_MAX72xx.h>
#include <SPI.h>

#include <ESP8266WiFi.h>
#include <ESPAsyncTCP.h>
#include <ESPAsyncWebServer.h>
#include <ESP8266mDNS.h>
#include <NTPClient.h>
#include <WiFiUdp.h>

#include <OneWire.h>
#include <DallasTemperature.h>

// Uncomment according to your hardware type
#define HARDWARE_TYPE MD_MAX72XX::FC16_HW
//#define HARDWARE_TYPE MD_MAX72XX::GENERIC_HW

// Defining size, and output pins
#define MAX_DEVICES 4
#define CS_PIN 15

MD_Parola Display = MD_Parola(HARDWARE_TYPE, CS_PIN, MAX_DEVICES);

// Set WiFi credentials
#define WIFI_SSID "home"
#define WIFI_PASS "secret"
#define UDP_PORT 8761

// Server
#define SERVER_PORT 8080
AsyncWebServer server(SERVER_PORT);
const char* PARAM_INPUT = "load";
#define BUFFER_SIZE 320
char buffer[BUFFER_SIZE];

bool restart = false;

// UDP
WiFiUDP UDP;
// AsyncUDP UDP;

unsigned long millis_loopGetData;
unsigned long millis_loopDisplay;
unsigned long millis_loopMode;
unsigned int dspMode = 0;
const long utcOffsetInSeconds = 10800;
// WiFiUDP ntpUDP;
NTPClient timeClient(UDP, "pool.ntp.org", utcOffsetInSeconds);

//Wire data GPIO5
#define ONE_WIRE_BUS 5
OneWire oneWire(ONE_WIRE_BUS);
// Pass our oneWire reference to Dallas Temperature sensor 
DallasTemperature sensors(&oneWire);

DeviceAddress sensor1 = { 0x28, 0x9F, 0x5C, 0x7, 0xD6, 0x1, 0x3C, 0xA8 };

String uid1 = "28 9F 5C 7 D6 1 3C A8";
String temp1 = "00.00";
String temp2 = "+0.00";

String deviceName = "Часы - термометр в комнате";
String serverPort = String(SERVER_PORT);

unsigned long wakeUp = 600000;
unsigned long millis_loop;

int8_t DispMSG[] = {0, 0, 0, 0};
bool dspPoint = true;

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

  MDNS.begin("NodeMCU");
  timeClient.begin();

  server.on("/update", HTTP_GET, [] (AsyncWebServerRequest *request) {
    if (request -> hasParam(PARAM_INPUT)) {
      temp2 = request -> getParam(PARAM_INPUT) -> value();
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

  Display.begin();
  Display.setIntensity(15);
  Display.displayClear();
}

void getBuffer() {
  temp1 = String(sensors.getTempC(sensor1), 2);
  snprintf(buffer, BUFFER_SIZE,
  "{\"name\":\"%s\",\"ipAddress\":\"\",\"port\":%s,\"type\":2,\"wakeUp\":%d,\"values\":[{\"uid\":\"%s\",\"value\":%s}]}",
    deviceName.c_str(), serverPort.c_str(), wakeUp, uid1.c_str(), temp1.c_str());
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
      millis_loop = millis() + wakeUp;
      IPAddress broadcastIp = WiFi.localIP();
      broadcastIp[3] = 255;
      getBuffer();
      UDP.beginPacket(broadcastIp, UDP_PORT);
      UDP.printf(buffer, BUFFER_SIZE);
      UDP.endPacket();
    }
  }  
  // scanSensors();

  if (millis_loopGetData < millis()) {
    millis_loopGetData = millis() + 3600000;
    timeClient.update();
  }
  if (millis_loopMode < millis()) {
    millis_loopMode = millis() + 10000;
    dspMode++;
    if (dspMode > 2) dspMode = 0;
    if (dspMode == 1) {
      sensors.requestTemperatures();
    }
  }
  if (millis_loopDisplay < millis()) {
    millis_loopDisplay = millis() + 1000;
    // Display.displayClear();
    Display.setTextAlignment(PA_CENTER);
    if (dspMode == 0) {
      byte h = timeClient.getHours();
      byte m = timeClient.getMinutes();
      byte s = timeClient.getSeconds();
      DispMSG[1] = h % 10;
      DispMSG[0] = (h - DispMSG[1]) / 10;
      DispMSG[3] = m % 10;
      DispMSG[2] = (m - DispMSG[3]) / 10;
      Display.print(String(DispMSG[0]) + String(DispMSG[1]) + 
        (dspPoint ? ":" : " ") +
        String(DispMSG[2]) + String(DispMSG[3]));
      dspPoint = !dspPoint;
    } else if (dspMode == 1) {
      Display.print((sensors.getTempC(sensor1) > 0.0 ? "+" : "") + String(sensors.getTempC(sensor1), 2));
    } else if (dspMode == 2) {
      Display.print(temp2);
    }
  }    
}
