package com.vspl.sessionmgmt;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class VSPLSessionObjectFactory {

	private volatile static VSPLSessionObjectFactory factory;

	public static VSPLSessionObjectFactory factory() {
		if (factory == null) {
			System.out.println("INSIDE VSPLSessionObjectFactory");
			synchronized (VSPLSessionObjectFactory.class) {
				if (factory == null) {
					factory = new VSPLSessionObjectFactory();
				}
			}
		}
		return factory;
	}

	ConcurrentHashMap<Object, VSPLSessionObject> sessionObjectMap = new ConcurrentHashMap<Object, VSPLSessionObject>();

	// Create sessionObject here and put that in map - a new SessionObject is
	// created here as this is a new session
	public VSPLSessionObject sessionCreated(HttpSession session, HttpServletRequest httpRequest) {
		VSPLSessionObject sessionObject = new VSPLSessionObject();
		sessionObject.session = session;
		sessionObject.uniqueID = session.getId();
		sessionObject.remoteAddress = httpRequest.getHeader("X-FORWARDED-FOR");
		if (sessionObject.remoteAddress == null) {
			sessionObject.remoteAddress = httpRequest.getRemoteAddr();
		}
		this.sessionObjectMap.put(sessionObject.uniqueID, sessionObject);
		// requestInitialized(sessionObject.uniqueID);
		session.setAttribute("sessionObject", sessionObject);
		return sessionObject;
	}

	public VSPLSessionObject sessionForID(Object uniqueID) {
		return this.sessionObjectMap.get(uniqueID);
	}

	public void sessionDestroyed(Object uniqueID, boolean isTimeOut) {
		this.sessionDestroyed(this.sessionObjectMap.get(uniqueID));
	}

	public void sessionDestroyed(Object uniqueID) {
		this.sessionDestroyed(uniqueID, false);
	}

	public void sessionDestroyed(VSPLSessionObject sessionObject) {
		if (sessionObject != null) {
			this.sessionObjectMap.remove(sessionObject.uniqueID);
			sessionObject.sessionDestroyed();
		}
	}

	@Override
	public String toString() {
		return "VSPLSessionObjectFactory [sessionObjectMap=" + this.sessionObjectMap + "]";
	}

}
