package cn.usr.middleware.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package: cn.usr.middleware.pojo
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-04-23 09:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsrCommandResult {

    /**
     * 指令发送信息
     */
    private String msg;

    /**
     * 设备Id
     */
    private String deviceId;

    /**
     * 指令ID
     */
    private String commandId;

    /**
     * 指令状态
     */
    private String status;

    /**
     * 下发命令有效的超期时间（秒）
     */
    private Integer expireTime;

    /**
     * 指令创建时间:20180423T021614Z
     */
    private String creationTime;

    public UsrCommandResult(String msg) {
        this.msg = msg;
    }
}
