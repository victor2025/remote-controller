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