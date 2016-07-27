package util;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

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

public class AES2old {
	private static final String password = "test";
	private static String salt;
	private static int pswdIterations = 65536;
	private static int keySize = 256;
	private byte[] ivBytes;

	
	
	public byte[] encrypt(String plainText) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		// get salt
		salt = generateSalt();
		byte[] saltBytes = salt.getBytes("UTF-8");

		// Derive the key
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, pswdIterations, keySize);
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

		// encrypt the message
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();
		ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
		byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
		return encryptedTextBytes;
	}
	
	
	
	
	public String encryptStr(String plainText) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		// get salt
		salt = generateSalt();
		byte[] saltBytes = salt.getBytes("UTF-8");

		// Derive the key
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, pswdIterations, keySize);
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

		// encrypt the message
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();
		ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
		byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
		return new String(encryptedTextBytes, "UTF-8");
	}

	public byte[] decrypt(byte[] encryptedBytes) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{

		byte[] saltBytes = salt.getBytes("UTF-8");
		//byte[] encryptedTextBytes = new Base64().decodeBase64(encryptedText); brauche ich das???
		byte[] encBytes = encryptedBytes;

		// Derive the key
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, pswdIterations, keySize);

		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

		// Decrypt the message
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));

		byte[] decryptedBytes = null;
		decryptedBytes = cipher.doFinal(encBytes);
	
		return decryptedBytes;
	}

	public String generateSalt() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		String s = new String(bytes);
		return s;
	}
}
