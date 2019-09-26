package cn.usr.middleware.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * @author liu
 * @date 2017-05-20
 * 设备
 */
@Data
public class Device implements Serializable {
    private long id;

    /**
     * 设备的唯一字符串ID
     */
    private String devId;
    private String devName;

    /**
     * 设备通信密码
     */
    private String password;

    /**
     * //没有用枚举是因为后期如果增加一种类型，需要修改枚举类，所有使用该包的的程序都需要改。因此这里不用枚举，虽然可读性不好
     * //协议类型 UNKNOWN(-100),MODBUS_RTU(0),MODBUS_TCP(1),TRAN(2);
     */
    private int protocol;

    /**
     * 设备类型:0默认设备即tcp，
     * 1:lora集中器，
     * 2:coap设备
     */
    private int devType;

    /**
     * 轮询间隔
     */
    private int pollingInterval;

    /**
     * 产品型号
     */
    private int productModel;

    /**
     * 用户ID
     */
    private long userId;

    /**
     * 扩展字段 1，共享支付设备本字段的值为"sharepay"
     * */
    private String custom1;

    /**
     * 自定义合包长度
     * */
    private int packetLength;



}
