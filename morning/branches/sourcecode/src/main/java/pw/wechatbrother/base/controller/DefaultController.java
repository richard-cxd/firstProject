package pw.wechatbrother.base.controller;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pw.wechatbrother.base.domain.User;
import pw.wechatbrother.base.domain.UserD;
import pw.wechatbrother.base.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
// 处理酒店信息
@RequestMapping("/")
public class DefaultController extends BaseController {
    @Autowired
    private UserService userService;



    @RequestMapping(method = RequestMethod.GET)
    public String sayHello(ModelMap model) {
        //UserD userD = userService.getUserById(1);
        //model.addAttribute("message", "Hello," + userT.getUserName());
        List<UserD> user = (List<UserD>) userService.select("1");
        model.addAttribute("users",user);
        model.addAttribute("message", "BY-郑景立");
        return "hello";
    }







}