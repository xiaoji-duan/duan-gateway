package com.xiaoji.duan.zuulserver.filter;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class PrePerformanceFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String upgradeHeader = ctx.getRequest().getHeader("Upgrade");
		if (null == upgradeHeader) {
			upgradeHeader = ctx.getRequest().getHeader("upgrade");
		}
        return !ctx.getRequest().getRequestURI().endsWith(".js")
    			&& !(null != upgradeHeader && "websocket".equalsIgnoreCase(upgradeHeader))
        		&& !ctx.getRequest().getRequestURI().endsWith("/shortapplication.json")
        		&& !ctx.getRequest().getRequestURI().endsWith(".ico")
        		&& !ctx.getRequest().getRequestURI().endsWith(".png")
        		&& !ctx.getRequest().getRequestURI().endsWith(".jpg")
        		&& !ctx.getRequest().getRequestURI().endsWith(".jpeg")
        		&& !ctx.getRequest().getRequestURI().endsWith(".gif")
        		&& !ctx.getRequest().getRequestURI().endsWith(".css")
        		&& !ctx.getRequest().getRequestURI().endsWith(".woff2")
        		&& !ctx.getRequest().getRequestURI().endsWith(".woff")
        		&& !ctx.getRequest().getRequestURI().endsWith(".ttf")
        		&& !ctx.getRequest().getRequestURI().endsWith(".map")
				&& (ctx.getRequest().getRequestURI().indexOf('/', 1) > 0 ? ctx.getRequest().getRequestURI().substring(1, ctx.getRequest().getRequestURI().indexOf('/', 1)).length() == 3 : true);
	}

	@Override
	public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        String host = ctx.getRequest().getServerName();
        String requestURI = ctx.getRequest().getRequestURI();
        String prefix = "/";

        if (requestURI.indexOf('/', 1) > 3)
        	prefix = requestURI.substring(1, requestURI.indexOf('/', 1));

        Enumeration<String> headers = ctx.getRequest().getHeaderNames();
        
        Map<String, String> header = new LinkedHashMap();
        while (headers.hasMoreElements()) {
        	String headername = headers.nextElement();
        	
        	String headervalue = ctx.getRequest().getHeader(headername);
        	
        	if (StringUtils.isEmpty(headername) || StringUtils.isEmpty(headervalue)) {
        		continue;
        	}
        	
        	header.put(headername, headervalue);
        }
        
        Map<String, Object> start = new LinkedHashMap();
        start.put("timestamp", System.currentTimeMillis());
        start.put("host", host == null ? "" : host);
        start.put("uri", requestURI == null ? "/" : requestURI);
        start.put("prefix", prefix == null ? "/" : prefix);
        start.put("headers", header);
        
        ctx.set("performance_request", start);
        
		return null;
	}

	@Override
	public String filterType() {
        return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
