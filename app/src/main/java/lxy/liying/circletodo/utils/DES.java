package lxy.liying.circletodo.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/11 13:21
 * 版本：1.0
 * 描述：将字符串进行DES加密解密
 * 备注：
 * =======================================================
 */
public class DES {

    /**
     * 加密KEY
     */
    private static byte[] KEY;
    /**
     * 算法
     */
    private static final String ALGORITHM = "DES";
    /**
     * IV
     */
    private static byte[] IV;
    /**
     * TRANSFORMATION
     */
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";

    static {
        KEY = Constants.SEED.getBytes();
        IV = "ASDQOPUI".getBytes();
    }

    /**
     * 将字符串进行DES加密
     *
     * @param source 未加密源字符串
     * @return 加密后字符串
     */
    public static String encrypt(String source) {
        byte[] retByte = null;

        // Create SecretKey object
        DESKeySpec dks = null;
        try {
            dks = new DESKeySpec(KEY);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey securekey = keyFactory.generateSecret(dks);

            // Create IvParameterSpec object with initialization vector
            IvParameterSpec spec = new IvParameterSpec(IV);

            // Create Cipter object
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // Initialize Cipher object
            cipher.init(Cipher.ENCRYPT_MODE, securekey, spec);

            // Decrypting data
            retByte = cipher.doFinal(source.getBytes());

            String result = Base64.encodeToString(retByte, 0);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将DES加密的字符串解密
     *
     * @param encrypted 加密过的字符串
     * @return 未加密源字符串
     */
    public static String decrypt(String encrypted) {
        byte[] retByte = null;

        // Create SecretKey object
        DESKeySpec dks = null;
        try {
            dks = new DESKeySpec(KEY);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey securekey = keyFactory.generateSecret(dks);

            // Create IvParameterSpec object with initialization vector
            IvParameterSpec spec = new IvParameterSpec(IV);

            // Create Cipter object
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // Initialize Cipher object
            cipher.init(Cipher.DECRYPT_MODE, securekey, spec);

            retByte = Base64.decode(encrypted, 0);
            // Decrypting data
            retByte = cipher.doFinal(retByte);
            return new String(retByte, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
