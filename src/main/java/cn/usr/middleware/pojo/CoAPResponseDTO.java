package cn.usr.middleware.pojo;

import lombok.Data;

@Data
public class CoAPResponseDTO {
    private String deviceId;
    private String commandId;
    private CoAPResponseResultDTO result;
}
