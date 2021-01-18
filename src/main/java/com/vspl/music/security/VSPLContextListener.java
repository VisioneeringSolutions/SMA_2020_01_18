package com.vspl.music.security;

import java.text.ParseException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class VSPLContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		try {
			VSPLApplicationObject.factory().startApplication();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		VSPLApplicationObject.factory().closeApplication();
	}
}
