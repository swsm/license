package com.swsm.license.client.model;

import com.swsm.license.sdk.model.AbstractLicenseManager;
import com.swsm.license.sdk.model.HardwareInfo;
import de.schlichtherle.license.LicenseContentException;
import de.schlichtherle.license.LicenseParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义LicenseManager，用于增加额外的服务器硬件信息校验
 *
 */
@Slf4j
public class CustomLicenseManager extends AbstractLicenseManager {
    
    public CustomLicenseManager(LicenseParam param) {
        super(param);
    }

    @Override
    protected void doValidate(Object extra, HardwareInfo hardwareInfo) throws LicenseContentException {
        LicenseCreatorParam.LicenseCheckModel expectedCheckModel = (LicenseCreatorParam.LicenseCheckModel) extra;

        if (expectedCheckModel != null && hardwareInfo != null) {
            //校验IP地址
            if (!checkIpAddress(expectedCheckModel.getIpAddress(), hardwareInfo.getIpAddress())) {
                throw new LicenseContentException("当前服务器的IP没在授权范围内");
            }

            //校验Mac地址
            if (!checkIpAddress(expectedCheckModel.getMacAddress(), hardwareInfo.getMacAddress())) {
                throw new LicenseContentException("当前服务器的Mac地址没在授权范围内");
            }

            //校验主板序列号
            if (!checkSerial(expectedCheckModel.getMainBoardSerial(), hardwareInfo.getMainBoardSerial())) {
                throw new LicenseContentException("当前服务器的主板序列号没在授权范围内");
            }

            //校验CPU序列号
            if (!checkSerial(expectedCheckModel.getCpuSerial(), hardwareInfo.getCpuSerial())) {
                throw new LicenseContentException("当前服务器的CPU序列号没在授权范围内");
            }
        } else {
            throw new LicenseContentException("不能获取服务器硬件信息");
        }
    }

}
