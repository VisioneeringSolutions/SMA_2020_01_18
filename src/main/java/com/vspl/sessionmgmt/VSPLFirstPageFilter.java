package com.vspl.sessionmgmt;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "VSPLFirstPageFilter", urlPatterns = {"/rest/auth/*","/rest/upload/*"})
public class VSPLFirstPageFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResonse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession(false);
		if (session == null) {
			session = httpRequest.getSession(true);
		}
		VSPLSessionObject sessionObject = (VSPLSessionObject) session.getAttribute("sessionObject");
		if (sessionObject == null) {
			sessionObject = VSPLSessionObjectFactory.factory().sessionCreated(session, httpRequest);
		}
		VSPLReqResThreadFactory.factory().getReqRespObject().initSessionObject(sessionObject);
		// pages given in url-pattern will not be stored in browser's cache.
		httpResonse.setHeader("Cache-Control", "no-cache, post-check=0, pre-check=0, must-revalidate, no-store");
		httpResonse.setHeader("Pragma", "no-cache");
		httpResonse.setDateHeader("Expires", 0);
		filterChain.doFilter(httpRequest, httpResonse);
	}

	@Override
	public void destroy() {
	}

}
