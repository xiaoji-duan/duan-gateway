package com.xiaoji.duan.zuulserver.filter;

import javax.servlet.http.HttpServletRequest;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class WebSocketFilter extends ZuulFilter {
	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext context = RequestContext.getCurrentContext();
		HttpServletRequest request = context.getRequest();
		String upgradeHeader = request.getHeader("Upgrade");
		if (null == upgradeHeader) {
			upgradeHeader = request.getHeader("upgrade");
		}
		if (null != upgradeHeader && "websocket".equalsIgnoreCase(upgradeHeader)) {
			context.addZuulRequestHeader("connection", "Upgrade");
			System.out.println("ws -> " + request.getRequestURI());
		}
		return null;
	}
}