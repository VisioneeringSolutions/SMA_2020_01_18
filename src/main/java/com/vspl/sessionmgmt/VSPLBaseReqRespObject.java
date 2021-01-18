package com.vspl.sessionmgmt;

import javax.servlet.http.HttpServletRequest;

public class VSPLBaseReqRespObject {

	public VSPLSessionObject sessionObject;
	public HttpServletRequest servletRequest;
	public String remoteAddress;
	public String deviceType;
	public String methodRequest;
	public String pathInfo;

	public void initSessionObject(VSPLSessionObject sessionObject) {
		this.sessionObject = sessionObject;
		if (sessionObject != null) {
			sessionObject.lastAccessTime = System.currentTimeMillis();
		}
	}

	public void setHttpRequestData(HttpServletRequest httpRequest) {
		this.servletRequest = httpRequest;
		this.remoteAddress = httpRequest.getHeader("X-FORWARDED-FOR");
		if (this.remoteAddress == null) {
			this.remoteAddress = httpRequest.getRemoteAddr();
			this.methodRequest = httpRequest.getMethod();
			this.pathInfo = httpRequest.getPathInfo();
		}
		this.deviceType = (httpRequest.getHeader("User-Agent").toLowerCase().indexOf("android") != -1) ? "ANDROID"
				: "WEB";
	}

	public void closeRequest() {

	}
}
