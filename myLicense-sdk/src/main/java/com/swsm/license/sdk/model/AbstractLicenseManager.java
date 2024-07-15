package com.swsm.license.sdk.model;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.swsm.license.sdk.constant.ProjectConstant;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseContentException;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseNotary;
import de.schlichtherle.license.LicenseParam;
import de.schlichtherle.license.NoLicenseInstalledException;
import de.schlichtherle.xml.GenericCertificate;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义LicenseManager，用于增加额外的服务器硬件信息校验
 */
@Slf4j
public abstract class AbstractLicenseManager extends LicenseManager {

    //XML编码
    private static final String XML_CHARSET = "UTF-8";
    //默认BUFSIZE
    private static final int DEFAULT_BUFSIZE = 8 * 1024;

    public AbstractLicenseManager(LicenseParam param) {
        super(param);
    }

    /**
     * 复写create方法
     */
    @Override
    protected synchronized byte[] create(
            LicenseContent content,
            LicenseNotary notary)
            throws Exception {
        initialize(content);
        this.validateCreate(content);
        final GenericCertificate certificate = notary.sign(content);
        return getPrivacyGuard().cert2key(certificate);
    }

    /**
     * 复写install方法，其中validate方法调用本类中的validate方法，校验IP地址、Mac地址等其他信息
     */
    @Override
    protected synchronized LicenseContent install(final byte[] key, final LicenseNotary notary) throws Exception {
        final GenericCertificate certificate = getPrivacyGuard().key2cert(key);

        try {
            notary.verify(certificate);
            final LicenseContent content = (LicenseContent) this.load(certificate.getEncoded());
            assert content != null;
            log.info("证书有效期范围：{} ~ {}", content.getIssued(), content.getNotAfter());
            this.validate(content);
            setLicenseKey(key);
            setCertificate(certificate);
            return content;
        } catch (Exception e) {
            log.error("证书校验失败：{} 程序将退出启动！", e.getLocalizedMessage());
            System.exit(0);
        }
        return null;
    }

    /**
     * 复写verify方法，调用本类中的validate方法，校验IP地址、Mac地址等其他信息
     */
    @Override
    protected synchronized LicenseContent verify(final LicenseNotary notary) throws Exception {

        // Load license key from preferences,
        final byte[] key = getLicenseKey();
        if (null == key) {
            throw new NoLicenseInstalledException(getLicenseParam().getSubject());
        }

        GenericCertificate certificate = getPrivacyGuard().key2cert(key);
        notary.verify(certificate);
        final LicenseContent content = (LicenseContent) this.load(certificate.getEncoded());
        this.validate(content);
        setCertificate(certificate);

        return content;
    }

    /**
     * 校验生成证书的参数信息
     *
     * @param content 证书正文
     */
    protected synchronized void validateCreate(final LicenseContent content) throws LicenseContentException {
        final Date now = new Date();
        final Date notBefore = content.getNotBefore();
        final Date notAfter = content.getNotAfter();
        if (null != notAfter && now.after(notAfter)) {
            throw new LicenseContentException("证书失效时间不能早于当前时间");
        }
        if (null != notBefore && null != notAfter && notAfter.before(notBefore)) {
            throw new LicenseContentException("证书生效时间不能晚于证书失效时间");
        }
        final String consumerType = content.getConsumerType();
        if (null == consumerType) {
            throw new LicenseContentException("用户类型不能为空");
        }
    }


    /**
     * 复写validate方法，增加IP地址、Mac地址等其他信息校验
     */
    @Override
    protected synchronized void validate(final LicenseContent content) throws LicenseContentException {
        //1. 首先调用父类的validate方法
        super.validate(content);
        
        //当前服务器真实的参数信息
        HardwareInfo hardwareInfo = getServerInfos();
        
        //2. 然后校验自定义校验逻辑
        doValidate(content.getExtra(), hardwareInfo);

    }

    protected abstract void doValidate(Object extra, HardwareInfo hardwareInfo) throws LicenseContentException;


    /**
     * 重写XMLDecoder解析XML
     */
    protected Object load(String encoded) {
        BufferedInputStream inputStream = null;
        XMLDecoder decoder = null;
        try {
            inputStream = new BufferedInputStream(new ByteArrayInputStream(encoded.getBytes(XML_CHARSET)));

            decoder = new XMLDecoder(new BufferedInputStream(inputStream, DEFAULT_BUFSIZE), null, null);

            return decoder.readObject();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            try {
                if (decoder != null) {
                    decoder.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                log.error("XMLDecoder解析XML失败", e);
            }
        }

        return null;
    }

    /**
     * 获取当前服务器需要额外校验的License参数
     */
    protected HardwareInfo getServerInfos() {
        //操作系统类型
        String osName = System.getProperty(ProjectConstant.OS_NAME).toLowerCase();
        AbstractServerInfos abstractServerInfos;

        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith(ProjectConstant.WINDOWS_PREFIX)) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith(ProjectConstant.LINUX_PREFIX)) {
            abstractServerInfos = new LinuxServerInfos();
        } else {//其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }

        return abstractServerInfos.getServerInfos();
    }

    /**
     * 校验当前服务器的IP/Mac地址是否在可被允许的IP范围内<br/>
     * 如果存在IP在可被允许的IP/Mac地址范围内，则返回true
     */
    protected boolean checkIpAddress(List<String> expectedList, List<String> serverList) {
        if (CollectionUtil.isNotEmpty(expectedList) && CollectionUtil.isNotEmpty(serverList)) {
            // 如果当前服务器ip集合已包含了许可证允许的ip集合，则校验通过
            return serverList.containsAll(expectedList);
        } else {
            return false;
        }
    }

    /**
     * 校验当前服务器硬件（主板、CPU等）序列号是否在可允许范围内
     */
    protected boolean checkSerial(String expectedSerial, String serverSerial) {
        if (StrUtil.isNotBlank(expectedSerial) && StrUtil.isNotBlank(serverSerial)) {
            return expectedSerial.equals(serverSerial);
        } else {
            return false;
        }
    }

}
