package com.onest.config;

import com.onest.service.FileChatHandlers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 *
 * Created by NIU on 2017/6/26.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

    /**
     * 配置websocket入口，允许访问域、socket注册Handler、sockjs支持和拦截器
     * */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //允许连接的域,只能以http或https开头，不限制时使用“*”号
        String[] allowsOrigins = { "*" };

        //websocket通道
        //1.registry.addHandler注册和路由的功能，当客户端发起websocket连接，把/path交给对应的handler处理，而不实现具体的业务逻辑，可以理解为收集和任务分发中心。
        //2.允许指定的域名或IP(含端口号)建立长连接。
        //3.addInterceptors，顾名思义就是为handler添加拦截器，可以在调用handler前后加入我们自己的逻辑代码。
        //registry.addHandler(textChatHandlers(), "/im").setAllowedOrigins(allowsOrigins).addInterceptors(getInterceptors());
        //registry.addHandler(textChatHandlers(), "/sockjs/im").setAllowedOrigins(allowsOrigins).addInterceptors(getInterceptors()).withSockJS();
        registry.addHandler(fileChatHandlers(), "/websocket/hh").setAllowedOrigins(allowsOrigins);
    }
    @Bean
    public WebSocketInterceptors getInterceptors(){
        return new WebSocketInterceptors();
    }
    @Bean
    public FileChatHandlers fileChatHandlers(){
        return new FileChatHandlers();
    }
}
