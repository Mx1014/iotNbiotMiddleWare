package cn.usr.middleware.controller;


import cn.usr.middleware.service.DataHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static cn.usr.middleware.util.BaseUtils.getRequestParams;

/**
 * @author zhiyuan
 * SpPortal回调接口
 * //TODO
 * 1.解析数据
 * 2.根据设备的DevId 查询IMEI
 * 3.将数据信息组装成Message发送到RabbitMq
 */
@RestController
@Slf4j
@RequestMapping("/subscribe")
public class SpPortalCallbackController {

    @Autowired
    private DataHandler dataHandler;

    @RequestMapping("/CallbackDataChange")
    public void CallbackDataChange(HttpServletRequest request) {
        String requestParams = getRequestParams(request);
        log.info("数据变化回调 Data={}",requestParams);
        if (StringUtils.isEmpty(requestParams)) {
            return;
        }
        log.info("设备数据上传--收到的数据为：{}", requestParams);
        dataHandler.dataChangeHandler(requestParams);
    }

    /**
     * 开发CallbackDataChange1 的原因是电信云出现过单方面的接口调用失效，必须换用新的接口才能重新订阅成功
     *
     * @param request
     */
    @RequestMapping("/CallbackDataChangeUpdate1")
    public void CallbackDataChange1(HttpServletRequest request) {
        String requestParams = getRequestParams(request);
        log.debug("CallbackDataChangeUpdate1 空访问");
        if (StringUtils.isEmpty(requestParams)) {
            return;
        }
        log.info("设备数据上传--收到的数据为：{}", requestParams);
        dataHandler.dataChangeHandler(requestParams);
    }


    @RequestMapping("/CallbackDeviceResponse")
    public void CallbackDeviceResponse(HttpServletRequest request) {
        String requestParams = getRequestParams(request);
        log.info("命令下发回调，Data={}", requestParams);
        if (StringUtils.isEmpty(requestParams)) {
            return;
        }
        // 页面上已经取消这个功能了，所以说此处也不加了
//        dataHandler.deviceResponse(requestParams);
    }

    @RequestMapping("/CallbackDeviceInfoChanged")
    public void CallbackDeviceInfoChanged(HttpServletRequest request) {
        String requestParams = getRequestParams(request);
        log.info("设备信息变化回调，Data={}", requestParams);
        if (StringUtils.isEmpty(requestParams)) {
            return;
        }
        dataHandler.stateChangeHandler(requestParams);
    }

    @Deprecated
    @RequestMapping("/CallbackStateChange")
    public void CallbackStateChange(HttpServletRequest request) {
        String requestParams = getRequestParams(request);
        log.info("bindDevice回调，Data={}", requestParams);
        if (StringUtils.isEmpty(requestParams)) {
            return;
        }
        dataHandler.stateChangeHandler(requestParams);
    }
}
