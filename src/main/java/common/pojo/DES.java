package common.pojo;

import ch.randelshofer.quaqua.ext.base64.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @description:
 * @author: raven
 * @create: 2020-04-06 13:53
 *  - 第一段是加密算法的名称，如DESede实际上是3-DES。
 *  这一段还可以放其它的对称加密算法，如Blowfish等。
 *
 * - 第二段是分组加密的模式，除了CBC和ECB之外，还可以是NONE/CFB/QFB等。最常用的就是CBC和ECB了
 * 。DES采用分组加密的方式，将明文按8字节（64位）分组分别加密。如果每个组独立处理，则是ECB。
 * CBC的处理方式是先用初始向量IV对第一组加密，再用第一组的密文作为密钥对第二组加密，然后依次完成整个加密操作
 * 。如果明文中有两个分组的内容相同，ECB会得到完全一样的密文，但CBC则不会。
 *
 * - 第三段是指最后一个分组的填充方式。大部分情况下，明文并非刚好64位的倍数。对于最后一个分组，如果长度小于64位，
 * 则需要用数据填充至64位。PKCS5Padding是常用的填充方式，如果没有指定，默认的方式就是它。
 **/
public class DES {
    public static byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};
    public static String key = "XCE927==";
    public static String decode(String str) {
        try {
            byte[] decode = Base64.decode(str);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "DES");
            Cipher instance = Cipher.getInstance("DES/CBC/PKCS5Padding");
            instance.init(2, secretKeySpec, ivParameterSpec);
            return new String(instance.doFinal(decode));
        } catch (Exception unused) {
            unused.printStackTrace();
            return "";
        }
    }

    public static String encode(String str) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "DES");
            Cipher instance = Cipher.getInstance("DES/CBC/PKCS5Padding");
            instance.init(1, secretKeySpec, ivParameterSpec);
            return Base64.encodeBytes(instance.doFinal(str.getBytes()));
        } catch (Exception unused) {
            unused.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        String str = encode("hello world");
        System.out.println("加密后的字符："+str);
        System.out.println("解密后的字符："+decode(str));
    }
}
