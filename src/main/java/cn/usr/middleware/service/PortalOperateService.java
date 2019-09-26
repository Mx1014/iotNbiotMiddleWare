package cn.usr.middleware.service;

import cn.usr.middleware.pojo.ResponseResult;
import cn.usr.middleware.pojo.UsrCommandResult;
import com.huawei.iotplatform.client.NorthApiException;

/**
 * @Package: cn.usr.middleware.service
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-04-20 11:03
 */
public interface PortalOperateService {


    /**
     * 添加设备
     *
     * @param imei
     * @param devName
     * @return
     */
    ResponseResult addDevice(String imei, String devName);


    /**
     * 删除设备
     *
     * @param deviceId
     * @return
     */
    ResponseResult deleteDevice(String deviceId);

    /**
     * 订阅平台消息之设备数据变化
     *
     * @param url
     */
    ResponseResult subscribeNotifyDeviceDataChanged(String url);

    /**
     * 订阅平台消息之设备信息变化
     *
     * @param url
     */
    ResponseResult subscribeNotifyDeviceInfoChanged(String url);

    /**
     * 订阅平台消息之设备绑定激活
     *
     * @param url
     */
    ResponseResult subscribeNotifyBindDevice(String url);

    /**
     * 订阅平台消息之添加新设备
     *
     * @param url
     */
    ResponseResult subscribeDeviceAdded(String url);


    /**
     * 订阅平台消息之删除设备
     *
     * @param url
     */
    ResponseResult subscribeDeviceDeleted(String url);

    /**
     * 订阅平台消息之响应命令
     *
     * @param url
     */
    ResponseResult subscribeCommandRsp(String url);


    /**
     * 下发指令
     *
     * @param deviceId
     * @param data
     * @return
     * @throws NorthApiException
     */
    UsrCommandResult postDeviceCommand(String deviceId, String data);

}
