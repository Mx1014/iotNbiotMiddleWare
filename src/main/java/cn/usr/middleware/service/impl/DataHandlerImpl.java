package cn.usr.middleware.service.impl;

import cn.usr.cloud.common.base.DeviceDTO;
import cn.usr.cloud.common.base.UserDTO;
import cn.usr.cloud.common.enumu.DeviceStateEnum;
import cn.usr.cloud.common.mqmessage.devicetocenter.DeviceToCenterDataMessage;
import cn.usr.cloud.common.mqmessage.devicetocenter.DeviceToCenterStateMessage;
import cn.usr.middleware.mqtt.UsrCloudMqttClient;
import cn.usr.middleware.pojo.*;
import cn.usr.middleware.service.DataHandler;
import cn.usr.middleware.service.NBModuleService;
import cn.usr.middleware.service.TaskService;
import cn.usr.middleware.util.ProtoStuffSerializerUtil;
import cn.usr.middleware.util.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;

import static cn.usr.middleware.entity.SpPortalConstant.*;
import static cn.usr.middleware.util.BaseUtils.hexStringToBytes;
import static cn.usr.middleware.util.EhcacheUtil.deviceRelationCache;
import static com.alibaba.fastjson.JSON.parseObject;


/**
 * @author zhiyuan
 * 数据处理实现类
 */
@Service
@Slf4j
@Scope("prototype")
public class DataHandlerImpl implements DataHandler {


    @Autowired
    private NBModuleService nbModuleService;


    @Autowired
    private TaskService taskService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${mq.exchange.chinatelcom}")
    private String exchangeChinatelcom;

    @Value("${mq.routingkey.to_center_data}")
    private String routingkeyToCenterData;

    @Value("${mq.routingkey.online}")
    private String online;

    @Value("${mq.routingkey.offline}")
    private String offline;

    @Value("${cn.device.lastActionKey}")
    private String lastActionKey;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * mqtt客户端句柄
     */
    private UsrCloudMqttClient usrCloudMqttClient = SpringUtil.getBean(UsrCloudMqttClient.class);


    /**
     * 有人云设备对接协议 t 主题 payload头部
     * Ver (1) ：0x01
     * MessageId(1) :0x00
     */
    private final byte[] head = {(byte) 0x01, (byte) 0x00};


    @Override
    public void dataChangeHandler(String data) {
        if (StringUtils.isEmpty(data)) {
            return;
        }
        JSONObject jsonObject = null;
        CoAPServiceDTO coAPServiceDTO = null;
        IotDeviceDTO iotDeviceDTO = null;

        // 解析电信云数据
        try {
            jsonObject = parseObject(data);
            coAPServiceDTO = JSONObject.parseObject(jsonObject.getString(KEY_SERVICE), CoAPServiceDTO.class);
            log.info("解析完成数据为：{}", coAPServiceDTO);
        } catch (Exception e) {
            log.error("【解析云数据异常】{}", e);
            e.printStackTrace();
            return;
        }

        iotDeviceDTO = getIotDeviceDTO(jsonObject.getString(KEY_DEVICEID));

        log.info("设备信息为：{}", iotDeviceDTO);
        if (iotDeviceDTO != null) {
//            sendMessageToCloud(coAPServiceDTO.getData().getDeviceReport(), iotDeviceDTO);
            sendMessageToUCloud(coAPServiceDTO.getData().getDeviceReport(), iotDeviceDTO);
        }

    }

