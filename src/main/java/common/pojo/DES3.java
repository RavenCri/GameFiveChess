package common.pojo;

/**
 * @description:
 * @author: raven
 * @create: 2020-04-06 13:42
 *  *  - 第一段是加密算法的名称，如DESede实际上是3-DES。
 *  *  这一段还可以放其它的对称加密算法，如Blowfish等。
 *  *
 *  * - 第二段是分组加密的模式，除了CBC和ECB之外，还可以是NONE/CFB/QFB等。最常用的就是CBC和ECB了
 *  * 。DES采用分组加密的方式，将明文按8字节（64位）分组分别加密。如果每个组独立处理，则是ECB。
 *  * CBC的处理方式是先用初始向量IV对第一组加密，再用第一组的密文作为密钥对第二组加密，然后依次完成整个加密操作
 *  * 。如果明文中有两个分组的内容相同，ECB会得到完全一样的密文，但CBC则不会。
 *  *
 *  * - 第三段是指最后一个分组的填充方式。大部分情况下，明文并非刚好64位的倍数。对于最后一个分组，如果长度小于64位，
 *  * 则需要用数据填充至64位。PKCS5Padding是常用的填充方式，如果没有指定，默认的方式就是它。
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
