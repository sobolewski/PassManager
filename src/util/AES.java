package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.DataEntry;

/**
 * 
 * Do not use ECB mode! There are several modes of operation for block ciphers:
 * ECB (Electronic CodeBook), CBC (Cipher Block Chaining), CFB (Cipher
 * FeedBack), OFB (Output FeedBack) and CTR (Counter). In some packages, the
 * default mode is ECB. It is not secure. Always use CBC (Cipher Block Chaining)
 * mode. This requires you to provide a separate Initialization Vector (IV)
 * which must be unique for every message you ever send using the same key.
 * 
 * A password is not a key! A password is a text string of variable length.
 * Encryption algorithms require the key as a fixed-length bit-string. You must
 * convert the password text string to a bit-string. Use the Password-Based Key
 * Derivation Function algorithm known as PBKDF2 to do this. This method
 * requires a random salt and an iteration count as well as the password text.
 * For convenience, you can use the same random value for the salt as for the
 * IV. Just make sure that the sender and recipient agree on this convention.
 * 
 * Ciphertext is not text! Ciphertext is a bit string that should not be stored
 * in a "string" type. Encode the ciphertext in hexadecimal or base64, which can
 * be safely stored and transmitted as a string type. Use a unique IV each time!
 * Always create a fresh, randomly-generated IV/salt each and every time you
 * encrypt a new message to send. Never give your users the opportunity to
 * re-use an old one or avoid the automatic re-generation process. Algorithms
 */

// AES symmetrical encription-decription

//UNLIMITED JCE POLICY... 

public class AES {
	//private static final String password = "test";
	private static String salt;
	private static int pswdIterations = 65536;
	private static int keySize = 256;
	private byte[] ivBytes;

	
	
	public void encryptDataListToFile(ObservableList<DataEntry> dataList, String masterPassword, File file) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {

		salt = generateSalt();
		byte[] saltBytes = salt.getBytes("UTF-8");

		// Derive the key
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), saltBytes, pswdIterations, keySize);
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

		// encrypt the message
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();
		ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(file)), cipher));
			oos.writeObject(new ArrayList<DataEntry>(dataList));
			oos.flush();
			oos.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
	}
	


	public ObservableList<DataEntry> decryptDataListFromFile(String masterPassword, File file) {
		ArrayList<DataEntry> dataEntryListArray = new ArrayList<DataEntry>();
		// ACHTUNG ich muss das salz und den IV noch mit im FILE speichern!!!
		

		// Derive the key
		try {
			byte[] saltBytes = salt.getBytes("UTF-8");
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), saltBytes, pswdIterations, keySize);

			SecretKey secretKey = factory.generateSecret(spec);
			SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

			// Decrypt the message
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
			
			ObjectInputStream ois = new ObjectInputStream(new CipherInputStream(new BufferedInputStream(new FileInputStream(file)), cipher));
			dataEntryListArray = (ArrayList<DataEntry>) ois.readObject();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return FXCollections.observableArrayList(dataEntryListArray);	
		/*
		byte[] decryptedBytes = null;
		decryptedBytes = cipher.doFinal(encBytes);
		*/
	}

	public String generateSalt() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		String s = new String(bytes);
		return s;
	}
}
