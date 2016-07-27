package model;

import java.io.Serializable;

public class ExportContainer implements Serializable{

	private byte[] salt;
	private byte[] iv;
	private byte[] encryptedData;
	
	
	
	public byte[] getSalt() {
		return salt;
	}
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
	public byte[] getIv() {
		return iv;
	}
	public void setIv(byte[] iv) {
		this.iv = iv;
	}
	public byte[] getEncryptedData() {
		return encryptedData;
	}
	public void setEncryptedData(byte[] encryptedData) {
		this.encryptedData = encryptedData;
	}
	
	
	
}
