package com.swsm.license.server.controller.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * @author swsm
 * @date 2024-07-09
 */
@Data
@ApiModel("创建license请求")
public class CreateLicenseReq {
    
    private String subject;

    @ApiModelProperty(value = "证书发行时间，默认不传则为当前时间", example = "2022-04-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime = new Date();

    @ApiModelProperty(value = "证书失效时间", example = "2023-04-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date expiryTime;
    
    @ApiModelProperty(value = "用户类型")
    private String customerType;

    @ApiModelProperty(value = "证书其余验证信息")
    private LicenseCheckInfo licenseCheckInfo;

    @ApiModelProperty(value = "描述信息", example = "系统证书")
    private String description;

    
    @Data
    public class LicenseCheckInfo {
        
        @ApiModelProperty("可被允许的IP地址")
        private List<String> ipAddress;

        @ApiModelProperty(value = "可被允许的MAC地址")
        private List<String> macAddress;

        @ApiModelProperty("可被允许的CPU序列号")
        private String cpuSerial;

        @ApiModelProperty("可被允许的主板序列号")
        private String mainBoardSerial;

        @ApiModelProperty("可被允许创建的设备数量")
        private Integer equipmentCount = 10;
    }
    
}
