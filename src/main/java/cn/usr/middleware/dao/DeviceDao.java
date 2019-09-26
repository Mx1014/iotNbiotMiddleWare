package cn.usr.middleware.dao;


import cn.usr.middleware.entity.Device;
import cn.usr.middleware.pojo.IotDeviceDTO;
import org.springframework.stereotype.Repository;

/**
 * @author zhiyuan
 * Created by liu on 2017-05-20.
 */
@Repository
public interface DeviceDao {
    /**
     * 获取设备信息 根据设备ID
     *
     * @param devId
     * @return
     */
    Device getByDevId(String devId);


    /**
     * 获取设备IotDeviceDTO息 根据设备ID
     *
     * @param devId
     * @return
     */
    IotDeviceDTO selectIotDeviceDtoByDeviceId(String devId);
}
