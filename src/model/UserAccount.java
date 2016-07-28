package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAccount implements Serializable{

	private static String username;
	private static byte[]  password; // PBKDF2 gehashed
	private static byte[] salt;
	private List<DataEntry> dataBase;
	

	public UserAccount(){
	}
	
	public UserAccount(String username, byte[] password, byte[] salt){
		UserAccount.username = username;
		UserAccount.password = password;
		UserAccount.salt = salt;
	}
	
	public UserAccount(String username, byte[] password, List<DataEntry> listOfDataEntries) {
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
	public static byte[] getPassword() {
		return password;
	}
	public static void setPassword(byte[] password) {
		UserAccount.password = password;
	}
	public static byte[] getSalt() {
		return salt;
	}
	public static void setSalt(byte[] salt) {
		UserAccount.salt = salt;
	}


	
	
}
