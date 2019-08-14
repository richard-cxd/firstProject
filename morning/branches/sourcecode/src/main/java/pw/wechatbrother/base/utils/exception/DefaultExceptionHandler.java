package pw.wechatbrother.base.utils.exception;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by zhengjingli on 2016/11/19.
 */
public class DefaultExceptionHandler extends SimpleMappingExceptionResolver {
    private final static Log logger = LogFactory.getLog(DefaultExceptionHandler.class);
    private final static int ERROR_CODE = 600;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object obj, Exception e) {
        response.setCharacterEncoding("UTF-8"); //避免乱码
        response.setStatus(ERROR_CODE); //设置状态码
        String requestUrl = request.getRequestURI();
        String errorMsg = "请求发生异常";
        if (e instanceof BusinessException) {
            errorMsg = e.getMessage();
        }

        logger.warn("errorCode：" + ERROR_CODE + "，url：" + requestUrl + "，errorMsg：" + errorMsg, e);

        if (isAjax(request)) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE); //设置ContentType
            try {
                PrintWriter out = response.getWriter();
                out.write("{\"success\":false,\"msg\":\"" + errorMsg + "\",\"developerMsg\":\"" + e + "\",\"requestUrl\":\"" + requestUrl + "\"}");
                out.flush();
                out.close();
            } catch (IOException e1) {
                throw new BusinessException(errorMsg);
            }
            return new ModelAndView();
        } else {
            ModelAndView model = new ModelAndView("error/600");
            model.addObject("msg", errorMsg);
            return model;
        }
    }

    private boolean isAjax(HttpServletRequest request) {
        return request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }
}
