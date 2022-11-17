/*
-*- encoding: utf-8 -*-
@File    :   ControlWebServer.ino
@Time    :   2022/11/12 22:45:10
@Author  :   victor2022
@Version :   1.0
@Desc    :   web server of arduino for control
*/
#include <ESP8266WebServer.h>

const String PREFIX = "remote-controller:";
const String NAME = "ESP-victor2022";
const String identity = PREFIX+NAME;

const String indexPage = "<html><head>    <title>Remote Controller Config Page</title>    <style>     body {            background-color: #cccccc;            font-family: Arial, Helvetica, Sans-Serif;            Color: #000088;        }    </style></head><body>    <!-- update info -->    <h1>Update wifi config</h1>    <form method=\"post\" action=\"/saveinfo/\" enctype=\"application/x-www-form-urlencoded\">        <dev>SSID: </dev><input type=\"text\" name=\"ssid\"></input><br><br>        <dev>Password: </dev><input type=\"password\" name=\"passwd\"></input><br><br>        <input type=\"submit\" value=\"Submit\">    </form>    <!-- switch -->    <h1>Switch led</h1>    <form method=\"get\" action=\"/switch/\" target=\"switchPage\">        <input type=\"submit\" value=\"Submit\">    </form>    <iframe id=\"switchPage\" name=\"switchPage\" hidden=\"true\"></iframe>    <!-- power -->    <h1>Power</h1>    <form method=\"get\" action=\"/power/\" target=\"switchPage\">        <input type=\"submit\" value=\"Submit\">    </form>    <iframe id=\"powerPage\" name=\"powerPage\" hidden=\"true\"></iframe>    <!-- mode -->    <h1>Mode</h1>    <form method=\"get\" action=\"/mode/\" target=\"switchPage\">        <input type=\"submit\" value=\"Submit\">    </form>    <iframe id=\"modePage\" name=\"modePage\" hidden=\"true\"></iframe>    <!-- brighter -->    <h1>Brighter</h1>    <form method=\"get\" action=\"/up/\" target=\"switchPage\">        <input type=\"submit\" value=\"Submit\">    </form>    <iframe id=\"upPage\" name=\"upPage\" hidden=\"true\"></iframe>    <!-- darker -->    <h1>Darker</h1>    <form method=\"get\" action=\"/down/\" target=\"switchPage\">        <input type=\"submit\" value=\"Submit\">    </form>    <iframe id=\"downPage\" name=\"downPage\" hidden=\"true\"></iframe></body></html>";

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
    server.on("/identity/",handleIdentity); // for scanning
    server.on("/switch/", handleSwitch);
    // save wifi info
    server.on("/saveinfo/", handleSave);
    // handle power
    server.on("/power/", handlePower);
    // handle light mode
    server.on("/mode/", handleMode);
    // handle light up
    server.on("/up/", handleLightUp);
    // handle light down
    server.on("/down/", handleLightDown);
    server.onNotFound(handleNotFound);
    server.begin();
    Serial.println("Server started...");
}

/*
@Time    :   2022/11/12 23:07:02
@Author  :   victor2022
@Desc    :   处理用户请求
*/
void serve(){
    server.handleClient();
}


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
@Time    :   2022/11/16 23:27:22
@Author  :   victor2022
@Desc    :   send identity string for scanning
*/
void handleIdentity(){
  Serial.println("identity is visited...");
  sendString(identity);
}

/*
@Time    :   2022/11/13 12:50:32
@Author  :   victor2022
@Desc    :   switch page
*/
void handleSwitch(){
  Serial.println("switch is visited...");
  String res = switchLed();
  sendString(res);
}

/*
@Time    :   2022/11/16 23:12:30
@Author  :   victor2022
@Desc    :   switch power
*/
void handlePower(){
  Serial.println("power is visited...");
  switchPower();
  sendString("success");
}

/*
@Time    :   2022/11/16 22:06:38
@Author  :   victor2022
@Desc    :   swich light mode
*/
void handleMode(){
  Serial.println("mode is visited...");
  switchMode();  
  sendString("success");
}

void handleLightUp(){
  Serial.println("light up is visited...");
  lightUp();
  sendString("success");
}

void handleLightDown()
{
  Serial.println("light down is visited...");
  lightDown();
  sendString("success");
}

void sendString(String msg){
  server.send(200, "text/plain", msg);
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
  server.send(200, "text/plain", "update success, device will reboot in 5 seconds...");
  saveWifiInfoAndReset(ssid,passwd);
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

