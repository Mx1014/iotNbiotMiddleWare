package cn.usr.middleware.service.impl;


import cn.usr.middleware.chinatelecom.SdkManager;
import cn.usr.middleware.pojo.ResponseResult;
import cn.usr.middleware.pojo.UsrCommandResult;
import cn.usr.middleware.service.PortalOperateService;
import com.huawei.iotplatform.client.dto.PostDeviceCommandOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cn.usr.middleware.entity.SpPortalConstant.*;

/**
 * @Package: cn.usr.middleware.service.impl
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-04-20 11:04
 */
@Service
@Slf4j
public class PortalOperateServiceImpl implements PortalOperateService {


    @Autowired
    private SdkManager sdkManager;


    @Override
    public ResponseResult addDevice(String imei, String devName) {
        return sdkManager.addDevice(imei, devName);
    }

    @Override
    public ResponseResult deleteDevice(String deviceId) {
        return sdkManager.deleteDevice(deviceId);
    }

    @Override
    public ResponseResult subscribeNotifyDeviceDataChanged(String url) {
        return sdkManager.subscribeNotify(DEVICEDATACHANGED, url);

    }

    @Override
    public ResponseResult subscribeNotifyDeviceInfoChanged(String url) {
        return sdkManager.subscribeNotify(DEVICEINFOCHANGED, url);
    }

    @Override
    public ResponseResult subscribeNotifyBindDevice(String url) {
        return sdkManager.subscribeNotify(BINDDEVICE, url);
    }

    @Override
    public ResponseResult subscribeDeviceAdded(String url) {
        return sdkManager.subscribeNotify(DEVICEADDED, url);
    }

    @Override
    public ResponseResult subscribeDeviceDeleted(String url) {
        return sdkManager.subscribeNotify(DEVICEDELETED, url);
    }

    @Override
    public ResponseResult subscribeCommandRsp(String url) {
        return sdkManager.subscribeNotify(COMMANDRSP, url);
    }

    @Override
    public UsrCommandResult postDeviceCommand(String deviceId, String data) {
        PostDeviceCommandOutDTO dto = sdkManager.postDeviceCommand(deviceId, data);
        log.debug("【PostDeviceCommandOutDTO】：{}", dto);
        return new UsrCommandResult("", deviceId, dto.getCommandId(), dto.getStatus(), dto.getExpireTime(), dto.getCreationTime());

    }
}
