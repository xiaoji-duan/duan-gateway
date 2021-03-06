package com.xiaoji.duan.zuulserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.xiaoji.duan.zuulserver.filter.AccessFilter;
import com.xiaoji.duan.zuulserver.filter.AccessGrantFilter;
import com.xiaoji.duan.zuulserver.filter.AuthorizeFilter;
import com.xiaoji.duan.zuulserver.filter.FaviconFilter;
import com.xiaoji.duan.zuulserver.filter.PostPerformanceFilter;
import com.xiaoji.duan.zuulserver.filter.PrePerformanceFilter;
import com.xiaoji.duan.zuulserver.filter.WebSocketFilter;

@SpringBootApplication
@EnableZuulProxy
public class ZuulServerApplication extends SpringBootServletInitializer {

    public static void main(String[] args){
        SpringApplication.run(ZuulServerApplication.class,args);
    }

    @Bean
    public AccessFilter accessFilterFilter(){
        return new AccessFilter();
    }
    
    @Bean
    public AuthorizeFilter authorizeFilterFilter() {
    	return new AuthorizeFilter();
    }
    
    @Bean
    public AccessGrantFilter accessGrantFilterFilter() {
    	return new AccessGrantFilter();
    }
    
    @Bean
    public WebSocketFilter accessWebSocketFilter() {
    	return new WebSocketFilter();
    }
    
//    @Bean
//    public PrePerformanceFilter prePerformanceFilter() {
//    	return new PrePerformanceFilter();
//    }
//    
//    @Bean
//    public PostPerformanceFilter postPerformanceFilter() {
//    	return new PostPerformanceFilter();
//    }
    
    @Bean
    public FaviconFilter faviconFilter() {
    	return new FaviconFilter();
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    	return builder.sources(ZuulServerApplication.class);
    }
}
