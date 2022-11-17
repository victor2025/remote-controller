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