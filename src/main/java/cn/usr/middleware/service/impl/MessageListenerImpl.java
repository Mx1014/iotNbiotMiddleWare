package cn.usr.middleware.service.impl;

import cn.usr.cloud.common.enumu.DeviceStateEnum;
import cn.usr.cloud.common.mqmessage.centertodevice.CenterToDeviceDataMessage;
import cn.usr.cloud.common.mqmessage.devicetocenter.DeviceToCenterStateMessage;
import cn.usr.middleware.chinatelecom.ChinaTelecomApiSdkManager;
import cn.usr.middleware.chinatelecom.SdkManager;
import cn.usr.middleware.pojo.IotDeviceDTO;
import cn.usr.middleware.pojo.PlatformDeviceRelation;
import cn.usr.middleware.service.MessageListener;
import cn.usr.middleware.service.NBModuleService;
import cn.usr.middleware.util.ProtoStuffSerializerUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


import java.util.List;

import static cn.usr.middleware.entity.SpPortalConstant.ONLINE_DEV;
import static cn.usr.middleware.util.BaseUtils.bytesToHexString;

import static cn.usr.middleware.util.EhcacheUtil.deviceRelationCache;

/**
 * @Package: cn.usr.middleware.service
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-05-10 15:27
 */
@Component
@Slf4j
public class MessageListenerImpl implements MessageListener {

    @Value("${mq.routingkey.to_device_data}")
    private String routingkeyToDeviceData;

    @Value("${mq.routingkey.cache_device}")
    private String cacheDevice;

    @Value("${mq.exchange.chinatelcom}")
    private String exchangeChinatelcom;

    @Value("${mq.routingkey.online}")
    private String online;

    @Value("${mq.routingkey.offline}")
    private String offline;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    NBModuleService nbModuleService;

    @Autowired
    ChinaTelecomApiSdkManager chinaTelecomApiSdkManager;

    @Autowired
    private DataHandlerImpl dataHandler;


    @Override
    public void onMessageReceive(final Message message) {

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("[RabbitMQ]收到来自透传云的原始数据流routingKey为::{}", routingKey);
        if (routingKey.equals(routingkeyToDeviceData)) {
            handleRabbitMQData(message.getBody());
        } else if (routingKey.equals(cacheDevice)) {
            handleRabbitMQCacheUpdate(message);
        }

    }

    @Override
    public void onMessageReceiveOfUCloud(String deviceId, byte[] data) {

        byte[] payload = new byte[data.length - 2];
        // 截取前两位字节，因为新的协议里有，ver 和 msgId 两个字节的数据，透传完全不需要
        System.arraycopy(data, 2, payload, 0, payload.length);

        try {
            PlatformDeviceRelation platformDeviceRelation = nbModuleService.selectDevRelationByIMEI(deviceId);

            if (platformDeviceRelation == null || platformDeviceRelation.getPlatformDeviceId() == null) {
                log.info("设备对应的电信云平台的信息为空 ，{}", deviceId);
                return;
            }
            chinaTelecomApiSdkManager.postDeviceCommand2(
                    platformDeviceRelation.getPlatformDeviceId(),
                    bytesToHexString(payload));
        } catch (Exception e) {
            log.error("向设备发送命令异常，:{}", e.getMessage());
        }

    }


    /**
     * 处理来自API的缓存更新通知
     *
     * @param message
     */
    private void handleRabbitMQCacheUpdate(Message message) {
        // 反序列化数据
        String deserialize = ProtoStuffSerializerUtil.deserialize(message.getBody(), String.class);

        if (deserialize == null) {
            return;
        }

        // 获取设备ID集合
        List<String> deviceList = JSONObject.parseArray(deserialize, String.class);

        log.info("更新缓存设备ID集合：{}", deviceList);

        for (String deviceId : deviceList) {

            // 先判断设备是否在线,如果不在线说明是新加的设备还没有进行通讯，不用上下线操作
            boolean ifOnline = RedisHandle.zsetHasKey(ONLINE_DEV, deviceId);
            if (!ifOnline) {
                return;
            }
            IotDeviceDTO iotDeviceDTO = nbModuleService.selectIotDeviceDtoByDeviceId(deviceId);


            if (iotDeviceDTO == null) {
                log.info("需要缓存更新的设备信息为null 设备ID为：{}", deviceId);
                return;
            }

            log.info("需要缓存更新的设备信息为：{}", iotDeviceDTO);

            dataHandler.sendDeviceStateToCenter(iotDeviceDTO);

            // 清理内部缓存
            try {
                // 清楚redis 设备信息
                nbModuleService.removeIotDeviceDtoForRedis(deviceId);
                byte[] bytes2 = deviceRelationCache.get(iotDeviceDTO.getDevid());

                if (bytes2 == null) {
                    return;
                }
                PlatformDeviceRelation platformDeviceRelation = ProtoStuffSerializerUtil.deserialize(bytes2, PlatformDeviceRelation.class);
                deviceRelationCache.remove(deviceId);
                deviceRelationCache.remove(platformDeviceRelation.getPlatformDeviceId());
            } catch (Exception e) {
                log.error("清理内部缓存出现异常，异常详情为：{}", e);
            }

        }
    }


    /**
     * 处理RabbitMQ收到的数据
     * <p>
     * 实现机制:
     * 1.先序列化数据
     * 2.调用电信API 下发数据
     *
     * @param body
     */
    private void handleRabbitMQData(byte[] body) {
        CenterToDeviceDataMessage receiveData = ProtoStuffSerializerUtil.deserialize(body, CenterToDeviceDataMessage.class);
        log.debug("收到Rabbit的的 数据为:{}", receiveData);

        try {

            byte[] bytes = deviceRelationCache.get(receiveData.getDeviceId());
            PlatformDeviceRelation platformDeviceRelation;
            if (bytes != null) {
                platformDeviceRelation = ProtoStuffSerializerUtil.deserialize(bytes, PlatformDeviceRelation.class);
            } else {
                platformDeviceRelation = nbModuleService.selectDevRelationByIMEI(receiveData.getDeviceId());
                deviceRelationCache.put(receiveData.getDeviceId(), ProtoStuffSerializerUtil.serialize(platformDeviceRelation));
            }

            log.info("【根据IMEI获取设备信息为】：{}", platformDeviceRelation);
            if (platformDeviceRelation != null && platformDeviceRelation.getDeviceImei() != null) {
                switch (platformDeviceRelation.getPlatformType()) {
                    case 0:
                        if (platformDeviceRelation.getPlatformDeviceId() != null) {
                            chinaTelecomApiSdkManager.postDeviceCommand2(
                                    platformDeviceRelation.getPlatformDeviceId(),
                                    bytesToHexString(receiveData.getData()));
                        }
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("【根据IMEI获取设备ID异常】", e);
        }

    }
}
