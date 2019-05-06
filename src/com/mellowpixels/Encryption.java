package com.mellowpixels;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;


public class Encryption {

    byte[] secretKey;

    public Encryption(String secretKey) {
        this.secretKey = secretKey.getBytes();
    }


    public String encrypt(String message)
            throws Exception
    {
        SecretKeySpec secretKey = new SecretKeySpec(this.secretKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(message.getBytes()).toString();
    }




    public String decrypt(String encrypted)
            throws Exception
    {
        SecretKeySpec secretKey = new SecretKeySpec(this.secretKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encrypted.getBytes()).toString();
    }
}
