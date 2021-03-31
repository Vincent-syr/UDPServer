## master-slave架构

! [image](https://github.com/Vincent-syr/UDPServer/blob/main/image/master-slave.png)

- master采用轮训的方式分配slave
- master和slave之间保持心跳和数据同步





## 收包发包模型

![image](https://github.com/Vincent-syr/UDPServer/blob/main/image/send-receive.png)

- 一个receive线程
- 多个worker线程
- 一个send线程









