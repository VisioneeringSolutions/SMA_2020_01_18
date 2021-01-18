package com.vspl.music.model.hp;

public class HPLogin {
	public String url;
	public String userId;
	public String firstName;
	public String token = "123456789";
	public Long userKey;
	public String role;
	public String id;
	public String address1;
	public String address2;
	public String phone;
	public String imageUrl;

	public HPLogin(String url, String userId) {
		super();
		this.url = url;
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "HPLogin [url=" + url + ", token=" + token + "]";
	}

}