    @Override
    public void stateChangeHandler(String data) {
        JSONObject jsonObject = null;
        CoAPDeviceInfoDTO coAPDeviceInfoDTO = null;


        /**
         * {"notifyType":"deviceInfoChanged","deviceId":"5453629f-8920-4739-8037-9c6c39791407","gatewayId":"5453629f-8920-4739-8037-9c6c39791407","networkStatus":null,"deviceInfo":{"nodeId":null,"name":null,"description":null,"manufacturerId":null,"manufacturerName":null,"mac":null,"location":null,"deviceType":null,"model":null,"swVersion":null,"fwVersion":null,"hwVersion":null,"protocolType":null,"bridgeId":null,"status":"ONLINE","statusDetail":"NONE","mute":null,"supportedSecurity":null,"isSecurity":null,"signalStrength":null,"sigVersion":null,"serialNumber":null,"batteryLevel":null,"isHD":null}}
         */
        try {
            // 解析电信云数据
            jsonObject = parseObject(data);
            coAPDeviceInfoDTO = JSONObject.parseObject(jsonObject.getString(KEY_DEVICEINFO), CoAPDeviceInfoDTO.class);
            log.info("解析电信云设备信息变化数据为：{}", coAPDeviceInfoDTO);
        } catch (Exception e) {
            log.error("【解析设备状态云数据异常】{}", e);
            e.printStackTrace();
            return;
        }


        if (coAPDeviceInfoDTO == null || coAPDeviceInfoDTO.getStatus() == null) {
            log.info("设备信息或者是状态为空，直接返回数据为：{}", data);

            return;
        }


        // 根据电信的设备ID获取出设备的详细信息
        IotDeviceDTO iotDevice = getIotDeviceDTO(jsonObject.getString(KEY_DEVICEID));


        if (iotDevice == null) {
            log.info("获取设备信息为空，电信云的设备ID为：{}", jsonObject.getString(KEY_DEVICEID));
            return;
        }


        // 获取设备信息,并推送到透传云
        try {


            if (iotDevice != null) {

                // 之所以采用另一个redis通用类 是因为两个人的代码风格
                // 在透传在线设备库获取设备是否在线
                boolean ifOnline = RedisHandle.zsetHasKey(ONLINE_DEV, iotDevice.getDevid());

                DeviceToCenterStateMessage offlinStateMessage = null;

                switch (coAPDeviceInfoDTO.getStatus()) {
                    case DEV_ONLINE:
                        if (ifOnline) {
                            return;
                        }
                        // 放入并更细设备的最后操作的时间，这里是定时监测上下线的逻辑，不启用的情况下这个不应设置
//                        stringRedisTemplate.opsForHash().put(lastActionKey, iotDevice.getDevid(), String.valueOf(System.currentTimeMillis()));

                        DeviceToCenterStateMessage onlineStateMessage = getDeviceToCenterStateMessage(iotDevice, DeviceStateEnum.ONLINE);

                        this.rabbitTemplate.convertAndSend(
                                exchangeChinatelcom,
                                online,
                                ProtoStuffSerializerUtil.serialize(onlineStateMessage));
                        log.info("接收设备变化回调 -- 设备上线：{}", onlineStateMessage.getDeviceDTO());
                        break;
                    case DEV_ABNORMAL:
                        if (!ifOnline) {
                            return;
                        }

                        sendDeviceStateToCenter(iotDevice);

                    case DEV_OFFLINE:
                        if (!ifOnline) {
                            return;
                        }

                        sendDeviceStateToCenter(iotDevice);

                        break;
                    default:

                        break;

                }
            }


        } catch (Exception e) {
            log.error("【组织Message 发送到透传云.上下线状态】{}", e);
            e.printStackTrace();
        }
    }

    public void sendDeviceStateToCenter(IotDeviceDTO iotDevice) {

        DeviceToCenterStateMessage offlinStateMessage = getDeviceToCenterStateMessage(iotDevice, DeviceStateEnum.OFFLINE);
        log.info("组装下线通知:{}", offlinStateMessage);

        this.rabbitTemplate.convertAndSend(
                exchangeChinatelcom,
                offline,
                ProtoStuffSerializerUtil.serialize(offlinStateMessage));
        log.info("接收设备变化回调 -- 设备下线状态：{}", offlinStateMessage.getDeviceDTO());
    }


