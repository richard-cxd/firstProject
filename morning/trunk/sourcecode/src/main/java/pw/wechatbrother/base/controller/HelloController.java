package pw.wechatbrother.base.controller;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pw.wechatbrother.base.domain.*;
import pw.wechatbrother.base.service.UserService;
import pw.wechatbrother.base.utils.file.FileUtil;
import pw.wechatbrother.base.utils.jfreechart.Line;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
// 处理酒店信息
@RequestMapping("/hello")
public class HelloController extends BaseController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/testhotel", method = RequestMethod.GET)
    public String printWelcome(ModelMap model,HttpServletRequest request,
                               HttpServletResponse response,String openid) {
System.out.println("测试获取的微信ID========================"+openid);
       User userSession= (User) request.getSession().getAttribute("user");
        System.out.println("base HelloController 中获取到的用户session值为=="+JSONObject.fromObject(userSession));

        List<UserD> user = (List<UserD>) userService.select("1");
        model.addAttribute("users",user);
        model.addAttribute("message", "测试，你好！");
        return "hello";
    }

    @RequestMapping(value = "/showUser", method = RequestMethod.GET)
    public String sayHello(ModelMap model) {
        //UserD userD = userService.getUserById(1);
        //model.addAttribute("message", "Hello," + userT.getUserName());
        List<UserD> user = (List<UserD>) userService.select("1");
        model.addAttribute("users",user);
        model.addAttribute("message", "郑景立的地盘，你好！");
        return "hello";
    }

   // 生成二维码
    @RequestMapping(value = "/createQRCode", method = RequestMethod.GET)
    public void createQRCode(ModelMap model,HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        String url="http://www.yn5180.com/ynFileServer/file/workReport/repair/20170724/a37d415758674edcb5269a1088d351da.pdf";

       File file= QRCode.from(url).to(ImageType.PNG).file();
        //导出文件到web前端
       /*
       ByteArrayOutputStream out = QRCode.from(url).to(ImageType.PNG).stream();
       response.setContentType("image/png");
        response.setContentLength(out.size());
        OutputStream outStream = response.getOutputStream();
        outStream.write(out.toByteArray());
        outStream.flush();
        outStream.close();*/
        FileUtil.saveTmpFile(file,"d://123.jpg");//生成文件到指定目录

    }


}