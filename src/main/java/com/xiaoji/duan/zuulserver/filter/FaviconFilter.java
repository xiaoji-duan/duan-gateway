package com.xiaoji.duan.zuulserver.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class FaviconFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();

        System.out.println(ctx.getRequest().getRequestURI().endsWith("favicon.ico")
				&& !ctx.getRequest().getRequestURI().startsWith("/aaf/")
        		&& (ctx.getRequest().getRequestURI().indexOf('/', 1) > 0 ? ctx.getRequest().getRequestURI().substring(1, ctx.getRequest().getRequestURI().indexOf('/', 1)).length() == 3 : true));
        return ctx.getRequest().getRequestURI().endsWith("favicon.ico")
				&& !ctx.getRequest().getRequestURI().startsWith("/aaf/")
        		&& (ctx.getRequest().getRequestURI().indexOf('/', 1) > 0 ? ctx.getRequest().getRequestURI().substring(1, ctx.getRequest().getRequestURI().indexOf('/', 1)).length() == 3 : true);
	}

	@Override
	public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        String host = ctx.getRequest().getServerName();
        String requestURI = ctx.getRequest().getRequestURI();
        String prefix = "vzz";
		System.out.println(requestURI);
		String referer = ctx.getRequest().getHeader("Referer");
		System.out.println(referer);

		if (referer != null && !StringUtils.isEmpty(referer)) {
			try {
				requestURI = new URL(referer).getPath();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
        if (requestURI.indexOf('/', 1) > 3)
        	prefix = requestURI.substring(1, requestURI.indexOf('/', 1));

        String redirect = ctx.getRequest().getScheme() + "://sa-aaf:80/aaf";

        URL redirectURL = null;
		InputStream is = null;
		try {
			redirectURL = new URL(redirect + "/" + prefix + "/favicon.ico");
			HttpURLConnection conn = (HttpURLConnection) redirectURL.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(false);
	        conn.setRequestProperty("Content-type", ctx.getRequest().getContentType());
	        conn.setRequestMethod(ctx.getRequest().getMethod());
			is = conn.getInputStream();
			HttpServletResponse response = ctx.getResponse();
			response.setContentLength(is.available());
			OutputStream output = response.getOutputStream();
			byte[] buffer = new byte[10240];

			for (int length = 0; (length = is.read(buffer)) > 0;) {
			    output.write(buffer, 0, length);
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ctx.setRouteHost(null);
		System.out.println(redirect + "/" + prefix + "/favicon.ico");
		return null;
	}

	@Override
	public String filterType() {
        return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return 0;
	}

}
