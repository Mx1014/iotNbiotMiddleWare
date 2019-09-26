package cn.usr.middleware.entity;

import org.springframework.stereotype.Component;

/**
 * @Package: cn.usr.middleware.constant
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-04-20 11:54
 */
@Component
public final class SpPortalConstant {


    /**
     * youren_01电信属性
     */
//    public static final String APP_ID = "mC1Bfjytns_KRbfPualPfSXZtu8a";
//    public static final String SECRET = "1dndq6Vf5mjzmKuGzONkGH8jD6ca";
//    public static final String IP = "device.api.ct10649.com";
//    public static final String PORT = "8743";


    /**
     * 电信属性
     */
//    public static final String APP_ID = "J8nPLOrGb_EmEEV0q9ztNvwhXfIa";
//    public static final String SECRET = "jkGqmGMWufZfGic9fuprMXNh7P8a";
//    public static final String IP = "device.api.ct10649.com";
//    public static final String PORT = "8743";


    public static final String KEY_TOKEN = "china_telecom_token";


    /**
     * 厂商ID
     */
    public static final String MANUFACTURER_ID = "SHWH";
    /**
     * 厂商名称
     */
    public static final String MANUFACTURER_NAME = "SHWH";
    /**
     * 设备类型
     */
    public static final String DEVICE_TYPE = "WHNB-L";

    /**
     * 设备模式
     */
    public static final String DEVICE_MODEL = "NBIoTDevice";

    /**
     * 通讯协议
     */
    public static final String PROTOCOL_TYPE = "CoAP";


    /**
     * 下发指令key
     */
    public static final String DEVICEADDED = "deviceAdded";
    public static final String DEVICEDATACHANGED = "deviceDataChanged";
    public static final String DEVICEDELETED = "deviceDeleted";
    public static final String COMMANDRSP = "commandRsp";
    public static final String BINDDEVICE = "bindDevice";
    public static final String DEVICEINFOCHANGED = "deviceInfoChanged";


    public static final String STATE_PENDING = "PENDING";
    public static final String STATE_DEFAULT = "DEFAULT";
    public static final String STATE_EXPIRED = "EXPIRED";
    public static final String STATE_FAILED = "FAILED";
    public static final String STATE_TIMEOUT = "TIMEOUT";
    public static final String STATE_SUCCESSFUL = "SUCCESSFUL";
    public static final String STATE_SENT = "SENT";
    public static final String STATE_DELIVERED = "DELIVERED";


    public static final String SERVICEID = "TransParent";
    public static final String COMMAND_NAME = "DEVICE_RECEIVE";
    public static final String GENERAL_VALUE = "%VALUE%";

    /**
     * redis token key
     */
    public final static String ONLINE_DEV = "online_dev";


    /**
     * 解析数据KEY
     */
    public static final String KEY_DEVICEID = "deviceId";
    public static final String KEY_SERVICE = "service";
    public static final String KEY_NAME = "name";
    public static final String KEY_NODEID = "nodeId";
    public static final String KEY_STATUS = "status";
    public static final String KEY_RESULTCODE = "resultCode";
    public static final String KEY_DEVICEINFO = "deviceInfo";
    public static final String KEY_RESULT = "result";
    public static final String DEV_OFFLINE = "OFFLINE";
    public static final String DEV_ONLINE = "ONLINE";
    public static final String DEV_ABNORMAL= "ABNORMAL";    //电信云状态-设备异常


    /**
     * Result
     */
    public static final String RESULT_NOIMEI = "不支持的IMEI";
    public static final String RESULT_NOPARM = "参数不全";
    public static final String RESULT_SUCCESS = "成功";


    private SpPortalConstant() {
    }
}
