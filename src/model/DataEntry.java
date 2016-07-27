package model;

import java.io.Serializable;

public class DataEntry implements Serializable{
	private String url;
	private String username;
	private String password;
	
	public DataEntry(String url, String username, String password){
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	

	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String passowrd) {
		this.password = passowrd;
	}
	
	
	 
}
