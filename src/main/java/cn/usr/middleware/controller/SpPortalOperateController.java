package cn.usr.middleware.controller;

import cn.usr.middleware.pojo.*;
import cn.usr.middleware.service.PortalOperateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static cn.usr.middleware.entity.SpPortalConstant.*;
import static cn.usr.middleware.util.BaseUtils.c;

/**
 * @Package: cn.usr.middleware.controller
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-04-17 15:01
 */
@RestController
@Slf4j
@EnableSwagger2
@Api(tags = "SpPortalOperateController", description = "平台操作相关")
@RequestMapping("operates")
public class SpPortalOperateController {


    @Autowired
    private PortalOperateService portalOperateService;

    /**
     * 添加新设备
     *
     * @param
     */
    @PostMapping("/addDevice")
    @ApiOperation(value = "添加一个设备到云平台", produces = "application/json")
    public ResponseResult addDevice(@RequestBody AddDeviceParameterDTO deviceDTO) {

        log.debug("【要添加设备的IMEI为】：{}", deviceDTO.getImei());

        if (deviceDTO.getImei() == null) {
            return new ResponseResult("505", RESULT_NOPARM, "");
        }
        // IMEI校验
        if (!c(deviceDTO.getImei())) {
            return new ResponseResult("500", RESULT_NOIMEI, "");
        }
        return portalOperateService.addDevice(deviceDTO.getImei(), deviceDTO.getDevName());
    }
    /**
     * 删除设备
     *
     * @param dto
     * @return
     */
    @PostMapping("/deleteDevice")
    @ApiOperation(value = "删除一个设备", produces = "application/json")
    public ResponseResult deleteDevice(@RequestBody DeviceParameterDTO dto) {
        log.debug("【要删除的设备IMEI为】：{}", dto.getDeviceId());
        if (dto.getDeviceId() == null) {
            return new ResponseResult("505", RESULT_NOPARM, "");
        }
        return portalOperateService.deleteDevice(dto.getDeviceId());
    }

    /**
     * 下发指令
     * @param usrDeviceData
     * @return
     */
    @PostMapping("/postDeviceCommand")
    @ApiOperation(value = "下发指令", produces = "application/json")
    public ResponseResult postDeviceCommand(@RequestBody UsrDeviceData usrDeviceData) {

        log.debug("【要下发指令的的设备IMEI为】：{}", usrDeviceData.getDeviceId());
        if (usrDeviceData.getDeviceId() == null && usrDeviceData.getData() == null) {
            return new ResponseResult("505", RESULT_NOPARM, "");
        }
        return new ResponseResult("0", RESULT_SUCCESS, portalOperateService.postDeviceCommand(usrDeviceData.getDeviceId(), usrDeviceData.getData()));
    }

    /**
     * 订阅平台消息之设备数据变化
     * @param callbackUrlDTO
     * @return
     */
    @PostMapping("/subscribeNotifyDeviceDataChanged")
    @ApiOperation(value = "订阅平台消息之设备数据变化", produces = "application/json")
    public ResponseResult subscribeNotifyDeviceDataChanged(@RequestBody CallbackUrlDTO callbackUrlDTO) {
        if (callbackUrlDTO == null || callbackUrlDTO.getUrl() == null) {
            return new ResponseResult("505", RESULT_NOPARM, "");
        }
        return portalOperateService.subscribeNotifyDeviceDataChanged(callbackUrlDTO.getUrl());
    }

    /**
     * 订阅平台消息之设备变化
     *
     * @param callbackUrlDTO
     * @return
     */
    @PostMapping("/subscribeNotifyDeviceInfoChanged")
    @ApiOperation(value = "订阅平台消息之设备信息变化", produces = "application/json")
    public ResponseResult subscribeNotifyDeviceInfoChanged(@RequestBody CallbackUrlDTO callbackUrlDTO) {
        if (callbackUrlDTO == null || callbackUrlDTO.getUrl() == null) {
            return new ResponseResult("505", RESULT_NOPARM, "");
        }
        return portalOperateService.subscribeNotifyDeviceInfoChanged(callbackUrlDTO.getUrl());
    }


    /**
     * 订阅平台消息之设备绑定激活
     *
     * @param callbackUrlDTO
     * @return
     */
    @PostMapping("/subscribeNotifyBindDevice")
    @Deprecated
    @ApiOperation(value = "订阅平台消息之设备绑定激活", produces = "application/json")
    public ResponseResult subscribeNotifyBindDevice(@RequestBody CallbackUrlDTO callbackUrlDTO) {
        if (callbackUrlDTO == null || callbackUrlDTO.getUrl() == null) {
            return new ResponseResult("505", RESULT_NOPARM, "");
        }
        return portalOperateService.subscribeNotifyBindDevice(callbackUrlDTO.getUrl());
    }

    /**
     * 订阅平台消息之添加新设备
     *
     * @param callbackUrlDTO
     * @return
     */
    @PostMapping("/subscribeDeviceAdded")
    @Deprecated
    @ApiOperation(value = "订阅平台消息之添加新设备", produces = "application/json")
    public ResponseResult subscribeDeviceAdded(@RequestBody CallbackUrlDTO callbackUrlDTO) {
        if (callbackUrlDTO == null || callbackUrlDTO.getUrl() == null) {
            return new ResponseResult("505", RESULT_NOPARM, "");
        }
        return portalOperateService.subscribeNotifyDeviceDataChanged(callbackUrlDTO.getUrl());
    }

    /**
     * 订阅平台消息之删除设备
     *
     * @param callbackUrlDTO
     * @return
     */
    @PostMapping("/subscribeDeviceDeleted")
    @Deprecated
    @ApiOperation(value = "订阅平台消息之删除设备", produces = "application/json")
    public ResponseResult subscribeDeviceDeleted(@RequestBody CallbackUrlDTO callbackUrlDTO) {
        if (callbackUrlDTO == null || callbackUrlDTO.getUrl() == null) {
            return new ResponseResult("505", RESULT_NOPARM, "");
        }
        return portalOperateService.subscribeNotifyDeviceDataChanged(callbackUrlDTO.getUrl());
    }


    /**
     * 订阅平台消息之下发指令响应变化
     *
     * @param callbackUrlDTO
     * @return
     */
    @PostMapping("/subscribeCommandRsp")
    @Deprecated
    @ApiOperation(value = "订阅平台消息之响应命令", produces = "application/json")
    public ResponseResult subscribeCommandRsp(@RequestBody CallbackUrlDTO callbackUrlDTO) {
        if (callbackUrlDTO == null || callbackUrlDTO.getUrl() == null) {
            return new ResponseResult("505", RESULT_NOPARM, "");
        }
        return portalOperateService.subscribeCommandRsp(callbackUrlDTO.getUrl());
    }


}
