package example.shareroom.Interceptor;

import example.shareroom.Annotation.LoginRequired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class AuthorityInterceptor implements HandlerInterceptor{


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //如果不是映射到方法则直接过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // ①:START 方法注解级拦截器
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();//获取方法
        // 判断接口是否需要登录
        LoginRequired methodAnnotation = method.getAnnotation(LoginRequired.class);
        // 有 @LoginRequired 注解，需要认证
        if (methodAnnotation != null) {
            /*原计划使用cookie来存储，后放弃
            //获得session_id
            String session_id;
            Cookie[] cookies=request.getCookies();
            for(Cookie item:cookies){
                if(item.getName().equals("session_id")) {
                    session_id=item.getValue();
                    break;
                }
            }*/
            //判断session_id是否有效
            String s=request.getParameter("token");

            if(s==null||s.equals("")) return false;
            String openid=stringRedisTemplate.opsForValue().get(s);
            System.out.println(openid);
            if(openid==null){
                return false;
            }else {
                //在session_id有效后，判断其过期时间，如果小于3分钟，则重新设置为30分钟
                if(stringRedisTemplate.getExpire(s, TimeUnit.SECONDS)<3*60) stringRedisTemplate.expire(s,30*60, TimeUnit.SECONDS);
            }

        }
        //否则直接过
        return true;
    }

}
