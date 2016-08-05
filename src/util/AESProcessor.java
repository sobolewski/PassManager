package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import model.ExportContainer;

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

// UNLIMITED JCE POLICY...

public class AESProcessor {

	private static byte[] salt;
	private static int pswdIterations = 65536;
	private static int keySize = 256;
	private static byte[] ivBytes;

	// HIER ALSO der Container mit salt und iv zum entschlüsseln der nachricht
	public void exportDataListContainerToFile(ObservableList<DataEntry> dataList, String masterPassword, File file) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, IOException {


		ExportContainer container = new ExportContainer();
		
		container.setEncryptedData(encrypt(dataList, masterPassword));
		container.setSalt(salt);
		container.setIv(ivBytes);

		ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(file)));
		oos.writeObject(container);
		oos.flush();
		oos.close();

	}

	private byte[] encrypt(ObservableList<DataEntry> dataEntryList, String password) throws NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException,
			IllegalBlockSizeException, BadPaddingException, IOException {
		// get salt
		salt = generateSalt();

		// Derive the key
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, pswdIterations, keySize);
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

		// encrypt the message
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();

		ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();

		//der oos schreibt in den byteOutStream, am ende wird dieser in ein byteArray gewandelt
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(byteOutStream);
		oos.writeObject(new ArrayList<DataEntry>(dataEntryList));
		oos.close();
		byte[] dataEntryBytes = byteOutStream.toByteArray();
			
		//verschlüsseln
		byte[] encryptedTextBytes = cipher.doFinal(dataEntryBytes);
		return encryptedTextBytes;
	}

	
	
	// auslagern nach einer neuen IO klasse?
	public ObservableList<DataEntry> importDataListFromFile(String masterPassword, File file)
			throws FileNotFoundException, IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		ArrayList<DataEntry> dataEntryListArray = new ArrayList<DataEntry>();

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		ExportContainer container = (ExportContainer) ois.readObject();

		byte[] salt = container.getSalt();
		byte[] iv = container.getIv();
		byte[] encData = container.getEncryptedData();
		
		byte[] decriptedData=  decrypt(encData, salt, masterPassword, iv);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(decriptedData);
		ObjectInputStream ois2 = new ObjectInputStream(bais);
		dataEntryListArray = (ArrayList<DataEntry>) ois2.readObject();


		return FXCollections.observableArrayList(dataEntryListArray);
		//return FXCollections.observableArrayList(dataEntryListArray);
		/*
		 * byte[] decryptedBytes = null; decryptedBytes =
		 * cipher.doFinal(encBytes);
		 */
	}

	private byte[] decrypt(byte[] encBytes, byte[] salt, String password, byte[] iv) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, pswdIterations, keySize);

			SecretKey secretKey = factory.generateSecret(spec);
			SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

			// Decrypt the message
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			byte[] decryptedBytes = cipher.doFinal(encBytes);
			return decryptedBytes;

	}

	
	
	private byte[] generateSalt() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		return bytes;
	}
}
