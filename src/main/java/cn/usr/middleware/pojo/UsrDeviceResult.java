package cn.usr.middleware.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Package: cn.usr.middleware.pojo
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-04-23 09:48
 */
@Data
@AllArgsConstructor
public class UsrDeviceResult {
    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备的
     */
    private String imei;
}
