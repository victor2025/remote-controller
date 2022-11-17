#line 1 "/home/victor2022/projects/Arduino/remote-controller/readme.md"
# 基于ESP8266的家电远程控制方案

## 启动流程
```
启动读取EEPROM中的wifi数据，尝试连接wifi -> 成功则开启控制服务器
            |
            V
失败则开启AP服务(ap_of_esp)，并开启连接服务器 -> 通过连接服务器写入wifi信息
                                            |
                                            V
                            重新启动  <- 存入EEPROM 
```