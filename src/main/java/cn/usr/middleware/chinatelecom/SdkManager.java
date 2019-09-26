package cn.usr.middleware.chinatelecom;


import cn.usr.middleware.entity.SpPortalConstant;
import cn.usr.middleware.pojo.ResponseResult;
import cn.usr.middleware.pojo.UsrDeviceResult;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huawei.iotplatform.client.NorthApiClient;
import com.huawei.iotplatform.client.NorthApiException;
import com.huawei.iotplatform.client.dto.*;
import com.huawei.iotplatform.client.invokeapi.Authentication;
import com.huawei.iotplatform.client.invokeapi.DataCollection;
import com.huawei.iotplatform.client.invokeapi.DeviceManagement;
import com.huawei.iotplatform.client.invokeapi.SignalDelivery;
import com.huawei.iotplatform.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static cn.usr.middleware.entity.SpPortalConstant.*;


/**
 * @Package: cn.usr.middleware.manager
 * @Description: SdkManager和ChinaTelecomApiSdkManager 是分开的，此类只关心通过swagger调用的方式调用中间件。
 * AccessToken单独调用无缓存
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-04-17 16:29
 */
@Component
@Slf4j
public class SdkManager {

    @Value(value = "${portal.appid}")
    private String appid;

    @Value("${portal.secret}")
    private String secret;

    @Value("${portal.ip}")
    private String ip;

    @Value("${portal.port}")
    private String port;


    @Value("${cn.usr.commandname}")
    private String commandname;


    @Value("${cn.usr.serviceid}")
    private String serviceid;


    @Value("${cn.usr.generalvalue}")
    private String generalvalue;


    @Value("${cn.usr.device_respose_callbackurl}")
    private String deviceResposeCallbackurl;


    /**
     * 电信云sdk句柄
     */
    private NorthApiClient nac;

    private Boolean inited = false;


    @PostConstruct
    private void init() throws NorthApiException {
        synchronized (inited) {
            if (inited) {
                return;
            }
            nac = new NorthApiClient();
            ClientInfo ci = new ClientInfo();
            ci.setAppId(appid);
            ci.setSecret(secret);
            ci.setPlatformIp(ip);
            ci.setPlatformPort(port);
            nac.setClientInfo(ci);
            nac.initSSLConfig();
            inited = true;
        }
    }


    /**
     * 往电信平台上添加一个设备
     *
     * @param imei
     * @param devName
     * @return 如果添加成功返回设备在电信平台上的deviceId, 否则返回null
     */

    public ResponseResult addDevice(String imei, String devName) {
        try {
            DeviceManagement dm = new DeviceManagement(nac);
            RegDirectDeviceInDTO rddid = new RegDirectDeviceInDTO();
            rddid.setNodeId(imei);
            rddid.setVerifyCode(imei);
            rddid.setTimeout(0);

            RegDirectDeviceOutDTO rddod = dm.regDirectDevice(rddid, appid, getAccessToken());

            //修改设备，本接口在NB-IoT方案中必选，否则无法将设备与对应的Profile文件关联
            modifyDevice(dm, rddod.getDeviceId(), devName);

            return new ResponseResult("0", RESULT_SUCCESS, new UsrDeviceResult(rddod.getDeviceId(), rddod.getVerifyCode()));
        } catch (NorthApiException e) {
            e.printStackTrace();
            log.error("【添加设备失败】详细信息为：{}", e);
            return new ResponseResult(e.getError_code(), e.getError_desc(), null);
        }
    }

    /**
     * 修改设备，此接口必须调用，才能让添加的设备绑定profile
     *
     * @param dm
     * @param deviceId
     * @param devName
     * @throws NorthApiException
     */
    private void modifyDevice(DeviceManagement dm, String deviceId, String devName) throws NorthApiException {
        ModifyDeviceInfoInDTO mdiid = new ModifyDeviceInfoInDTO();
        mdiid.setName(devName);
        mdiid.setDeviceId(deviceId);
        mdiid.setManufacturerId(SpPortalConstant.MANUFACTURER_ID);
        mdiid.setManufacturerName(SpPortalConstant.MANUFACTURER_NAME);
        mdiid.setDeviceType(SpPortalConstant.DEVICE_TYPE);
        mdiid.setModel(SpPortalConstant.DEVICE_MODEL);
        mdiid.setProtocolType(SpPortalConstant.PROTOCOL_TYPE);
        dm.modifyDeviceInfo(mdiid, appid, getAccessToken());
    }


