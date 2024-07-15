package com.swsm.license.sdk.model;

import cn.hutool.core.util.StrUtil;
import de.schlichtherle.license.AbstractKeyStoreParam;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义KeyStoreParam，用于将公私钥存储文件存放到其他磁盘位置而不是项目中
 */
@Slf4j
public class CustomKeyStoreParam extends AbstractKeyStoreParam {
    /**
     * 公钥/私钥在磁盘上的存储路径
     */
    private String storePath;
    private String alias;
    private String storePwd;
    private String keyPwd;

    public CustomKeyStoreParam(Class clazz, String resource, String alias, String storePwd, String keyPwd) {
        super(clazz, resource);
        this.storePath = resource;
        this.alias = alias;
        this.storePwd = storePwd;
        this.keyPwd = keyPwd;
    }


    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public String getStorePwd() {
        return storePwd;
    }

    @Override
    public String getKeyPwd() {
        return keyPwd;
    }

    /**
     * 复写de.schlichtherle.license.AbstractKeyStoreParam的getStream()方法<br/>
     * 用于将公私钥存储文件存放到其他磁盘位置而不是项目中
     */
    @Override
    public InputStream getStream() throws IOException {
        if (StrUtil.isBlank(storePath)) {
            log.error("配置项license.publicKeysStorePath不能为空，程序将退出启动");
            System.exit(0);
        }
        if (!new File(storePath).exists()) {
            log.error("证书校验失败：{}，程序将退出启动！", storePath + "(系统找不到指定的文件)");
            System.exit(0);
        }
        return new FileInputStream(storePath);
    }
}
