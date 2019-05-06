package com.mellowpixels;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;


class Cryptograph {

    public String key;

    public Cryptograph(String skey) {
        this.key = skey;
    }

    private Key getKey() throws Exception {
        return new SecretKeySpec(Arrays.copyOf(key.getBytes("UTF-8"), 16), "AES");
    }


    public String encrypt(String pass) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, getKey());
        byte[] encrypted = cipher.doFinal(pass.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String encryptedStr) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(encryptedStr);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, getKey());

        String de = new String(cipher.doFinal(encrypted));
//        System.out.println("Decrypting -->" + encryptedStr + "  -->  " + de);

        return new String(cipher.doFinal(encrypted));
    }
}