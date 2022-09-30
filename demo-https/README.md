# spring-boot-demo-https

> This demo mainly demonstrates how Spring Boott integrates https

## 1. Generate a certificate

First use the keytool command that comes with jdk to copy the certificate to the project's 'resources' directory (the generated certificate is generally in the user directory C:\Users\Administrator\server.keystore)

> the self-generated certificate browser will have a dangerous tip, but not go to the ssl website to apply for money

! [ssl command screenshot] (ssl.png)

## 2. Add a configuration

1. Configure the generated certificate in the configuration file

```yaml
server:
  ssl:
    # Certification path
    key-store: classpath:server.keystore
    key-alias: tomcat
    enabled: true
    key-store-type: JKS
    #与申请时输入一致
    key-store-password: 123456
    # Browser default port is similar to 80
  port: 443
```

2. Configure Tomcat

```java
/**
 * <p>
 * HTTPS configuration class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2020-01-19 10:31
 */
@Configuration
public class HttpsConfig {
    /**
     * Configure http(80) - > Force jump to https(443)
     */
    @Bean
    public Connector connector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(80);
        connector.setSecure(false);
        connector.setRedirectPort(443);
        return connector;
    }

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(Connector connector) {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }
}
```

## 3. Test

Start the project and browser access http://localhost will automatically jump to the https://localhost

## 4. reference

- 'keytool' command reference

```bash
$ keytool --help
Key and certificate management tools

Command:

 -certreq generates a certificate request
 -changealias changes the alias of the entry
 -delete delete entry
 -exportcert export the certificate
 -genkeypair generates a key pair
 -genseckey generates the key
 -gencert generates a certificate based on a certificate request
 -importcert Import the certificate or certificate chain
 -importpass import password
 -importkeystore imports one or all entries from other keystores
 -keypasswd changes the key password for an entry
 -list lists the entries in the keystore
 -printcert prints the certificate contents
 -printcertreq prints the contents of the certificate request
 -printcrl prints the contents of the CRL file
 -storepasswd changes the storage password for the keystore

Use "keytool -command_name -help" to get the usage of command_name
```

- [Introduction to Java Keytool Tools] (https://blog.csdn.net/liumiaocn/article/details/61921014)
