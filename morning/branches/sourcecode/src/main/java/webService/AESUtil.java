package webService;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by YN on 2018/1/18.
 */
public class AESUtil {


    /**
     * 加密
     */
    public static String Encrypt(String sSrc, String key) throws
            Exception {
        if (checkKey(key)) {
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes());
            return byte2hex(encrypted).toLowerCase();
        }
        return null;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

    public static boolean checkKey(String key) throws Exception {
        if (key == null) {
/*"密码为空！"错误处理代码*/
        } else if (key.length() != 16) {
/*"Key长度不是16位！"错误处理代码*/
        }
        return true;
    }
}
