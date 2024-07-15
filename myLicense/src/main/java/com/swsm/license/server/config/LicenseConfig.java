package com.swsm.license.server.config;

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
    public static final String PRI_KEY_STORE_NAME = "privateKeys.keystore";
    
    public static String subject;

    public static String customerType;

    public static String publicAlias;

    public static String privateAlias;

    public static String privateStorePass;

    public static String publicStorePass;

    public static String licensePath;

    @Value("${license.subject:zy-customer}")
    public void setSubject(String subject) {
        LicenseConfig.subject = subject;
    }

    @Value("${license.customerType}")
    public void setCustomerType(String customerType) {
        LicenseConfig.customerType = customerType;
    }

    @Value("${license.publicAlias:publicCert}")
    public void setPublicAlias(String publicAlias) {
        LicenseConfig.publicAlias = publicAlias;
    }

    @Value("${license.privateAlias}")
    public void setPrivateAlias(String privateAlias) {
        LicenseConfig.privateAlias = privateAlias;
    }

    @Value("${license.privateStorePass}")
    public void setPrivateStorePass(String privateStorePass) {
        LicenseConfig.privateStorePass = privateStorePass;
    }

    @Value("${license.publicStorePass}")
    public void setPublicStorePass(String publicStorePass) {
        LicenseConfig.publicStorePass = publicStorePass;
    }

    @Value("${license.licensePath}")
    public void setLicensePath(String licensePath) {
        LicenseConfig.licensePath = licensePath;
    }
}
