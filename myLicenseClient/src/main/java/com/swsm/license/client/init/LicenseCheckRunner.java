package com.swsm.license.client.init;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.swsm.license.client.config.LicenseConfig;
import com.swsm.license.client.constant.ProjectConstant;
import com.swsm.license.sdk.model.AbstractServerInfos;
import com.swsm.license.sdk.model.HardwareInfo;
import com.swsm.license.sdk.model.LinuxServerInfos;
import com.swsm.license.sdk.model.WindowsServerInfos;
import com.swsm.license.client.model.LicenseVerify;
import com.swsm.license.client.model.LicenseVerifyParam;
import de.schlichtherle.license.LicenseContent;
import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 在项目启动时安装证书
 */
@Slf4j
@Component
public class LicenseCheckRunner implements ApplicationRunner {

    /**
     * 证书subject
     */
    @Value("${license.subject:zy-customer}")
    private String subject;

    /**
     * 公钥别称
     */
    @Value("${license.publicAlias:publicCert}")
    private String publicAlias;

    /**
     * 访问公钥库的密码
     */
    @Value("${license.publicStorePass:public_password1234}")
    private String storePass;

    /**
     * 证书生成路径
     */
    @Value("${license.licensePath:}")
    private String licensePath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        checkLicense();
    }

    public void checkLicense() {
        // 打印服务器信息
        HardwareInfo serverInfos = getServerInfos();
        if (serverInfos != null) {
            log.info("服务器信息：" + JSON.toJSONString(serverInfos));
        } else {
            log.error("未获取到服务器信息");
        }
        if (StrUtil.isBlank(licensePath)) {
            log.error("未配置有效证书路径，程序将退出启动！");
            System.exit(0);
        }
        File file = new File(licensePath);
        if (!file.isDirectory()) {
            log.error("配置的证书路径不是目录，程序将退出启动");
            System.exit(0);
        }
        File[] files = file.listFiles();
        // 该路径下必须存在 license.lic 和 publicCerts.keystore 这2个文件
        int exactCount = 2;
        if (files == null || files.length < exactCount) {
            log.error("配置的证书路径下无必须的 license.lic 和 publicCerts.keystore 文件，程序将退出启动");
            System.exit(0);
        } else {
            List<String> fileNames = Lists.newArrayList();
            for (File f : files) {
                fileNames.add(f.getName());
            }
            if (!fileNames.contains(LicenseConfig.LICENSE_NAME) || !fileNames.contains(LicenseConfig.PUB_KEY_STORE_NAME)) {
                log.error("配置的证书路径下无必须的 license.lic 和 publicCerts.keystore 文件，程序将退出启动");
                System.exit(0);
            }
        }
        log.info("++++++++ 开始安装证书 ++++++++");

        LicenseVerifyParam param = new LicenseVerifyParam();
        param.setSubject(subject);
        param.setPublicAlias(publicAlias);
        param.setStorePass(storePass);
        param.setLicensePath(licensePath + File.separator + LicenseConfig.LICENSE_NAME);
        param.setPublicKeysStorePath(licensePath + File.separator + LicenseConfig.PUB_KEY_STORE_NAME);

        LicenseVerify licenseVerify = new LicenseVerify();
        //安装证书
        LicenseContent result = licenseVerify.install(param);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info(MessageFormat.format("证书安装成功，证书有效期：{0} ~ {1}", sdf.format(result.getNotBefore()), sdf.format(result.getNotAfter())));
        log.info("++++++++ 证书安装结束 ++++++++");
    }

    public static HardwareInfo getServerInfos () {
        //操作系统类型
        String osName = System.getProperty(ProjectConstant.OS_NAME).toLowerCase();
        AbstractServerInfos abstractServerInfos;

        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith(ProjectConstant.WINDOWS_PREFIX)) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith(ProjectConstant.LINUX_PREFIX)) {
            abstractServerInfos = new LinuxServerInfos();
        } else {
            //其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }
        return abstractServerInfos.getServerInfos();
    }

}
