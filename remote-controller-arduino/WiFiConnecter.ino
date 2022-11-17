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