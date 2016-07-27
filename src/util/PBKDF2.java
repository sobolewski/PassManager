package util;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/* https://www.owasp.org/index.php/Hashing_Java
Remember to save the salt with the hashed password!
Remember to save the value of iterations with the hashed password!
...
A keyLength of 256 is safe */

//das ist Hashing um das MasterPW möglichst sicher zu speichern
public class PBKDF2 {

	private static final int ITERATIONS = 100000;
	private static final int KEY_LENGTH = 512; // bits
	//93 zeichen
	private static final String ALPHABET = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&'()*+,-.:;<=>?@[]\\^_`{|}~";
	
	
	public static String generatePasswordFromBytes(byte[] hashedBytes, int passwordLength) {

		
		BigInteger alphabetLen = BigInteger.valueOf(ALPHABET.length());
		String pw = "";
		BigInteger number = new BigInteger(hashedBytes);
		for(int i = 0; i<passwordLength; i++){
			
			pw = pw + ALPHABET.charAt(number.mod(alphabetLen).intValue());
			number = number.divide(alphabetLen);
		}
		return pw;
		
		
	}

	public static byte[] hashedBytesPBKDF2(String domain, String username, String masterPassword, byte[] saltBytes)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		String domainspecificPasswordInput = domain+username+masterPassword;
		char[] passwordChars = domainspecificPasswordInput.toCharArray();
		//byte[] saltBytes = salt.getBytes();

		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_LENGTH);
		SecretKey keyFactory = secretKeyFactory.generateSecret(spec);
		byte[] hashedPassword = keyFactory.getEncoded();
		return hashedPassword;

		// return String.format("%x", new BigInteger(hashedPassword));

	}

	public static byte[] getSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}

}
