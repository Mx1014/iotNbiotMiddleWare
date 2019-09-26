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
public class UsrDeviceData {
    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备上报的数据
     */
    private String data;
}
