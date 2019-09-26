package cn.usr.middleware.pojo;

import lombok.Data;

@Data
public class CoAPServiceDTO {

    /**
     * 服务ID
     */
    private String serviceId;
    /**
     * 服务类型
     */
    private String serviceType;
    /**
     * 透传数据
     */
    private CoAPDataDTO data;
    /**
     * 时间
     */
    private String eventTime;
}
