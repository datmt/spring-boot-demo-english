package com.xkcoding.email;

import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * Database password test
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-08-27 16:15
 */
public class PasswordTest extends SpringBootDemoEmailApplicationTests {
    @Autowired
    private StringEncryptor encryptor;

    /**
     * Generate an encryption password
     */
    @Test
    public void testGeneratePassword() {
        Your email password
        String password = "Just4Test!";
        Encrypted password (Note: ENC (encryption password) is required when configuring)
        String encryptPassword = encryptor.encrypt(password);
        String decryptPassword = encryptor.decrypt(encryptPassword);

        System.out.println("password = " + password);
        System.out.println("encryptPassword = " + encryptPassword);
        System.out.println("decryptPassword = " + decryptPassword);
    }
}
