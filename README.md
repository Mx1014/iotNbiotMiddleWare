# 透传云对接电信物联网平台通讯服务

语雀文档地址：https://www.yuque.com/docs/share/d60bb3b4-fc85-4f4f-8d49-2e119baa7a14
# 透传云对接电信物联网平台通讯服务

<a name="9aaf403a"></a>
## 硬件资料
面向对象 WH-NB模组<br />
![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551759541348-1b6b6e3c-0c52-41d7-91ce-eddb5084f1e3.png#align=left&display=inline&height=1212&name=image.png&originHeight=1212&originWidth=2844&size=1180150&status=done&width=2844)

<a name="1532cf1f"></a>
### 硬件应用场景

![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551759707577-ed806377-c2d3-4792-a15f-59acba9c8f11.png#align=left&display=inline&height=1378&name=image.png&originHeight=1378&originWidth=2402&size=1003722&status=done&width=2402)

<a name="d56f5820"></a>
## 项目背景

有人透传云支持 CoAP 协议接入，用户只需要在透传云进行设备的添加，就可以实现将用户发送的数据发
送到透传云服务器上，用户可以基于透传云的二次开发 SDK 读取到
用户的数据和向用户设备发送指定的数据。
<br /><br /><br />**目前电信运营商不允许将数据发往私有 IP，所以使用电信运营商的客户只能访问电信云物联网云平台，
而这个平台入门难度高，设计复杂，导致客户在接入时存在困难，有人透传云简单的授权和配置使用方式方
便用户快速上线项目。
**

<a name="ce308636"></a>
## 项目作用

对接电信物联网平台，通过电信云平台**完成设备数据的上传和下发**，然后对接到透传云上的消息体系，增加配合对电信云的调试功能。

<a name="447564e4"></a>
## 资料

电信开发测试平台（新）：[https://180.101.147.135:8843/](https://180.101.147.135:8843/#/)<br />账号 `yourenwulian2017`  密码 `Wangyufeng!@34`  

电信生产平台：[https://180.101.146.80:8843/main.html#/](https://180.101.146.80:8843/main.html#/)<br />账号 `jinanyouren`  密码：`jinanyourenNBIOT#2018` 


<a name="1bbbb204"></a>
## 注意事项
1. 使用调试api添加设备的时候，IMEI必须为有人的卡段开头的。"35656607", "86386604", "86822104", "86433304"
1. 会有俩个人的代码风格和不同时期的理解问题，如果接手的人有时间的话，可以考虑整合。蹦木莎卡拉卡

<a name="33952f92"></a>
## 开发测试过程

1. 创建应用 
  1. 设置应用省电参数配置为 DRX 模式![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551687945369-5e1fca4a-37fa-453e-88fa-dde33adb4bdd.png#align=left&display=inline&height=337&name=image.png&originHeight=674&originWidth=1932&size=118779&status=done&width=966)
  1. 上传profile文件![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551688024360-8ca8b026-17c7-4f38-a4d7-a565ff1fd067.png#align=left&display=inline&height=626&name=image.png&originHeight=1252&originWidth=3304&size=239035&status=done&width=1652)

1. 配置程序中的配置文件
  1. appid+secret  
    1. ![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551688075726-57b9927b-3961-45f3-9919-afb1a151149b.png#align=left&display=inline&height=188&name=image.png&originHeight=376&originWidth=742&size=56225&status=done&width=371)
  1. web基本配置
    1. <br />![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551688579950-8681672a-c0cb-4b42-ba2b-cca6732967de.png#align=left&display=inline&height=109&name=image.png&originHeight=218&originWidth=646&size=26045&status=done&width=323)
  1. 其他自己看着配置

1. 手动订阅（可选）
  1. 访问程序中的 swagger-ui 界面；举例：  http://47.101.189.88:8882/coap/swagger-ui.html
  1. 执行订阅平台业务管理数据，只需要订阅 **设备数据变化，设备信息变化** 两个就可以其他不要管；举例：
    1. `http://47.101.189.88:8882/coap/subscribe/CallbackDataChange` 

                ![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551688360782-516413c1-f3ec-41dd-844a-3835d04705dd.png#align=left&display=inline&height=236&name=image.png&originHeight=1166&originWidth=1912&size=175086&status=done&width=387) 

1. 检查订阅配置
  1. <br />![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551688775305-4f119f83-53ba-4c3b-b9a6-7e75b804fbb1.png#align=left&display=inline&height=398&name=image.png&originHeight=796&originWidth=2042&size=122032&status=done&width=1021)

---
<a name="d41d8cd9"></a>
### 
<a name="20170634"></a>
### 初始化项目流程


![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551427953161-4699c7c5-7b84-41e6-857b-84d0622bf38e.png#align=left&display=inline&height=610&name=image.png&originHeight=610&originWidth=917&size=127337&status=done&width=917)


<a name="94b85fb7"></a>
### 设备上行数据全流程


![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551671446643-6d9a99d3-1fa8-42d9-8280-913029262b32.png#align=left&display=inline&height=548&name=image.png&originHeight=357&originWidth=396&size=42776&status=done&width=608)

<a name="a6feec13"></a>
### 命令下发处理流程


![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551671477996-d7ed171e-85d9-4bfa-9119-46bee9200abc.png#align=left&display=inline&height=554&name=image.png&originHeight=373&originWidth=396&size=47857&status=done&width=588)


> 当设备收到命令，模组会自动回复一个ACK（2.04 Changed）命令应答（此为CoAP协议层的应答），命令的状态由“已发送”状态变为“已送达”状态。


> 当设备执行完命令，上报一条命令执行成功结果的码流并经插件decode解析，上报deviceRsp时，命令状态由“已送达”变为“成功”。


> 在命令经过平台发送后，在一定时间内，如果设备没有返回ACK（2.04 Changed）命令应答，则命令状态会变成“超时”。


<a name="b36bab54"></a>
#### 命令立即下发处理流程

![image.png](https://cdn.nlark.com/yuque/0/2019/png/219184/1551671743625-b2ccf48e-4517-4ddf-a6da-f8d5da04112f.png#align=left&display=inline&height=437&name=image.png&originHeight=343&originWidth=396&size=41606&status=done&width=505)

1. 应用服务器调用北向接口立即下发命令，参数expireTime传0表示立即下发，
1. 平台收到命令后，调用插件编码，将命令下发给对应插件的encode函数，encode入参样例如下：
1. 平台将编码后的命令向设备下发。
1. 平台向应用返回200 OK响应，更新命令状态为SENT，表示命令已经下发。
1. NB模组收到命令时，回CoAP协议的ACK消息（注：ACK消息对设备应用不可见）
1. 平台收到ACK后，认为命令送达设备。向NA推送送达通知。消息样例：

------以下操作我们的应用不涉及
1. UE执行完命令，如果需要上报命令执行结果，则上报执行结果
1. 平台调用插件解码，解码输出样例： 其中mid参数表示命令标识，应该和encode输入的一致
1. 平台根据mid查找命令，并给NA上报命令响应通知，如果没有mid，那么平台不去匹配命令来更新命令成功或失败的状态，推送给NA的commandID为null。插件收到命令执行结果后，decode函数解析完成上报的命令应答json样例：

<a name="eb9d0685"></a>
## 代码初始化流程

项目分为两部分
1. 设备数据的收发
1. 设备的添加以及订阅等动作


<a name="18e5e771"></a>
#### 设备上线后收到的回调

> 在电信云中一个设备初始周期应该是    注册-->绑定--->激活 


<a name="973a0315"></a>
## 回调地址可以修改吗

可以。如果回调地址的IP和端口号需要更换，需要调用“批量删除订阅”接口，删除之前的回调地址后，再重新订阅。

<a name="c8759551"></a>
## 之前是在线的设备，为何一段时间之后变为了异常或者离线状态
<a name="af4ed20d"></a>
## 设备在25个小时内未上报数据，会刷新状态为异常；在49小时内未上报数据，会刷新状态为离线

---
<a name="ba0afce7"></a>
## 全链路联调

测试使用设备：imei: 868221040020292  SN：089201806002030

1. 首先部署一套 api 里面的电信appid+secret 为


<br />**appid**: J8nPLOrGb_EmEEV0q9ztNvwhXfIa<br />**       secret**: jkGqmGMWufZfGic9fuprMXNh7P8a<br />

> 这样是为了将NB设备加到透传云的体系中


1. 如果有前端可以用的话，就用前端。没有前端就部署一套center使用sdk 调用

---

## 电信NBIOT设备快速检测上下线开发

实现方案：

- 设备上线或者是发送数据后往redis插入一条记录。 记录的格式为：HASH结构的，key为deviceid，value为当前时间的时间戳。
- 然后在程序启动的时候启动一个定时任务，去定时遍历HASH表里的设备列表，取出设备ID对应的最后一条动作时间，然后和当前时间进行对比，如果超过20分钟，就代表设备异常发送设备下线通知，然后将设备在redis的记录删除。