    /**
     * 删除一个设备
     *
     * @param deviceId
     * @return
     */
    public ResponseResult deleteDevice(String deviceId) {
        try {
            DeviceManagement dm = new DeviceManagement(nac);
            dm.deleteDirectDevice(deviceId, appid, getAccessToken());
            return new ResponseResult("0", RESULT_SUCCESS, null);
        } catch (NorthApiException e) {
            e.printStackTrace();
            log.error("【删除设备失败】详细信息为：{}", e);
            return new ResponseResult(e.getError_code(), e.getError_desc(), null);
        }
    }

    /**
     * 下发命令
     *
     * @param devId
     * @return
     */
    public PostDeviceCommandOutDTO postDeviceCommand(String devId, String value) {
        try {
            SignalDelivery signalDelivery = new SignalDelivery();

            signalDelivery.setNorthApiClient(nac);
            PostDeviceCommandInDTO postDeviceCommandInDTO = new PostDeviceCommandInDTO();
            AsynCommandDTO asynCommandDTO = new AsynCommandDTO();
            asynCommandDTO.setMethod(commandname);
            asynCommandDTO.setServiceId(serviceid);
            ObjectNode paras = JsonUtil.convertObject2ObjectNode(installData(value));
            asynCommandDTO.setParas(paras);
            postDeviceCommandInDTO.setCommand(asynCommandDTO);
            postDeviceCommandInDTO.setDeviceId(devId);
            postDeviceCommandInDTO.setExpireTime(0);

            log.info("给设备下发指令 所设置的回调地址为：{}",deviceResposeCallbackurl);
            postDeviceCommandInDTO.setCallbackUrl(deviceResposeCallbackurl);

            PostDeviceCommandOutDTO pdc = signalDelivery.postDeviceCommand(postDeviceCommandInDTO, appid, getAccessToken());
            return pdc;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【下发设备指令失败】详细信息为：{}", e);
            return null;
        }
    }


    /**
     * 订阅平台发送消息
     *
     * @throws NorthApiException
     */
    public ResponseResult subscribeNotify(final String key, final String callbackUrl) {
        try {
            SubscribeInDTO subscribeInDTO = new SubscribeInDTO();
            switch (key) {
                case DEVICEADDED:
                    subscribeInDTO.setNotifyType(DEVICEADDED);
                    break;
                case DEVICEDATACHANGED:
                    subscribeInDTO.setNotifyType(DEVICEDATACHANGED);
                    break;
                case DEVICEDELETED:
                    subscribeInDTO.setNotifyType(DEVICEDELETED);
                    break;
                case COMMANDRSP:
                    subscribeInDTO.setNotifyType(COMMANDRSP);
                    break;
                case BINDDEVICE:
                    subscribeInDTO.setNotifyType(BINDDEVICE);
                    break;
                case DEVICEINFOCHANGED:
                    subscribeInDTO.setNotifyType(DEVICEINFOCHANGED);
                    break;
                default:
                    break;
            }
            subscribeInDTO.setCallbackurl(callbackUrl);
            DataCollection dataCollection = new DataCollection(nac);
            dataCollection.subscribeNotify(subscribeInDTO, getAccessToken());
            return new ResponseResult("0", RESULT_SUCCESS, null);
        } catch (NorthApiException e) {
            e.printStackTrace();
            log.error("【订阅平台数据失败】详细信息为：{}", e);
            return new ResponseResult(e.getError_code(), e.getError_desc(), null);
        }

    }

    /**
     * 获取登陆凭证
     *
     * @return
     * @throws NorthApiException
     */
    private String getAccessToken() throws NorthApiException {
        synchronized (nac) {
            Authentication authentication = new Authentication(nac);
            AuthOutDTO aod = authentication.getAuthToken();
            log.info("getAccessToken :{}", aod.getAccessToken());
            return aod.getAccessToken();
        }
    }

    /**
     * 组装数据
     *
     * @param value
     * @return
     */
    private String installData(String value) {
        String data = "{\"DeviceReceive\":\"" + generalvalue + "\"}";
        return data.replaceAll(generalvalue, value);
    }
}
