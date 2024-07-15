package com.swsm.license.server.model;

import com.swsm.license.sdk.model.AbstractLicenseManager;
import com.swsm.license.sdk.model.HardwareInfo;
import de.schlichtherle.license.LicenseContentException;
import de.schlichtherle.license.LicenseParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义LicenseManager，用于增加额外的服务器硬件信息校验
 */
@Slf4j
public class CustomLicenseManager extends AbstractLicenseManager {


    public CustomLicenseManager(LicenseParam param) {
        super(param);
    }

    @Override
    protected void doValidate(Object extra, HardwareInfo hardwareInfo) throws LicenseContentException {
        log.info("此工程不校验");
    }

}
