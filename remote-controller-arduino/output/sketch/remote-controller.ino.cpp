#include <Arduino.h>
#line 1 "/home/victor2022/projects/Arduino/remote-controller/remote-controller.ino"
/*
-*- encoding: utf-8 -*-
@File    :   remote-controller.ino
@Time    :   2022/11/12 21:17:13
@Author  :   victor2022
@Version :   1.0
@Desc    :   main for remote monitor
*/
#include <EEPROM.h>
void initNetwork(int i);
void setupServer();
void serve();
void setupPin();

#line 15 "/home/victor2022/projects/Arduino/remote-controller/remote-controller.ino"
void setup();
#line 31 "/home/victor2022/projects/Arduino/remote-controller/remote-controller.ino"
void loop();
#line 40 "/home/victor2022/projects/Arduino/remote-controller/remote-controller.ino"
void initNetwork(int timeout);
#line 27 "/home/victor2022/projects/Arduino/remote-controller/AccessPointHandler.ino"
void startAp();
#line 93 "/home/victor2022/projects/Arduino/remote-controller/ControlWebServer.ino"
void handleSwitch();
#line 117 "/home/victor2022/projects/Arduino/remote-controller/ControlWebServer.ino"
void handleNotFound();
#line 134 "/home/victor2022/projects/Arduino/remote-controller/ControlWebServer.ino"
bool handlePost();
#line 19 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
void initEeprom();
#line 28 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
bool set_string(int addr, int size, String str, int flag);
#line 47 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
String get_string(int addr, int size);
#line 59 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
bool saveSsid(const char* str);
#line 64 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
bool savePasswd(const char* str);
#line 69 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
bool saveSign();
#line 73 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
String getSsid();
#line 78 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
String getPasswd();
#line 83 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
String getSign();
#line 92 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
void clear();
#line 103 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
void saveWifiInfoAndReset(const char* ssid, const char* passwd);
#line 18 "/home/victor2022/projects/Arduino/remote-controller/WiFiConnecter.ino"
bool connectWifi(int timeout, const char* ssid, const char* passwd);
#line 15 "/home/victor2022/projects/Arduino/remote-controller/remote-controller.ino"
void setup(){
    Serial.begin(115200);
    delay(500);
    Serial.println("");
    // 初始化EEPROM
    initEeprom();
    // 连接wifi
    initNetwork(10);
    // 启动服务器
    setupServer();
    // 设置串口
    setupPin();

    // saveSsid("CU_wifi");
}

void loop(){
    serve();
}

/*
@Time    :   2022/11/13 15:27:36
@Author  :   victor2022
@Desc    :   初始化网络
*/
void initNetwork(int timeout)
{
    // 处理wifi
    if (getSign() == "Y")
    {
        String ssid = getSsid();
        String passwd = getPasswd();
        // 尝试连接wifi
        if (connectWifi(timeout, ssid.c_str(), passwd.c_str()))
            return;
    }
    // 开启ap服务器，开启服务
    startAp();
}
#line 1 "/home/victor2022/projects/Arduino/remote-controller/AccessPointHandler.ino"
/*
-*- encoding: utf-8 -*-
@File    :   AccessPointHandler.ino
@Time    :   2022/11/12 22:45:10
@Author  :   victor2022
@Version :   1.0
@Desc    :   web server of arduino for connect
*/
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>

#ifndef APSSID
#define APSSID "ap_of_esp"
#define APPSK "88888888"
#endif

// ap info
String apSsid= APSSID;
String apPsk = APPSK;

/*
@Time    :   2022/11/13 21:28:50
@Author  :   victor2022
@Desc    :   开启热点
*/
void startAp(){
    Serial.println("Access point starting...");
    WiFi.softAP(apSsid, apPsk);
    // print infos
    IPAddress myIP = WiFi.softAPIP();
    Serial.printf("SSID: %s, passowrd: %s\n", apSsid, apPsk);
    Serial.print("AP IP address: ");
    Serial.println(myIP);
    Serial.println();
}
#line 1 "/home/victor2022/projects/Arduino/remote-controller/ControlWebServer.ino"
/*
-*- encoding: utf-8 -*-
@File    :   ControlWebServer.ino
@Time    :   2022/11/12 22:45:10
@Author  :   victor2022
@Version :   1.0
@Desc    :   web server of arduino for control
*/
#include <ESP8266WebServer.h>
// control server
void handleIndex();
void handleTurnOn();
void handleTurnOff();
void handleRoot();
// info server
void handleSave();
// Save
// PinHandler
void turnOn();
void turnOff();
String switchLed();

// 声明服务器
ESP8266WebServer server(80);

/*
@Time    :   2022/11/12 23:06:54
@Author  :   victor2022
@Desc    :   初始化控制服务器
*/
void setupServer(){
    server.on("/",handleIndex);
    server.on("/turnon/", handleTurnOn);
    server.on("/turnoff/", handleTurnOff);
    server.on("/switch/", handleSwitch);
    server.on("/saveinfo/", handleSave);
    server.onNotFound(handleNotFound);
    server.begin();
}

