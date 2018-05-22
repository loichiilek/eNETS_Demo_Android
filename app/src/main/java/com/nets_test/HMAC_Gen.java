package com.nets_test;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class exists to generate the HMAC
 */

public class HMAC_Gen {
    public static String generateSignature(String txnReq, String secretKey){
        String concatPayloadAndSecretKey = txnReq + secretKey;
        String hmac = encodeBase64(hashSHA256ToBytes(concatPayloadAndSecretKey.getBytes()));
        Log.d("HMAC", "hmac: " + hmac);
        return hmac.replace("\n", "");
    }

    public static byte[] hashSHA256ToBytes(byte[] input){
        byte[] byteData;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input);
            byteData = md.digest();
            Log.d("hashSHA256ToBytes", "OK");
            return byteData;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String encodeBase64(byte[] data){


        Log.d("encodeBase64", Base64.encodeToString(data, Base64.DEFAULT));
        return Base64.encodeToString(data, Base64.DEFAULT);

    }
}
