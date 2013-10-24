package com.example;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user-details")
public class User {

	private int id;
	private String username;
	private String fullname;

	public User() {}

	public User(int id, String username, String fullname) {
		super();
		this.id = id;
		this.username = username;
		this.fullname = fullname;
	}

	@XmlElement(name = "id")
	public int getId() {
		return id;
	}
	
	public void setId(int id){
		this.id=id;
	}

	@XmlElement(name = "user")
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@XmlElement(name = "fullName")
	public String getFullname() {
		return fullname;
	}
	
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	@Override
	public String toString() {
		return String.format("Details " + "[id=%s, user=%s, fullName=%s]",
				id, username, fullname);
	}

}