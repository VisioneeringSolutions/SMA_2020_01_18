package com.vspl.sessionmgmt;

public class VSPLReqResThread extends ThreadLocal<VSPLBaseReqRespObject> {

	@Override
	protected VSPLBaseReqRespObject initialValue() {
		return new VSPLBaseReqRespObject();
	}

	@Override
	public VSPLBaseReqRespObject get() {
		return super.get();
	}
}