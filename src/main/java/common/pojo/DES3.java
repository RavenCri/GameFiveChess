package common.pojo;

/**
 * @description:
 * @author: raven
 * @create: 2020-04-06 13:42
 **/
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DES3 {
    public static final String encoding = "utf-8";
    public static final String iv = "01234567";
    public static final String secretKey = "XCE927TcgEHSJNmskYR+Dw==";

    public static String decode(String str) throws Exception {
        SecretKey generateSecret = SecretKeyFactory.getInstance("desede").generateSecret(new DESedeKeySpec(secretKey.getBytes()));
        Cipher instance = Cipher.getInstance("desede/CBC/PKCS5Padding");
        instance.init(2, generateSecret, new IvParameterSpec(iv.getBytes()));
        return new String(instance.doFinal(Base64.decode(str)), encoding);
    }

    public static String encode(String str) throws Exception {
        SecretKey generateSecret = SecretKeyFactory.getInstance("desede").generateSecret(new DESedeKeySpec(secretKey.getBytes()));
        Cipher instance = Cipher.getInstance("desede/CBC/PKCS5Padding");
        instance.init(1, generateSecret, new IvParameterSpec(iv.getBytes()));
        return Base64.encode(instance.doFinal(str.getBytes(encoding)));
    }

    public static void main(String[] args) throws Exception {
        String str = encode("hello world");
        System.out.println("加密后的字符："+str);
        System.out.println("解密后的字符："+decode(str));
    }
}
