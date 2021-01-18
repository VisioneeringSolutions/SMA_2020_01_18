package com.vspl.music.model.hp;

public class HPMsgObject {
	public String id;
	public String subject;
	public String emailTmplStr;

	@Override
	public String toString() {
		return "MsgObject [id=" + this.id + ", subject=" + this.subject + ", emailTmplStr=" + this.emailTmplStr + "]";
	}

}