/*
@Time    :   2022/11/12 23:07:02
@Author  :   victor2022
@Desc    :   处理用户请求
*/
void serve(){
    server.handleClient();
}

const String indexPage = "<html><head>    <title>ESP8266 Web Server POST handling</title>    <style>     body {            background-color: #cccccc;            font-family: Arial, Helvetica, Sans-Serif;            Color: #000088;        }    </style></head><body>    <!-- turn on -->    <h1>POST plain text to /turnon/</h1>    <form method=\"post\" action=\"/turnon/\" target=\"turnOnPage\">        <input type=\"submit\" value=\"Submit\">    </form>    <iframe id=\"turnOnPage\" name=\"turnOnPage\" hidden=\"true\"></iframe>    <!-- turn off -->    <h1>POST form data to /turnoff/</h1>    <form method=\"post\" action=\"/turnoff/\" target=\"turnOffPage\">        <input type=\"submit\" value=\"Submit\">    </form>    <iframe id=\"turnOffPage\" name=\"turnOffPage\" hidden=\"true\"></iframe>    <!-- switch -->    <h1>Switch led</h1>    <form method=\"post\" action=\"/switch/\" target=\"switchPage\">        <input type=\"submit\" value=\"Submit\">    </form>    <iframe id=\"switchPage\" name=\"switchPage\" hidden=\"true\"></iframe>    <!-- save info -->    <h1>Update wifi config</h1>    <form method=\"post\" action=\"/saveinfo/\" enctype=\"application/x-www-form-urlencoded\">        <dev>SSID:  </dev><input type=\"text\" name=\"ssid\"></input><br><br>        <dev>Password:  </dev><input type=\"text\" name=\"passwd\"></input><br><br>        <input type=\"submit\" value=\"Submit\">    </form></body></html>";

/*
@Time    :   2022/11/13 11:43:36
@Author  :   victor2022
@Desc    :   index page
*/
void handleIndex()
{
  Serial.println("index is visited...");
  server.send(200, "text/html", indexPage);
}

/*
@Time    :   2022/11/13 12:22:41
@Author  :   victor2022
@Desc    :   turn on page
*/
void handleTurnOn()
{
  if (!handlePost())
    return;
  turnOn();
  server.send(200, "text/plain", "success");
}

/*
@Time    :   2022/11/13 12:22:52
@Author  :   victor2022
@Desc    :   turn off page
*/
void handleTurnOff()
{
  if(!handlePost())return;
  turnOff();
  server.send(200, "text/plain", "success");
}

/*
@Time    :   2022/11/13 12:50:32
@Author  :   victor2022
@Desc    :   switch page
*/
void handleSwitch(){
  if (!handlePost())
    return;
  String res = switchLed();
  server.send(200, "text/plain", res);
}

/*
@Time    :   2022/11/13 21:27:20
@Author  :   victor2022
@Desc    :   存储wifi信息
*/
void handleSave(){
  if (!handlePost())
    return;
  // 解析参数
  const char* ssid = server.arg("ssid").c_str();
  const char* passwd = server.arg("passwd").c_str();
  Serial.printf("Receive data: \nssid: %s\t password: %s\n\n",ssid,passwd);
  // 存储wifi信息
  saveWifiInfoAndReset(ssid,passwd);
  server.send(200, "text/plain", "update success, please reboot");
}

void handleNotFound()
{
  String message = "File Not Found\n\n";
  message += "URI: ";
  message += server.uri();
  message += "\nMethod: ";
  message += (server.method() == HTTP_GET) ? "GET" : "POST";
  message += "\nArguments: ";
  message += server.args();
  message += "\n";
  for (uint8_t i = 0; i < server.args(); i++)
  {
    message += " " + server.argName(i) + ": " + server.arg(i) + "\n";
  }
  server.send(404, "text/plain", message);
}

bool handlePost(){
  if (server.method() != HTTP_POST)
  {
    server.send(405, "text/plain", "Method Not Allowed");
    return false;
  }
  return true;
}


#line 1 "/home/victor2022/projects/Arduino/remote-controller/DataHandler.ino"
/*
-*- encoding: utf-8 -*-
@File    :   DataHandler.ino
@Time    :   2022/11/13 00:25:21
@Author  :   victor2022
@Version :   1.0
@Desc    :   data read and write handlers for arduino
*/
#include <EEPROM.h>

const String sign = "Y"; // 信息标记
const int sign_addr = 0;
const int ssid_addr = 8;
const int ssid_len = 32;
const int pswd_addr = 48;
const int pswd_len = 32;

// 开启EEPROM
void initEeprom(){
    EEPROM.begin(128);
}

/*
@Time    :   2022/11/13 20:45:07
@Author  :   victor2022
@Desc    :   写入字符串
*/
bool set_string(int addr, int size, String str, int flag)
{
    if (flag)
        EEPROM.write(size, str.length()); // EEPROM第a位，写入str字符串的长度
    //通过一个for循环，把str所有数据，逐个保存在EEPROM
    for (int i = 0; i < str.length(); i++)
    {
        EEPROM.write(addr + i, str[i]);
        EEPROM.commit();
    }
    Serial.println("Data has been saved to EEPROM!");
    return true; //执行保存EEPROM
}

