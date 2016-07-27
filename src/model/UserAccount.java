package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAccount implements Serializable{

	private static String username;
	private static String  password;
	private List<DataEntry> dataBase;
	
	public String testString;
	

	public UserAccount(){
	}
	
	public UserAccount(String username, String password){
		UserAccount.username = username;
		UserAccount.password = password;
	}
	
	public UserAccount(String username, String password, List<DataEntry> listOfDataEntries) {
		UserAccount.username = username;
		UserAccount. password = password;
		this.dataBase = listOfDataEntries;
	}
	public static String getUsername() {
		return username;
	}
	public static void setUsername(String username) {
		UserAccount.username = username;
	}
	public static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		UserAccount.password = password;
	}


	
	
}
