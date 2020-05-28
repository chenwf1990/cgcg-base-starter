package com.cgcg.base.interceptor;

import com.cgcg.base.util.RequestApiUtils;
import com.cgcg.context.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求日志拦截器
 * The type Page interceptor.
 *
 * @author zc.lin
 */
@Slf4j
public class RequestInterceptor extends HandlerInterceptorAdapter {
    private static final Map<String, Logger> LOGGER_MAP = new HashMap<>();
    private static final boolean INFO_ENABLED = log.isInfoEnabled();
    private static final String LOGGER_TIME_FLAG = "_START_TIME";
    String params;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (INFO_ENABLED) {
            params = RequestApiUtils.appendParam(request);
            final String apiOperationValue = RequestApiUtils.fetchApiOperationValue(handler);
            //this.getLogger(handler).info("{} [{}] [{}] => [{}] {}", request.getMethod(), IpUtils.getIpAddress(request), apiOperationValue, request.getRequestURL(), RequestApiUtils.fetchParam(request));
            //this.getLogger(handler).info("{} [{}] [{}] => [{}]", request.getMethod(), IpUtils.getIpAddress(request), apiOperationValue, request.getRequestURL()+"?"+params);
            request.setAttribute(LOGGER_TIME_FLAG, System.currentTimeMillis());
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (INFO_ENABLED) {
            try {
                final long e = Long.parseLong(request.getAttribute(LOGGER_TIME_FLAG).toString());
                long time = System.currentTimeMillis() - e;
                int level = getLevel(time);
                this.getLogger(handler).info("LEVEL [{}] Time [{}]ms END [{}] =>[{}]",level,time,IpUtils.getIpAddress(request), request.getRequestURL()+params);
            } catch (NullPointerException var4) {
                this.getLogger(handler).info("END [{}] =>[{}] ", IpUtils.getIpAddress(request), request.getRequestURL()+params);
            }
        }
    }

    private Logger getLogger(Object handler) {
        Logger logger = log;
        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final String methodName = handlerMethod.getBeanType().getName() + "#" + handlerMethod.getMethod().getName();
            logger = LOGGER_MAP.get(methodName);
            if (logger == null) {
                logger = LoggerFactory.getLogger(methodName);
                LOGGER_MAP.put(methodName, logger);
            }
        }
        return logger;
    }

    /**
     * 根据响应时间定义级别
     * @param time
     * @return
     */
    private int getLevel(long time){
        if(0<=time && time<1000)return 0;
        if(1000<time && time<2000)return 1;
        if(2000<time && time<3000)return 2;
        if(3000<time && time<4000)return 3;
        if(4000<time && time<5000)return 4;
        if(5000<time && time<6000)return 5;
        if(6000<time && time<7000)return 6;
        if(7000<time && time<8000)return 7;
        if(8000<time && time<9000)return 8;
        if(9000<time && time<10000)return 9;
        return 10;
    }
}
