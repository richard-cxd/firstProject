package webService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pw.wechatbrother.base.service.WebServiceService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by YN on 2018/1/18.
 */
//每5分钟调用一次webService
/*@Controller*/
public class JobQuartz {
    @Autowired
private WebServiceService webServiceService;
    public void work() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("当前时间:"+sdf.format(new Date()));
    }
}
