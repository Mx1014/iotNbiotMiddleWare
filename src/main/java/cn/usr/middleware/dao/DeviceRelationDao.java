package cn.usr.middleware.dao;


import cn.usr.middleware.pojo.PlatformDeviceRelation;
import org.springframework.stereotype.Repository;

/**
 * @Package: cn.usr.cloud.db.dao
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-05-10 11:35
 */
@Repository
public interface DeviceRelationDao {

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
}
