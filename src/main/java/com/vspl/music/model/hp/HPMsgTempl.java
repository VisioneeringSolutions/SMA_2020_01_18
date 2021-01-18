package com.vspl.music.model.hp;

import java.util.ArrayList;
import java.util.HashMap;

public class HPMsgTempl {
	public ArrayList<HPMsgObject> msgObjArray;

	public HashMap<String, HPMsgObject> msdIdObjectMap;

	public HashMap<String, HPMsgObject> msdIdObjectMap() {
		if (this.msdIdObjectMap == null) {
			this.msdIdObjectMap = new HashMap<>();
			for (HPMsgObject msgObj : this.msgObjArray) {
				this.msdIdObjectMap.put(msgObj.id, msgObj);
			}
		}
		return this.msdIdObjectMap;
	}

	@Override
	public String toString() {
		return "MsgTempl [msgObjArray=" + this.msgObjArray + ", msdIdObjectMap=" + this.msdIdObjectMap + "]";
	}

}