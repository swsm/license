License
# 说明
底层使用truelicense 实现，使用SpringBoot为基本框架
- myLicense-sdk 为license的基础sdk，封装了license的基本功能
- myLicenseClient 为使用license的客户端系统,依赖myLicense-sdk,可以自定义license校验的项和内容
- myLicense 为管理和生成license的服务端,可以直接下载license压缩包

# 前置处理
当前我们使用JDK自带的KeyTool工具进行制作
```bash

keytool：Java提供的用于管理密钥库和证书的工具。

-genkey：生成一个新密钥对。
-alias privatekeys：给生成的密钥对设置一个别名为privatekeys。
-keyalg DSA：使用DSA算法生成密钥对。
-keysize 1024：密钥长度为1024位。
-keystore privateKeys.store：将生成的密钥对保存在名为privateKeys.store的密钥库文件中。
-validity 3650：指定密钥的有效期为3650天，即10年。

命令行：keytool -genkey -alias privatekeys -keyalg DSA -keysize 1024 -keystore privateKeys.store -validity 3650

-export：导出指定别名的证书或公钥。
-alias privatekeys：指定要导出的证书或公钥的别名为privatekeys。
-file certfile.cer：指定要导出的证书的输出文件为certfile.cer。
-keystore privateKeys.store：指定要使用的密钥库文件为privateKeys.store。

命令行：keytool -export -alias privatekeys -file certfile.cer -keystore privateKeys.store


-import：导入一个证书或公钥。
-alias publiccert：指定导入的证书或公钥的别名为publiccert。
-file certfile.cer：指定要导入的证书文件为certfile.cer。
-keystore publicCerts.store：指定要保存导入证书或公钥的密钥库文件为publicCerts.store。
命令行：keytool -import -alias publiccert -file certfile.cer -keystore publicCerts.store

最后生成的文件privateKeys.store(私钥)和publicCerts.store(公钥)拷贝出来备用。
```


