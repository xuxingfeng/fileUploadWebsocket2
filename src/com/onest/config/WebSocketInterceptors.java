package com.onest.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by NIU on 2017/6/26.
 */
public class WebSocketInterceptors implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> map) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            String path = httpServletRequest.getServletContext().getRealPath("/")+"/static/upload";
            //发送者编号
//            String fid = httpServletRequest.getParameter("fid").toString();
            //接收者编号
//            String uid = httpServletRequest.getParameter("uid").toString();
            //接收者IP
//            String ip = httpServletRequest.getParameter("ip").toString();
            //接收者端口号
//            String port = httpServletRequest.getParameter("port").toString();
            map.put("path",path);
//            map.put("fid",fid);
//            map.put("uid",uid);
//            map.put("ip",ip);
//            map.put("port",port);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Exception ex) {

    }
}
