package com.vspl.sessionmgmt;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebListener
public class VSPLRequestListener implements ServletRequestListener {

	@Override
	public void requestInitialized(ServletRequestEvent requestEvent) {
		HttpServletRequest request = ((HttpServletRequest) requestEvent.getServletRequest());
		if (this.doCreateReqResOBject(request)) {
			VSPLBaseReqRespObject reqRespObject = VSPLReqResThreadFactory.factory().getReqRespObject();
			reqRespObject.setHttpRequestData(request);
			HttpSession session = request.getSession(false);
			if (session != null) {
				VSPLSessionObject sessionObject = (VSPLSessionObject) session.getAttribute("sessionObject");
				reqRespObject.initSessionObject(sessionObject);
			}
		}
	}

	private boolean doCreateReqResOBject(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		if (requestURI.length() != 0) {
			if (requestURI.contains("/rest/")) {
				request.setAttribute("isReqResObjCreated", true);
				return true;
			}
		}
		request.setAttribute("isReqResObjCreated", false);
		return false;
	}

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		// TODO Auto-generated method stub

	}
}
