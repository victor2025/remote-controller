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
bool set_string(int addr, int size, String str)
{
    // 保证长度不超过最大限制
    size = size<str.length()?size:str.length();
    EEPROM.write(addr, size); // EEPROM第addr位，写入str字符串的长度
    //通过一个for循环，把str所有数据，逐个保存在EEPROM
    for (int i = 0; i < size; i++)
    {
        EEPROM.write(addr + i +1, char(str[i]));
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
    // 读取长度
    byte tempSize = EEPROM.read(addr);
    if(tempSize>0){
        size = tempSize;
    }
    addr++;
    // 读取数据
    String data = "";
    //通过一个for循环，从EEPROM中逐个取出每一位的值，并连接起来
    for (int i = 0; i < size; i++)
    {
        data += char(EEPROM.read(addr + i));
    }
    return data;
}

/*
@Time    :   2022/11/13 22:50:57
@Author  :   victor2022
@Desc    :   清空数据
*/
void clear(int addr, int size)
{
    for (int i = addr; i <= addr+size; i++)
    {
        EEPROM.write(i, 0);
    }
    EEPROM.commit();
}

// 设置wifi信息
bool saveSsid(const char* str){
    clear(ssid_addr, ssid_len);
    return set_string(ssid_addr,ssid_len, str); //保存wifi名称
}

bool savePasswd(const char* str){
    clear(pswd_addr, pswd_len);
    return set_string(pswd_addr,pswd_len, str); //保存wifi密码
}

bool saveSign(){
    clear(sign_addr,1);
    return set_string(sign_addr,1,sign);    
}

String getSsid(){
    return get_string(ssid_addr, EEPROM.read(ssid_len));
}

String getPasswd(){
    return get_string(pswd_addr, EEPROM.read(pswd_len));
}

String getSign(){
    return get_string(sign_addr, 1);
}

/*
@Time    :   2022/11/13 20:54:00
@Author  :   victor2022
@Desc    :   保存wifi相关信息并重启
*/
void saveWifiInfoAndReset(const char* ssid, const char* passwd){
    // 清空数据
    Serial.printf("Writing data: %s, %s\n", ssid, passwd);
    // 写入新数据
    saveSsid(ssid);
    savePasswd(passwd);
    saveSign();
    delay(5000);
    // 重启
    Serial.println("\nRebooting...");
    ESP.restart();
}