    @Override
    public void deviceResponse(String data) {
        CoAPResponseDTO coAPResponseDTO = null;

        try {
            // 解析电信云数据
            coAPResponseDTO = JSONObject.parseObject(data, CoAPResponseDTO.class);
        } catch (Exception e) {
            log.error("【解析设备状态云数据异常】{}", e);
            e.printStackTrace();
            return;
        }
        try {
            // 获取设备信息 并更新到数据库
            IotTaskQueueDTO iotTaskQueueDO = new IotTaskQueueDTO();

            byte[] bytes = deviceRelationCache.get(coAPResponseDTO.getDeviceId());
            PlatformDeviceRelation platformDeviceRelation;
            if (bytes != null) {
                platformDeviceRelation = ProtoStuffSerializerUtil.deserialize(bytes, PlatformDeviceRelation.class);
            } else {
                platformDeviceRelation = nbModuleService.selectDevRelationByDeviceId(coAPResponseDTO.getDeviceId());
                deviceRelationCache.put(coAPResponseDTO.getDeviceId(), ProtoStuffSerializerUtil.serialize(platformDeviceRelation));
            }

            iotTaskQueueDO.setDid(platformDeviceRelation.getDeviceImei());
            iotTaskQueueDO.setCommandid(coAPResponseDTO.getCommandId());
            switch (coAPResponseDTO.getResult().getResultCode()) {
                case STATE_SENT:
                    iotTaskQueueDO.setState(2);
                    break;
                case STATE_DELIVERED:
                    iotTaskQueueDO.setState(3);
                    break;
                case STATE_TIMEOUT:
                    iotTaskQueueDO.setState(4);
                    break;
                default:
                    iotTaskQueueDO.setState(4);
                    break;
            }
            log.info("【更新设备响应状态到透传云数据库】：{}", iotTaskQueueDO);

            taskService.updateTaskState(iotTaskQueueDO.getDid(), iotTaskQueueDO.getCommandid(), iotTaskQueueDO.getState());
        } catch (Exception e) {
            log.error("【更新设备响应状态到透传云数据库异常】{}", e);
            e.printStackTrace();
        }
    }


    public void sendDeviceOnlineStateToCloud(IotDeviceDTO iotDeviceDTO) {

        if (iotDeviceDTO == null) {
            return;
        }

        DeviceToCenterStateMessage deviceToCenterStateMessage = getDeviceToCenterStateMessage(iotDeviceDTO, DeviceStateEnum.ONLINE);

        this.rabbitTemplate.convertAndSend(
                exchangeChinatelcom,
                online,
                ProtoStuffSerializerUtil.serialize(deviceToCenterStateMessage));
    }

    /**
     * 发送数据到有人云
     *
     * @param data
     * @param iotDeviceDTO
     */
    private void sendMessageToUCloud(String data, IotDeviceDTO iotDeviceDTO) {
        // 组织Message 发送到透传云
        try {
            if (!StringUtils.isEmpty(data)) {
                byte[] payload = byteMerger(head, data.getBytes());

                try {
                    log.debug("[收到设备发送上来的数据，发送到MQTT]，设备的ID为:{},数据长度为：{};", iotDeviceDTO.getDevid(), payload.length);
                    // 发布设备上行的数据到MQTT服务
                    usrCloudMqttClient.PublishDataForDevId(iotDeviceDTO.getDevid(), payload);
                } catch (Exception e) {
                    log.error("[将设备的数据发送到上层应用异常捕获，设备ID为：{},异常信息为：{}", iotDeviceDTO.getDevid(), e);
                }
            }
        } catch (Exception e) {
            log.error("[RabbitMq],发送数据到透传云发送异常：设备信息为：{},异常为{}", iotDeviceDTO, e);
            e.printStackTrace();
        }
    }

