package com.vspl.sessionmgmt;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class VSPLSessionListener implements HttpSessionListener {

	public VSPLSessionListener() {
	}

	public void sessionCreated(HttpSessionEvent sessionEvent) {
	}

	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		VSPLSessionObjectFactory.factory().sessionDestroyed(sessionEvent.getSession().getId(), true);
	}
}
