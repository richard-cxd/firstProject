package webService;


import com.taigu.endpoint.IUploadDataAction;
import com.taigu.endpoint.Services;

/**
 * Created by YN on 2018/1/18.
 */
public class Client {
   public static void main(String[] args) throws Exception {
       Services services = new Services();
       IUploadDataAction iUploadDataAction = services.getUploadDataActionImplPort();
       String data = AESUtil.Encrypt(DataUtil.JsonData(), "WOSHI123456MIYAO");
       String s = iUploadDataAction.uploadAllData(data);
       System.out.println(s);

   }



}
