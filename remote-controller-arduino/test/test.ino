// ESP32 利用EEPROM对数据进行读写操作
//保存WIFI信息，并且开机自动连接

#include <EEPROM.h>
#include <WiFi.h>
String ssid_s = "CU_wifi"; //  输入你的WIFI名称
String password_s = "li1045899571";  //  输入你的WIFI密码
String ssid;
String password;
String e_flag = "N"; //如果想要修改已保存的WIFI信息，改下这个字母就可以
char sd[30];
char pd[30];
void setup()
{

    Serial.begin(115200);
    Serial.println("");
    save_or_read_wifi(); //读取或者保存wifi信息
    Serial.println("wifi信息");
    Serial.print("ssid:");
    Serial.println(ssid);
    Serial.print("password:");
    Serial.println(password);

    strcpy(sd, ssid.c_str());     //需要转换格式
    strcpy(pd, password.c_str()); //需要转换格式

    WiFi.begin(sd, pd);
    while (WiFi.status() != WL_CONNECTED)
    {
        delay(500);
        Serial.print(".");
    }
    Serial.println("");
    Serial.println("连接wifi成功");
    Serial.print("IP地址：");
    Serial.println(WiFi.localIP());
}
void loop()
{
}

//读取或保存wifi等信息；
void save_or_read_wifi(void)
{
    EEPROM.begin(1024);
    if (get_string(1, 0) == e_flag)
    { //如果第0位字符为“M”，则表示已储存WIFI信息
        Serial.println("已储存WIFI信息");
        //提取信息
        ssid = get_string(EEPROM.read(10), 15);
        password = get_string(EEPROM.read(50), 55);
    }
    else
    {
        Serial.println("未储存有WIFI信息,将储存wifi信息");
        //保存信息
        set_string(10, 15, ssid_s, 1);     //保存wifi名称
        set_string(50, 55, password_s, 1); //保存wifi名称

        set_string(1, 0, e_flag, 0); //录入"N"字符
        Serial.println("完成储存WIFI信息");
        Serial.println("即将重启");
        ESP.restart();
    }

    EEPROM.end();
}

//用EEPROM的a位保存字符串的长度，字符串的从EEPROM的b位开始保存，str为要保存的字符串，s为是否保存字符串长度
void set_string(int a, int b, String str, int s)
{
    if (s)
        EEPROM.write(a, str.length()); // EEPROM第a位，写入str字符串的长度
    //通过一个for循环，把str所有数据，逐个保存在EEPROM
    for (int i = 0; i < str.length(); i++)
    {
        EEPROM.write(b + i, str[i]);
    }
    EEPROM.commit(); //执行保存EEPROM
}

//获取指定EEPROM位置的字符串，a是字符串长度，b是起始位，从EEPROM的b位开始读取
String get_string(int a, int b)
{
    String data = "";
    //通过一个for循环，从EEPROM中逐个取出每一位的值，并连接起来
    for (int i = 0; i < a; i++)
    {
        data += char(EEPROM.read(b + i));
    }
    return data;
}
