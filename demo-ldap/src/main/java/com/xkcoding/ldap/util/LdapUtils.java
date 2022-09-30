package com.xkcoding.ldap.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * LdapUtils
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-08-26 1:03
 */
public class LdapUtils {

    /**
     * Verify password
     *
     * @param ldapPassword ldap encryption password
     * @param inputPassword user input
     * @return boolean
     * @throws NoSuchAlgorithmException encryption and decryption exception
     */
    public static boolean verify(String ldapPassword, String inputPassword) throws NoSuchAlgorithmException {

        MessageDigest provides the functionality of message digest algorithms, such as MD5 or SHA, where LDAP uses SHA-1
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        Check out the encrypting characters
        if (ldapPassword.startsWith("{SSHA}")) {
            ldapPassword = ldapPassword.substring(6);
        } else if (ldapPassword.startsWith("{SHA}")) {
            ldapPassword = ldapPassword.substring(5);
        }
        Decode BASE64
        byte[] ldapPasswordByte = Base64.decode(ldapPassword);
        byte[] shaCode;
        byte[] salt;

        The first 20 bits are the SHA-1 encryption segment, and after the 20 bits is the random plaintext at the time of the initial encryption
        if (ldapPasswordByte.length <= 20) {
            shaCode = ldapPasswordByte;
            salt = new byte[0];
        } else {
            shaCode = new byte[20];
            salt = new byte[ldapPasswordByte.length - 20];
            System.arraycopy(ldapPasswordByte, 0, shaCode, 0, 20);
            System.arraycopy(ldapPasswordByte, 20, salt, 0, salt.length);
        }
        Adds the user-entered password to the summary calculation information
        md.update(inputPassword.getBytes());
        Add random plaintext to the summary calculation information
        md.update(salt);

        Calculate the current user password by pressing SSHA
        byte[] inputPasswordByte = md.digest();

        Returns the validation results
        return MessageDigest.isEqual(shaCode, inputPasswordByte);
    }

    /**
     * Ascii converted to string
     *
     * @param value Ascii string
     * @return string
     */
    public static String asciiToString(String value) {
        StringBuilder sbu = new StringBuilder();
        String[] chars = value.split(",");
        for (String aChar : chars) {
            sbu.append((char) Integer.parseInt(aChar));
        }
        return sbu.toString();
    }

}
