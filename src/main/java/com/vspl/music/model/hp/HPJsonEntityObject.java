package com.vspl.music.model.hp;

import java.util.List;

public class HPJsonEntityObject {
	String key;
	String displayName;
	List<HPJsonObject> params;

	public HPJsonEntityObject(String key, String displayName, List<HPJsonObject> params) {
		this.key = key;
		this.displayName = displayName;
		this.params = params;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<HPJsonObject> getParams() {
		return params;
	}

	public void setParams(List<HPJsonObject> params) {
		this.params = params;
	}

}
