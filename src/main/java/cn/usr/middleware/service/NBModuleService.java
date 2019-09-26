package cn.usr.middleware.service;


import cn.usr.middleware.pojo.IotDeviceDTO;
import cn.usr.middleware.pojo.PlatformDeviceRelation;

/**
 * @Package: cn.usr.cloud.db.service
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-05-10 11:34
 */
public interface NBModuleService {


    /**
     * 平台设备关联信息 根据Imei
     *
     * @param imei
     * @return
     */
    PlatformDeviceRelation selectDevRelationByIMEI(String imei);

    /**
     * 平台设备关联信息 根据平台设备ID
     *
     * @param deviceId
     * @return
     */
    PlatformDeviceRelation selectDevRelationByDeviceId(String deviceId);


    /**
     * 获取设备IotDeviceDTO息 根据设备ID
     *
     * @param devId
     * @return
     */
    IotDeviceDTO selectIotDeviceDtoByDeviceId(String devId);

    /**
     * 删除设备缓存信息
     *
     * @param devId
     * @return
     */
    void removeIotDeviceDtoForRedis(String devId);
}
