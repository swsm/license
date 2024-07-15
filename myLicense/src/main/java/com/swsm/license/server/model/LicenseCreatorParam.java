package com.swsm.license.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swsm.license.server.config.LicenseConfig;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * License生成类需要的参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseCreatorParam implements Serializable {

    private static final long serialVersionUID = -7793154252684580872L;

    /**
     * 证书subject
     */
    private String subject;

    /**
     * 密钥别称
     */
    @JsonIgnore
    private String privateAlias = LicenseConfig.privateAlias;

    /**
     * 密钥密码(需要妥善保管，不能让使用者知道)
     */
    private String keyPass = LicenseConfig.privateStorePass;

    /**
     * 访问秘钥库的密码
     */
    private String publicStorePass = LicenseConfig.publicStorePass;

    /**
     * 证书生成路径
     */
    private String licensePath;

    /**
     * 密钥库存储路径
     */
    private String privateKeysStorePath;

    /**
     * 证书生效时间
     */
    private Date issuedTime = new Date();

    /**
     * 证书失效时间
     */
    private Date expiryTime;

    /**
     * 用户类型
     */
    private String customerType = "user";

    /**
     * 用户数量
     */
    private Integer consumerAmount = 1;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 额外的服务器硬件校验信息
     */
    @ApiModelProperty(value = "服务器硬件信息", required = true)
    private LicenseCheckModel licenseCheckModel;

    /**
     * 自定义需要校验的License参数
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LicenseCheckModel implements Serializable{
    
        private static final long serialVersionUID = 8600137500316662317L;
        /**
         * 可被允许的IP地址
         */
        private List<String> ipAddress;
    
        /**
         * 可被允许的MAC地址
         */
        private List<String> macAddress;
    
        /**
         * 可被允许的CPU序列号
         */
        private String cpuSerial;
    
        /**
         * 可被允许的主板序列号
         */
        private String mainBoardSerial;

        /**
         * 可被允许创建的设备数量
         */
        private Integer equipmentCount = 10;
    }
}
