package cn.usr.middleware.pojo;

import lombok.*;

/**
 * @Package: cn.usr.core.pojo
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-04-18 14:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformDeviceRelation {

    /**
     * 设备的IMEI号
     */
    private String deviceImei;

    /**
     * 平台上的设备ID
     */
    private String platformDeviceId;

    /**
     * 平台的类别
     */
    private Integer platformType;

}
