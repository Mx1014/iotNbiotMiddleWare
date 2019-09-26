package cn.usr.middleware.service;


/**
 * @author Administrator
 */
public interface DataHandler {

    /**
     * 设备数值变化处理
     *
     * @param data
     */
    void dataChangeHandler(String data);

    /**
     * 设备状态变化
     *
     * @param data
     */
    void stateChangeHandler(String data);

    /**
     * 设备响应命令
     *
     * @param data
     */
    void deviceResponse(String data);

    /**
     * 设备信息变化
     *
     * @param data
     */
    void deviceInfoChanged(String data);
}
