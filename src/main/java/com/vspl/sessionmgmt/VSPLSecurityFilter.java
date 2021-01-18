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

import com.vspl.music.util.DataUtil;


@WebFilter(filterName = "VSPLSecurityFilter", urlPatterns = { "/rest/ajax/*",
		"/rest/ajaxRegistration/*",
		"/rest/ajaxSalary/*",
		"/rest/ajaxReport/*",
		"/rest/ajaxTimeSlot/*",
		"/rest/ajaxDashboard/*",
		"/rest/ajaxFeedback/*",
		"/rest/ajaxMessage/*",
		"/rest/ajaxInvoice/*"})
public class VSPLSecurityFilter implements Filter {
	public void init(FilterConfig filterConfig) {
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpResonse = (HttpServletResponse) servletResponse;
		HttpSession session = httpRequest.getSession(false);
		if (session == null) {
			if (DataUtil.boolValue(httpRequest.getHeader("CS"))) {
				session = httpRequest.getSession(true);
			} else {
				httpResonse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}
		VSPLSessionObject sessionObject = (VSPLSessionObject) session.getAttribute("sessionObject");
		if (DataUtil.boolValue(httpRequest.getHeader("CS"))) {
			if (sessionObject == null) {
				sessionObject = VSPLSessionObjectFactory.factory().sessionCreated(session, httpRequest);
			}
			sessionObject.isAuthenticated = true;
		} else if (sessionObject == null || !sessionObject.isAuthenticated) {
			httpResonse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		httpResonse.setHeader("Cache-Control", "no-cache, post-check=0, pre-check=0, must-revalidate, no-store");
		httpResonse.setHeader("Pragma", "no-cache");
		httpResonse.setDateHeader("Expires", 0);
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
	}
}
