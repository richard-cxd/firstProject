package pw.wechatbrother.base.utils.safety;

import pw.wechatbrother.base.utils.JavaUUIDGenerator;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * des加密解密
 *拿来主义直接用
 * @author zhengjingli
 *
 */
public class DesEncrypt {

    Key key;

    public DesEncrypt(String str) {
        setKey(str);// 生成密匙
    }

    public DesEncrypt() {
        setKey("Yn@2016ByBeautifulGirl");//请勿擅自随意改定这个参数的值 20170523am zjl
    }

    /**
     * 根据参数生成KEY
     */
    public void setKey(String strKey) {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            this.key  = keyFactory.generateSecret(new DESKeySpec(strKey.getBytes("UTF8")));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error initializing SqlMap class. Cause: " + e);
        }
    }


    /**
     * 加密String明文输入,String密文输出
     */
    public String encrypt(String strMing) {
        byte[] byteMi = null;
        byte[] byteMing = null;
        String strMi = "";
        BASE64Encoder base64en = new BASE64Encoder();
        try {
            byteMing = strMing.getBytes("UTF8");
            byteMi = this.getEncCode(byteMing);
            strMi = base64en.encode(byteMi);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error initializing SqlMap class. Cause: " + e);
        } finally {
            base64en = null;
            byteMing = null;
            byteMi = null;
        }
        return strMi;
    }

    /**
     * 解密 以String密文输入,String明文输出
     *
     * @param strMi
     * @return
     */
    public String decrypt(String strMi) {
        BASE64Decoder base64De = new BASE64Decoder();
        byte[] byteMing = null;
        byte[] byteMi = null;
        String strMing = "";
        try {
            byteMi = base64De.decodeBuffer(strMi);
            byteMing = this.getDesCode(byteMi);
            strMing = new String(byteMing, "UTF8");
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error initializing SqlMap class. Cause: " + e);
        } finally {
            base64De = null;
            byteMing = null;
            byteMi = null;
        }
        return strMing;
    }

    /**
     * 加密以byte[]明文输入,byte[]密文输出
     *
     * @param byteS
     * @return
     */
    private byte[] getEncCode(byte[] byteS) {
        byte[] byteFina = null;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key,SecureRandom.getInstance("SHA1PRNG"));
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error initializing SqlMap class. Cause: " + e);
        } finally {
            cipher = null;
        }
        return byteFina;
    }

    /**
     * 解密以byte[]密文输入,以byte[]明文输出
     *
     * @param byteD
     * @return
     */
    private byte[] getDesCode(byte[] byteD) {
        Cipher cipher;
        byte[] byteFina = null;
        try {
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key,SecureRandom.getInstance("SHA1PRNG"));
            byteFina = cipher.doFinal(byteD);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error initializing SqlMap class. Cause: " + e);
        } finally {
            cipher = null;
        }
        return byteFina;
    }
    public static Date format5(String v)
    {
        DateFormat format5= new SimpleDateFormat("yyyyMMddHHmm");
        try {
            return  format5.parse( v.replace(" ",""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public static void main(String args[])  {
       // String string = JavaUUIDGenerator.getUUID();
       /* String string = "d608cb7f46ae4afd911610ede2ed7e43";
        DesEncrypt des = new DesEncrypt(string);
        String password="039186";//039186
        System.out.println(string);
        System.out.println(new DesEncrypt().encrypt(string));
        System.out.println(des.encrypt(password));
        System.out.println(des.decrypt(des.encrypt(password)));*/
        DateFormat format1= new SimpleDateFormat("yyyyMMdd");
        DateFormat format2= new SimpleDateFormat("yyyy年MM月dd日");
        DateFormat format3= new SimpleDateFormat("HHmm");
        DateFormat format4= new SimpleDateFormat("HH时mm分");
        DateFormat format5= new SimpleDateFormat("yyyyMMddHHmm");
        DateFormat format6= new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        try {
            System.out.println(format2.format(format1.parse("20171012")));
            System.out.println(format4.format(format3.parse("2000")));
            System.out.println(format6.format(format5.parse("20171012 2000")));
            System.out.println("========"+format6.format(format5("20171012 2000")));
            System.out.println(format1.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(Float.parseFloat(null));
/*        String lastHour="1530";
        System.out.println(lastHour.substring(0,2));
       System.out.println(Integer.parseInt("00"));*/

      /*  DesEncrypt des = new DesEncrypt();
        String str1 = "15000";
        // DES加密
        String str2 = des.encrypt(str1);
        DesEncrypt des1 = new DesEncrypt();
        String deStr = des1.decrypt(str2);
        System.out.println("密文:" + str2);
        // DES解密
        System.out.println("明文:" + deStr);*/
        //List<String> list =new ArrayList<String>(Arrays.asList(new String[]{})) ;
//        List<String> list =new ArrayList<String>(Arrays.asList(new String[]{"c5dabccec7ef4ca6b757c8d2ed774246"})) ;
//        DesEncrypt desEncrypt = new DesEncrypt();
//        for(String s:list){
//            System.out.println(s+"  "+desEncrypt.encrypt(s)+"   "+desEncrypt.decrypt(desEncrypt.encrypt(s)));
//        }
//        System.out.println(desEncrypt.decrypt("5b5ZIOc6BvCkOcWiBzhpklmMBdUygKS/1Wm2yEkluzFYEzUvUkXXgA=="));
//        System.out.println(new DesEncrypt(desEncrypt.decrypt("5b5ZIOc6BvCkOcWiBzhpklmMBdUygKS/1Wm2yEkluzFYEzUvUkXXgA==")).decrypt("bwNp+bR1biIe+bCFQXGVKE98iqzfeHpyXBCXAMjW0sqM2mAamk077w=="));


       // System.out.println(desEncrypt.decrypt("OiGnhQkUPho="));
//        DesEncrypt des = new DesEncrypt();
//        System.out.println(new DesEncrypt().encrypt("e6344a622a0144e79149065558a2456e"));
//        System.out.println(new DesEncrypt().decrypt("CgK1qoyJ27f54Q+kTeQ7OzYP+2p2YLBbmUDHGPCptXbwkVt8oEinsw=="));
//        System.out.println(new DesEncrypt().decrypt("w4qViLt+5qqpqspeLczgG0qFybkhfAYVPgon88yD12xYEzUvUkXXgA=="));
//        System.out.println(new DesEncrypt().decrypt("w4qViLt+5qqpqspeLczgG0qFybkhfAYVPgon88yD12xYEzUvUkXXgA=="));
//        System.out.println(new DesEncrypt(new DesEncrypt().decrypt("w4qViLt+5qqpqspeLczgG0qFybkhfAYVPgon88yD12xYEzUvUkXXgA==")).decrypt("+TofTStsJnS0y9TMTffYQtgWGItJBG7P"));
    }

}