/*
@Time    :   2022/11/13 20:45:24
@Author  :   victor2022
@Desc    :   读取字符串
*/
String get_string(int addr, int size)
{
    String data = "";
    //通过一个for循环，从EEPROM中逐个取出每一位的值，并连接起来
    for (int i = 0; i < size; i++)
    {
        data += char(EEPROM.read(addr + i));
    }
    return data;
}

// 设置wifi信息
bool saveSsid(const char* str){
    return set_string(ssid_addr,ssid_len, str, 1); //保存wifi名称
    // return set_string(15,10,str,1);
}

bool savePasswd(const char* str){
    return set_string(pswd_addr,pswd_len, str, 1); //保存wifi密码
    // return set_string(50, 55, str, 1);
}

bool saveSign(){
    return set_string(sign_addr,1,sign,0);    
}

String getSsid(){
    return get_string(ssid_addr, EEPROM.read(ssid_len));
    // return get_string(15,EEPROM.read(10));
}

String getPasswd(){
    return get_string(pswd_addr, EEPROM.read(pswd_len));
    // return get_string(50, EEPROM.read(55));
}

String getSign(){
    return get_string(sign_addr, 1);
}

/*
@Time    :   2022/11/13 22:50:57
@Author  :   victor2022
@Desc    :   清空数据
*/
void clear(){
    for (int i = 0; i < 128; i++)
    {
        EEPROM.write(i, 0);
    }
}
/*
@Time    :   2022/11/13 20:54:00
@Author  :   victor2022
@Desc    :   保存wifi相关信息并重启
*/
void saveWifiInfoAndReset(const char* ssid, const char* passwd){
    // 清空数据
    clear();
    delay(500);
    Serial.println("Writing data...");
    // 写入新数据
    saveSsid(ssid);
    savePasswd(passwd);
    saveSign();
}
#line 1 "/home/victor2022/projects/Arduino/remote-controller/PinHandler.ino"
/*
-*- encoding: utf-8 -*-
@File    :   PinHandler.ino
@Time    :   2022/11/13 12:35:15
@Author  :   victor2022
@Version :   1.0
@Desc    :   handle pins of arduino
*/
#include <Arduino.h>

const int ledPin = 2; // pin number for led
bool isOn = false; // 表示灯是否开

/*
@Time    :   2022/11/13 12:36:15
@Author  :   victor2022
@Desc    :   set pin modes
*/
void turnOff();
void setupPin(){
    pinMode(ledPin, OUTPUT);
    turnOff();
}

/*
@Time    :   2022/11/13 12:26:38
@Author  :   victor2022
@Desc    :   开灯
*/
void turnOn(){
    digitalWrite(ledPin, LOW);
}

/*
@Time    :   2022/11/13 12:26:44
@Author  :   victor2022
@Desc    :   关灯
*/
void turnOff(){
    digitalWrite(ledPin, HIGH);
}

/*
@Time    :   2022/11/13 12:52:08
@Author  :   victor2022
@Desc    :   调整到另一状态
*/
String switchLed(){
    if(isOn){
        turnOff();
    }else{
        turnOn();
    }
    isOn = !isOn;
    return isOn?"on":"off";
}
#line 1 "/home/victor2022/projects/Arduino/remote-controller/WiFiConnecter.ino"
/*
-*- encoding: utf-8 -*-
@File    :   WiFiConnecter.ino
@Time    :   2022/11/12 22:04:47
@Author  :   victor2022
@Version :   1.0
@Desc    :   connect functions for wifi
*/
#include <ESP8266WiFi.h>

const char* name = "remote_controller_00";

/*
@Time    :   2022/11/12 22:42:19
@Author  :   victor2022
@Desc    :   connect wifi and set hostname
*/
bool connectWifi(int timeout, const char* ssid, const char* passwd){
    Serial.println();
    Serial.printf("Connecting to \"%s\"\n", ssid);

    /* Explicitly set the ESP8266 to be a WiFi-client, otherwise, it by default,
       would try to act as both a client and an access-point and could cause
       network-issues with your other WiFi-devices on your WiFi-network. */
    WiFi.mode(WIFI_STA);
    // 设置主机名
    WiFi.setHostname(name);
    // 尝试连接wifi
    WiFi.begin(ssid, passwd);

    // 等待连接wifi
    int i = 0;
    while (WiFi.status() != WL_CONNECTED&&i<timeout*2)
    {
        delay(500);
        Serial.print(".");
        // 超时处理
        i++;
    }
    Serial.println("");
    // 输出不同的数据进行处理
    // 连接成功
    if (WiFi.status()==WL_CONNECTED){
        Serial.printf("WiFi connected as: %s\n", WiFi.getHostname());
        Serial.print("IP address: ");
        Serial.println(WiFi.localIP());
        Serial.println();
        return true; 
    }
    Serial.println("WiFi connect failed!!!\n");
    return false;
}