    /**
     * 合并byte[]数组
     *
     * @param byte_1
     * @param byte_2
     * @return
     */
    private byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }


    /**
     * 发送数据到透传云
     *
     * @param data
     * @param iotDeviceDTO
     */
    private void sendMessageToCloud(String data, IotDeviceDTO iotDeviceDTO) {
        // 组织Message 发送到透传云
        try {
            if (!StringUtils.isEmpty(data)) {

                DeviceToCenterDataMessage dataMessage = getDeviceToCenterDataMessage(data, iotDeviceDTO);

                // 在透传在线设备库获取设备是否在线
                boolean ifOnline = RedisHandle.zsetHasKey(ONLINE_DEV, iotDeviceDTO.getDevid());

                log.info("在透传在线设备库获取设备是否在线:{}", ifOnline);
                if (!ifOnline) {
                    sendDeviceOnlineStateToCloud(iotDeviceDTO);
                }

                // 放入并更新设备的最后操作的时间
                stringRedisTemplate.opsForHash().put(lastActionKey, iotDeviceDTO.getDevid(), String.valueOf(System.currentTimeMillis()));

                log.info("[RabbitMQ]发送设备的数据到透传云：{},数据区为：{}", dataMessage.getDeviceDTO(), data);
                this.rabbitTemplate.convertAndSend(
                        exchangeChinatelcom,
                        routingkeyToCenterData,
                        ProtoStuffSerializerUtil.serialize(dataMessage));
            }
        } catch (Exception e) {
            log.error("[RabbitMq],发送数据到透传云发送异常：设备信息为：{},异常为{}", iotDeviceDTO, e);
            e.printStackTrace();
        }
    }

    /**
     * 组织发送设备到Center的数据
     *
     * @param data
     * @param iotDeviceDTO
     * @return
     */
    private DeviceToCenterDataMessage getDeviceToCenterDataMessage(String data, IotDeviceDTO iotDeviceDTO) {
        DeviceDTO deviceDTO = new DeviceDTO(iotDeviceDTO.getDevid(), iotDeviceDTO.getName(), iotDeviceDTO.getUid(), iotDeviceDTO.getProtocol(), iotDeviceDTO.getType());
        UserDTO userDTO;
        if (iotDeviceDTO.getPuid() == null) {
            userDTO = new UserDTO(iotDeviceDTO.getAccount(), iotDeviceDTO.getUid(), iotDeviceDTO.getPAccount(), -1);
        } else {
            userDTO = new UserDTO(iotDeviceDTO.getAccount(), iotDeviceDTO.getUid(), iotDeviceDTO.getPAccount(), iotDeviceDTO.getPuid());
        }

        return new DeviceToCenterDataMessage(deviceDTO, userDTO, hexStringToBytes(data));
    }


    /**
     * 构造设备上下线发送数据
     *
     * @param iotDevice
     * @param state
     * @return
     */
    public DeviceToCenterStateMessage getDeviceToCenterStateMessage(IotDeviceDTO iotDevice, DeviceStateEnum state) {
        DeviceDTO deviceDTO = new DeviceDTO(
                iotDevice.getDevid(),
                iotDevice.getName(),
                iotDevice.getUid(),
                iotDevice.getProtocol(),
                iotDevice.getType());

        UserDTO userDTO = null;
        if (iotDevice.getPuid() == null) {
            userDTO = new UserDTO(iotDevice.getAccount(), iotDevice.getUid(), iotDevice.getPAccount(), -1);
        } else {
            userDTO = new UserDTO(iotDevice.getAccount(), iotDevice.getUid(), iotDevice.getPAccount(), iotDevice.getPuid());
        }

        return new DeviceToCenterStateMessage(deviceDTO, userDTO, state);
    }

    /**
     * 获取设备信息
     *
     * @param devId
     * @return
     */
    public IotDeviceDTO getIotDeviceDTO(String devId) {
        try {
            byte[] bytes1 = deviceRelationCache.get(devId);
            PlatformDeviceRelation relation;
            if (bytes1 != null) {
                relation = ProtoStuffSerializerUtil.deserialize(bytes1, PlatformDeviceRelation.class);
            } else {
                relation = nbModuleService.selectDevRelationByDeviceId(devId);
                if (relation != null) {
                    deviceRelationCache.put(devId, ProtoStuffSerializerUtil.serialize(relation));
                    deviceRelationCache.put(relation.getDeviceImei(), ProtoStuffSerializerUtil.serialize(relation));
                }
            }
            log.info("nb关联信息为：{}", relation);
            if (relation == null) {
                return null;
            }
            IotDeviceDTO iotDeviceDTO = nbModuleService.selectIotDeviceDtoByDeviceId(relation.getDeviceImei());
            if (iotDeviceDTO == null) {
                return null;
            }

            return iotDeviceDTO;
        } catch (Exception e) {
            log.error("【查询设备信息异常】{}", e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deviceInfoChanged(String data) {

    }
}
