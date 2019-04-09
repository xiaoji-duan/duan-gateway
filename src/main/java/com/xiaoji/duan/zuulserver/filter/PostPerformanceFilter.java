package com.xiaoji.duan.zuulserver.filter;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xiaoji.duan.zuulserver.service.MessageQueueService;

public class PostPerformanceFilter extends ZuulFilter {

	@Autowired
    private MessageQueueService mqservice;
    
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
        Map<String, Object> start = (LinkedHashMap) ctx.get("performance_request");
        Map<String, Object> end = new LinkedHashMap();
        
        Object zuulResponse = ctx.getCurrentContext().get("zuulResponse");
        if (zuulResponse != null) {
        	HttpResponse resp = (HttpResponse) zuulResponse;

            Header[] headers = resp.getAllHeaders();
            
            Map<String, String> header = new LinkedHashMap();
            for (Header headerentry : headers) {
            	String headername = headerentry.getName();
            	
            	String headervalue = headerentry.getValue();
            	
            	if (StringUtils.isEmpty(headername) || StringUtils.isEmpty(headervalue)) {
            		continue;
            	}

            	header.put(headername, headervalue);
            }

            end.put("headers", header);
        }
        
        Long starttime = (Long) start.get("timestamp");
        String prefix = (String) start.get("prefix");
        if ("/".equals(prefix)) {
        	prefix = "aac";
        }
        
        long costs = System.currentTimeMillis() - starttime;
        
        Map<String, Object> performance = new LinkedHashMap();
        performance.put("start", start);
        performance.put("end", end);
        
        mqservice.performance(prefix, starttime, costs, performance);
		return null;
	}

	@Override
	public String filterType() {
        return FilterConstants.POST_TYPE;
	}

	@Override
	public int filterOrder() {
		return 0;
	}

}
