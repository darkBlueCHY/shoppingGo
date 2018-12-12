package com.CHY.utils;

import org.springframework.util.DigestUtils;

import java.util.Base64;

/**
 * @author CHY
 * @create 2018-12-02 17:02
 */
public class EncoderUtils {
    private EncoderUtils(){ };

    public static String MD5Bas64Encode(String cleartext){
        return new String(Base64.getEncoder().encode(DigestUtils.md5Digest(cleartext.getBytes())));
    }

    public static String MD5Bas64EncodeNoTail(String cleartext){
        String ciphertext = MD5Bas64Encode(cleartext);
        return ciphertext.substring(0, ciphertext.length() - 2);
    }
}
