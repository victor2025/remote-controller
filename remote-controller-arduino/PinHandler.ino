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
const int powerPin = 4; // D2 on wemos_d1_mini
const int modePin = 0; // D3
const int upPin = 12; // D6
const int downPin = 13; // D7
bool isOn = false; // 表示灯是否开

/*
@Time    :   2022/11/13 12:36:15
@Author  :   victor2022
@Desc    :   set pin modes
*/
void turnOff();
void setupPin(){
    pinMode(ledPin, OUTPUT);
    // power
    pinMode(powerPin, OUTPUT);
    // mode
    pinMode(modePin, OUTPUT);
    // up
    pinMode(upPin, OUTPUT);
    // down
    pinMode(downPin, OUTPUT);
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
@Desc    :   开关灯
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

/*
@Time    :   2022/11/16 23:02:25
@Author  :   victor2022
@Desc    :   开关灯
*/
void switchPower(){
    pulse(powerPin);
}

/*
@Time    :   2022/11/16 22:03:07
@Author  :   victor2022
@Desc    :   修改模式
*/
void switchMode(){
    pulse(modePin);
}

/*
@Time    :   2022/11/16 23:03:38
@Author  :   victor2022
@Desc    :   亮度调高
*/
void lightUp(){
    pulse(upPin);
}

/*
@Time    :   2022/11/16 23:03:22
@Author  :   victor2022
@Desc    :   亮度调低
*/
void lightDown(){
    pulse(downPin);
}

/*
@Time    :   2022/11/16 22:05:49
@Author  :   victor2022
@Desc    :   send pulse to a pin
*/
void pulse(int pinNum){
    digitalWrite(pinNum, LOW);
    delay(30);
    flash();
    digitalWrite(pinNum, HIGH);
}

void flash(){
    digitalWrite(ledPin,LOW);
    delay(1);
    digitalWrite(ledPin, HIGH);
}