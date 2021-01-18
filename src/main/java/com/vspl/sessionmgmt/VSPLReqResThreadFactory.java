package com.vspl.sessionmgmt;

public class VSPLReqResThreadFactory {

	private volatile static VSPLReqResThreadFactory factory;

	public static VSPLReqResThreadFactory factory() {
		if (factory == null) {
			synchronized (VSPLReqResThreadFactory.class) {
				if (factory == null) {
					factory = new VSPLReqResThreadFactory();
				}
			}
		}
		return factory;
	}

	private VSPLReqResThread reqRespThreadLocal = new VSPLReqResThread();

	public VSPLBaseReqRespObject getReqRespObject() {
		return this.reqRespThreadLocal.get();
	}

	public void removeReqRespObject() {
		this.removeReqRespObject(this.getReqRespObject());
	}

	public void removeReqRespObject(VSPLBaseReqRespObject reqRespObject) {
		try {
			reqRespObject.closeRequest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.reqRespThreadLocal.remove();
	}
}
