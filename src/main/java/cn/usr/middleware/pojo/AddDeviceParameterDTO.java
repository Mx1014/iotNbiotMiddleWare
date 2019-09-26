package cn.usr.middleware.pojo;

import lombok.Data;

/**
 * @Package: cn.usr.middleware.pojo
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-04-23 11:45
 */
@Data
public class AddDeviceParameterDTO {

    /**
     * 设备的IMEI号
     */
    private String imei;

    /**
     * 设备的名称
     */
    private String devName;
}
