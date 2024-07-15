package com.swsm.license.server.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.swsm.license.sdk.model.AbstractServerInfos;
import com.swsm.license.sdk.model.HardwareInfo;
import com.swsm.license.sdk.model.LinuxServerInfos;
import com.swsm.license.sdk.model.WindowsServerInfos;
import com.swsm.license.server.common.ResultVO;
import com.swsm.license.server.config.LicenseConfig;
import com.swsm.license.server.constant.ProjectConstant;
import com.swsm.license.server.controller.req.CreateLicenseReq;
import com.swsm.license.server.model.LicenseCreator;
import com.swsm.license.server.model.LicenseCreatorParam;
import com.swsm.license.server.util.ZipUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.nio.file.StandardCopyOption;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于生成证书文件，不能放在给客户部署的代码里
 */
@Slf4j
@Api(tags = "证书API")
@RestController
@RequestMapping("/license")
public class LicenseController {

    @ApiOperation("获取服务器信息")
    @GetMapping(value = "/getServerInfos")
    public HardwareInfo getServerInfos(
            @RequestParam(value = "osName", required = false) String osName) {
        //操作系统类型
        if (StrUtil.isBlank(osName)) {
            osName = System.getProperty(ProjectConstant.OS_NAME);
        }
        osName = osName.toLowerCase();

        AbstractServerInfos abstractServerInfos;

        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith("windows")) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith("linux")) {
            abstractServerInfos = new LinuxServerInfos();
        } else {//其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }
        return abstractServerInfos.getServerInfos();
    }


    @ApiOperation("生成license")
    @PostMapping(value = "/createLicense")
    public ResultVO<String> createLicense(@RequestBody @Validated CreateLicenseReq req) {
        LicenseCreatorParam param = new LicenseCreatorParam();
        param.setSubject(req.getSubject());
        param.setPrivateAlias(LicenseConfig.privateAlias);
        param.setKeyPass(LicenseConfig.privateStorePass);
        param.setPublicStorePass(LicenseConfig.publicStorePass);
        param.setLicensePath(LicenseConfig.licensePath);
        param.setPrivateKeysStorePath(LicenseConfig.licensePath + File.separator + LicenseConfig.PRI_KEY_STORE_NAME);
        param.setIssuedTime(req.getStartTime());
        param.setExpiryTime(req.getExpiryTime());
        param.setCustomerType(req.getCustomerType());
        param.setDescription(req.getDescription());
        LicenseCreatorParam.LicenseCheckModel licenseCheckModel = LicenseCreatorParam.LicenseCheckModel.builder()
                .cpuSerial(req.getLicenseCheckInfo().getCpuSerial())
                .ipAddress(req.getLicenseCheckInfo().getIpAddress())
                .macAddress(req.getLicenseCheckInfo().getMacAddress())
                .mainBoardSerial(req.getLicenseCheckInfo().getMainBoardSerial())
                .equipmentCount(req.getLicenseCheckInfo().getEquipmentCount()).build();
        param.setLicenseCheckModel(licenseCheckModel);

        LicenseCreator licenseCreator = new LicenseCreator(param);
        boolean result = licenseCreator.generateLicense(param.getSubject() + File.separator + LicenseConfig.LICENSE_NAME);
        if (!result) {
            return ResultVO.getError("生成失败");
        }
        String publicKeyStoreFile = LicenseConfig.licensePath + File.separator + LicenseConfig.PUB_KEY_STORE_NAME;
        // 将公钥拷贝至生成的证书的目录下
        String destPath = LicenseConfig.licensePath + File.separator + param.getSubject();
        FileUtil.copyFile(publicKeyStoreFile, destPath);
        return ResultVO.getSuccess(destPath);
    }


    @ApiOperation("下载license")
    @PostMapping(value = "/downloadLicense")
    public void createLicense(@RequestBody @Validated CreateLicenseReq req, HttpServletResponse response) {
        LicenseCreatorParam param = new LicenseCreatorParam();
        param.setSubject(req.getSubject());
        param.setPrivateAlias(LicenseConfig.privateAlias);
        param.setKeyPass(LicenseConfig.privateStorePass);
        param.setPublicStorePass(LicenseConfig.publicStorePass);
        param.setLicensePath(LicenseConfig.licensePath);
        param.setPrivateKeysStorePath(LicenseConfig.licensePath + File.separator + LicenseConfig.PRI_KEY_STORE_NAME);
        param.setIssuedTime(req.getStartTime());
        param.setExpiryTime(req.getExpiryTime());
        param.setCustomerType(req.getCustomerType());
        param.setDescription(req.getDescription());
        LicenseCreatorParam.LicenseCheckModel licenseCheckModel = LicenseCreatorParam.LicenseCheckModel.builder()
                .cpuSerial(req.getLicenseCheckInfo().getCpuSerial())
                .ipAddress(req.getLicenseCheckInfo().getIpAddress())
                .macAddress(req.getLicenseCheckInfo().getMacAddress())
                .mainBoardSerial(req.getLicenseCheckInfo().getMainBoardSerial())
                .equipmentCount(req.getLicenseCheckInfo().getEquipmentCount()).build();
        param.setLicenseCheckModel(licenseCheckModel);

        LicenseCreator licenseCreator = new LicenseCreator(param);
        boolean result = licenseCreator.generateLicense(param.getSubject() + File.separator + LicenseConfig.LICENSE_NAME);
        if (!result) {
            throw new RuntimeException("生成失败");
        }
        String publicKeyStoreFile = LicenseConfig.licensePath + File.separator + LicenseConfig.PUB_KEY_STORE_NAME;
        // 将公钥拷贝至生成的证书的目录下
        String destPath = LicenseConfig.licensePath + File.separator + param.getSubject();
        FileUtil.copyFile(publicKeyStoreFile, destPath, StandardCopyOption.REPLACE_EXISTING);
        String outputFileName = req.getSubject() + "-license.zip";
        try {
            byte[] data = ZipUtil.getOutputBytes(response, destPath, outputFileName, outputFileName, true, true);
            IOUtils.write(data, response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("生成失败");
        }
    }
}
