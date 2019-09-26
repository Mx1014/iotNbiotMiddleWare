package cn.usr.middleware.service.impl;


import cn.usr.middleware.dao.DeviceDao;
import cn.usr.middleware.dao.DeviceRelationDao;
import cn.usr.middleware.pojo.IotDeviceDTO;
import cn.usr.middleware.pojo.PlatformDeviceRelation;
import cn.usr.middleware.service.NBModuleService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Package: cn.usr.cloud.db.service.impl
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-05-10 11:55
 */
@Service
@Slf4j
public class NBModuleServiceImpl implements NBModuleService {


    public static final String REDIS_KEY_NB_DEVICE_CACHE = "NBDeviceCache";

    @Autowired
    private DeviceRelationDao deviceRelationDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PlatformDeviceRelation selectDevRelationByIMEI(String imei) {
        return deviceRelationDao.selectDevRelationByIMEI(imei);
    }

    @Override
    public PlatformDeviceRelation selectDevRelationByDeviceId(String deviceId) {
        return deviceRelationDao.selectDevRelationByDeviceId(deviceId);
    }

    @Override
    public IotDeviceDTO selectIotDeviceDtoByDeviceId(String devId) {
        String device = (String) stringRedisTemplate.opsForHash().get(REDIS_KEY_NB_DEVICE_CACHE, devId);

        log.info("缓存中的设备信息数据为：{}", device);
        if (StringUtils.isEmpty(device)) {
            IotDeviceDTO iotDeviceDTO1 = deviceDao.selectIotDeviceDtoByDeviceId(devId);
            log.info("数据库查询设备信息为：{}", iotDeviceDTO1);
            if (iotDeviceDTO1 != null) {
                stringRedisTemplate.opsForHash().put(REDIS_KEY_NB_DEVICE_CACHE, devId, JSONObject.toJSONString(iotDeviceDTO1));
            }
            return iotDeviceDTO1;
        }
        return JSONObject.parseObject(device, IotDeviceDTO.class);
    }

    @Override
    public void removeIotDeviceDtoForRedis(String devId) {
        stringRedisTemplate.opsForHash().delete(REDIS_KEY_NB_DEVICE_CACHE, devId);
    }
}
