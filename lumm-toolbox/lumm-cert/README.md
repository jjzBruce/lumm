```shell
# 生成PKCS12格式的密钥库
keytool -genkeypair -alias tomcat12_8 -keyalg RSA -keysize 2048 -validity 3650 -keystore tomcat12_8.p12 -storetype PKCS12

# 导出证书
keytool -exportcert -keystore tomcat12_8.p12 -file tomcat12_8.cer -alias tomcat12_8

# 如果需要将证书导入到另一个密钥库，也使用PKCS12格式
keytool -importcert -keystore client_trust.p12 -file tomcat12_8.cer -alias tomcat12_8 -noprompt
```

