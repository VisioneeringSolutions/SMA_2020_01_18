package com.vspl.music.model.hp;

public class HPJsonObject {
	public String keyName;
	public String dbColumnName;
	public String dataType;
	public String displayName;

	public HPJsonObject(String keyName, String dbColumnName, String dataType, String displayName) {
		super();
		this.keyName = keyName;
		this.dbColumnName = dbColumnName;
		this.dataType = dataType;
		this.displayName = displayName;
	}

}
