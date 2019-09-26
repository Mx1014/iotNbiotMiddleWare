package cn.usr.middleware.chinatelecom;


import cn.usr.middleware.pojo.IotTaskQueueDTO;
import cn.usr.middleware.pojo.PlatformDeviceRelation;
import cn.usr.middleware.service.NBModuleService;
import cn.usr.middleware.service.TaskService;
import cn.usr.middleware.util.ProtoStuffSerializerUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huawei.iotplatform.client.NorthApiClient;
import com.huawei.iotplatform.client.NorthApiException;
import com.huawei.iotplatform.client.dto.*;
import com.huawei.iotplatform.client.invokeapi.Authentication;
import com.huawei.iotplatform.client.invokeapi.DataCollection;
import com.huawei.iotplatform.client.invokeapi.SignalDelivery;
import com.huawei.iotplatform.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static cn.usr.middleware.entity.SpPortalConstant.*;
import static cn.usr.middleware.util.EhcacheUtil.deviceRelationCache;


/**
 * @author 石志远
 * @Description 此类只服务于有人云下发数据，accesstoken 是单独通过redis和api服务共同管理调用
 */
@Slf4j
@Component
public class ChinaTelecomApiSdkManager {


    @Value(value = "${portal.appid}")
    private String appid;

    @Value("${portal.secret}")
    private String secret;

    @Value("${portal.ip}")
    private String ip;

    @Value("${portal.port}")
    private String port;

    /**
     * 电信sdk句柄
     */
    private NorthApiClient nac;
    private Boolean inited = false;

    @Autowired
    private TaskService taskService;

    @Autowired
    private NBModuleService nbModuleService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 下发命令回调地址，在配置文件中初始化
     */
    @Value("${cn.usr.device_respose_callbackurl}")
    private String deviceResposeCallbackurl;


    /**
     * 通用 初始化电信云sdk句柄
     *
     * @throws NorthApiException
     */
    private void initChinaTelecom() throws NorthApiException {
        synchronized (inited) {
            if (inited) {
                return;
            }
            nac = new NorthApiClient();
            ClientInfo ci = new ClientInfo();
            ci.setAppId(appid);
            ci.setPlatformIp(ip);
            ci.setPlatformPort(port);
            ci.setSecret(secret);
            nac.setClientInfo(ci);
            nac.initSSLConfig();
            inited = true;
        }
    }

    /**
     * 通用 获取电信云SDK调用凭证，通过redis和有人云API服务器共同管理
     *
     * @return
     * @throws NorthApiException
     */
    private String getAccessToken() throws NorthApiException {
        synchronized (nac) {
            String token = stringRedisTemplate.opsForValue().get(KEY_TOKEN);
            if (token != null) {
                return token;
            }
            Authentication authentication = new Authentication(nac);
            AuthOutDTO aod = authentication.getAuthToken();
            log.info("getAccessToken :{}", aod.getAccessToken());
            stringRedisTemplate.opsForValue().set(KEY_TOKEN, aod.getAccessToken(), aod.getExpiresIn() - 300, TimeUnit.SECONDS);
            return aod.getAccessToken();
        }
    }

    /**
     * 下发命令
     *
     * @param devId
     * @return
     */
    public PostDeviceCommandOutDTO postDeviceCommand2(String devId, String value) throws Exception {

        initChinaTelecom();
        SignalDelivery signalDelivery = new SignalDelivery();
        signalDelivery.setNorthApiClient(nac);
        PostDeviceCommandInDTO postDeviceCommandInDTO = new PostDeviceCommandInDTO();
        AsynCommandDTO asynCommandDTO = new AsynCommandDTO();
        asynCommandDTO.setMethod(COMMAND_NAME);
        asynCommandDTO.setServiceId(SERVICEID);
        ObjectNode paras = JsonUtil.convertObject2ObjectNode(installData(value));
        asynCommandDTO.setParas(paras);

        postDeviceCommandInDTO.setCommand(asynCommandDTO);
        postDeviceCommandInDTO.setDeviceId(devId);
        postDeviceCommandInDTO.setExpireTime(0);


        log.info("给设备下发指令 所设置的回调地址为：{}", deviceResposeCallbackurl);
        // 此处下发命令必须使用这里设置，应用响应回调的方式对NB不适用
        postDeviceCommandInDTO.setCallbackUrl(deviceResposeCallbackurl);
        try {
            PostDeviceCommandOutDTO pdc = signalDelivery.postDeviceCommand(postDeviceCommandInDTO, appid, getAccessToken());

            // 修改有人云指令下发状态
            updateCloudDBState(pdc);
            return pdc;
        } catch (NorthApiException e) {
            e.printStackTrace();
            log.error("[电信云下发指令异常]异常码为：{}，异常描述为：{}", e.getError_code(), e.getError_desc());
            return null;
        }
    }

