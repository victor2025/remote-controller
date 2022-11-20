# 针对ESP系列IOT芯片的远程控制方案

## 介绍
- 本项目是针对ESP系列IOT芯片的基于WiFi的远程控制方案，实现了局域网中IOT设备的配置、发现和控制的整个流程

- 方案包含两个部分：
  - [IOT设备固件(remote-controller-arduino)](remote-controller-arduino/readme.md)
  - [Android远程控制软件(remote-controller-app)](remote-controller-app/readme.md)

- app与远程设备之间依靠http协议进行通信