package pw.wechatbrother.base.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

/**
 * Created by Administrator on 16-1-27.
 */
@Controller
public class BaseController {
    @Value("#{configProperties[UPLOAD_ROOT]}")
    public String uploadRoot;
    @Value("#{configProperties[WEB_IMG_URL]}")
    public String web_img_url;
    @Value("#{configProperties[Monitoring_Password]}")
    public String monitoringPassword;
    @Value("#{configProperties[WARNINGBACKDAY]}")
    public String warningBackDay;

}