    /**
     * 修改下发命令的状态
     *
     * @param pdc
     */
    private void updateCloudDBState(PostDeviceCommandOutDTO pdc) {
        log.info("下发命令返回的值：{}", pdc);
        try {
            IotTaskQueueDTO iotTaskQueueDO = new IotTaskQueueDTO();
            iotTaskQueueDO.setCommandid(pdc.getCommandId());
            byte[] bytes = deviceRelationCache.get(pdc.getDeviceId());
            PlatformDeviceRelation platformDeviceRelation;
            if (bytes != null) {
                platformDeviceRelation = ProtoStuffSerializerUtil.deserialize(bytes, PlatformDeviceRelation.class);
            } else {
                platformDeviceRelation = nbModuleService.selectDevRelationByDeviceId(pdc.getDeviceId());
                deviceRelationCache.put(pdc.getDeviceId(), ProtoStuffSerializerUtil.serialize(platformDeviceRelation));
            }
            iotTaskQueueDO.setDid(platformDeviceRelation.getDeviceImei());
            JSONObject jsonObject = JSON.parseObject((pdc.getCommand().getParas()).toString());
            iotTaskQueueDO.setData(jsonObject.getString("DeviceReceive"));
            switch (pdc.getStatus()) {
                case STATE_PENDING:
                    iotTaskQueueDO.setState(1);
                    break;
                case STATE_DEFAULT:
                    iotTaskQueueDO.setState(1);
                    break;
                case STATE_SENT:
                    iotTaskQueueDO.setState(2);
                    break;
                case STATE_EXPIRED:
                    iotTaskQueueDO.setState(4);
                    break;
                case STATE_FAILED:
                    iotTaskQueueDO.setState(4);
                    break;
                case STATE_TIMEOUT:
                    iotTaskQueueDO.setState(4);
                    break;
                case STATE_SUCCESSFUL:
                    iotTaskQueueDO.setState(3);
                    break;
                default:
                    iotTaskQueueDO.setState(1);
                    break;
            }

            taskService.addTaskByIotTaskQueue(iotTaskQueueDO);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("【下发命令更改透传云数据库状态异常】：{}", e);
        }
    }

    /**
     * 组装数据
     *
     * @param value
     * @return
     */
    private String installData(String value) {
        String data = "{\"DeviceReceive\":\"" + GENERAL_VALUE + "\"}";
        return data.replaceAll(GENERAL_VALUE, value);
    }


    /**
     * 电信消息回调 应用订阅API实例
     * (测试调用)
     *
     * @throws NorthApiException
     */
    private void subscribeNotify() throws NorthApiException {
        SubscribeInDTO subscribeInDTO = new SubscribeInDTO();
        // 设备数据变化:  IoT平台接收到设备数据变化（动态变化，如，服务属性值的变化），IoT平台使用该接口功能将该信息通知给Application。
//        subscribeInDTO.setNotifyType("deviceDataChanged");
//        subscribeInDTO.setCallbackurl("https://cloudcoap.usr.cn:8743/coap/subscribe/CallbackDataChange");


        // 响应命令:  IoT平台接收到设备（包括网关设备和普通设备）的响应命令（如下发命令给设备，设备执行命令后，发送响应命令给平台，如视频呼叫、视频录制，抓图等），将该消息通知给Application
//        subscribeInDTO.setNotifyType("commandRsp");
//        subscribeInDTO.setCallbackurl("https://cloudcoap.usr.cn:8743/coap/subscribe/CallbackDeviceResponse");

        // 设备绑定激活: 即设备初始登陆通知，网关设备在平台注册绑定（网关设备在平台创建对应设备的信息并获取密码的过程），平台通知给NA。
        subscribeInDTO.setNotifyType("bindDevice");
        subscribeInDTO.setCallbackurl("https://cloudcoap.usr.cn:8743/coap/subscribe/CallbackStateChange");


        DataCollection dataCollection = new DataCollection();
        dataCollection.setNorthApiClient(nac);
        dataCollection.subscribeNotify(subscribeInDTO, getAccessToken());

    }


}
