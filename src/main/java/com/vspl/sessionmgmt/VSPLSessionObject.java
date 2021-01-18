package com.vspl.sessionmgmt;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

public class VSPLSessionObject {

	public static int sessionNumberGen = 0;
	public static int activeSessionNumber = 0;

	public int sessionNumber;
	public int transcNumber;
	public Object uniqueID;
	public boolean isAuthenticated;
	public boolean isAltaSupportUserAuthenticated;
	public String sessionUser;

	public long loggedInTime;
	public long lastAccessTime;
	public String remoteAddress;
	public String remoteHost;
	public String remoteString;
	public HttpSession session;
	public String redirectPath;
	public boolean isCookieBasedLogin;
	public boolean isUiBasedLogin;
	public HashMap<String, Long> cacheKeyToSend = new HashMap<>();

	public VSPLSessionObject() {
		this.sessionNumber = ++sessionNumberGen;
		++activeSessionNumber;
		this.cacheKeyToSend = new HashMap<>();
		this.loggedInTime = System.currentTimeMillis();
	}

	public void sessionDestroyed() {
		try {
			if (this.session != null) {
				this.session.removeAttribute("sessionObject");
				this.session.invalidate();
			}
		} catch (IllegalStateException ignored) {
		}
		--activeSessionNumber;
		if (this.sessionUser != null && this.sessionUser.length() != 0) {
			System.out.println("Session User: " + this.sessionUser);
		}
		System.out.println("Session with ID :" + this.uniqueID + " destroyed in "
				+ ((System.currentTimeMillis() - this.loggedInTime) / 1000) + " seconds");
	}

	public Object getSessionAttribute(String attributeName) {
		return this.session.getAttribute(attributeName);
	}

	public void setAuthenticatedUser(String user) {
		this.sessionUser = user;
		this.isAuthenticated = true;
	}

	@Override
	public String toString() {
		return "VSPLSessionObject [sessionNumber=" + this.sessionNumber + ", uniqueID=" + this.uniqueID
				+ ", isAuthenticated=" + this.isAuthenticated + ", sessionUser=" + this.sessionUser + ", loggedInTime="
				+ this.loggedInTime + ", session=" + this.session + "]";
	}

}
