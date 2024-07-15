package com.swsm.license.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 建类说明：许可证配置
 */
@Component
@Configuration
@ConfigurationProperties(prefix = "license")
public class LicenseConfig {

    public static final String LICENSE_NAME = "license.lic";
    public static final String PUB_KEY_STORE_NAME = "publicCerts.keystore";

    public static String licensePath;

    @Value("${license.licensePath}")
    public void setLicensePath(String licensePath) {
        LicenseConfig.licensePath = licensePath;
    }
}
