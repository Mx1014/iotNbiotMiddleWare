package cn.usr.middleware.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Package: cn.usr.middleware.pojo
 * @Description: TODO 返回码可以用枚举进行优化
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-04-23 11:45
 */
@Data
@AllArgsConstructor
public class ResponseResult {

    /**
     * 状态码
     */
    private String state;

    /**
     * 描述
     */
    private String desc;

    /**
     * 数据
     */
    private Object data;
}
