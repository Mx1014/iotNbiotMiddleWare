package cn.usr.middleware.pojo;

import lombok.Data;

/**
 * @author zhiyuan
 * @desc 石志远封装的一个DTO 可以直接获取到设备层到Center所需的设备Dto
 */
@Data
public class IotDeviceDTO {
    /**
     * 设备ID
     */
    private Integer id;

    /**
     * 设备IMEI
     */
    private String devid;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备名称
     */
    private Integer type;

    /**
     * 通讯协议
     */
    private Integer protocol;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 用户名
     */
    private String account;

    /**
     * 父用户id
     */
    private Integer puid;

    /**
     * 用户名
     */
    private String pAccount;
}
