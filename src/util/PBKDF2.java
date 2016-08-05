package util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PBKDF2 {

	private static final int ITERATIONS = 100000;
	private static final int KEY_LENGTH = 512; // bits
	private static final String ALPHABET = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&'()*+,-.:;<=>?@[]\\^_`{|}~"; // 93Zeichen
	private static final String salt = "=?93g)s<sXd:DSa";
	
																																			 

	/**
	 * Mit dieser Methode wird das Master-Password mit der Domain und dem
	 * User-name zu einem String verkettet, um daraus ein zu der domain und dem
	 * username zugehöriges PW abzuleiten. Dafür wird PBKDF2 auf Grundlage des
	 * SHA512-Hashings verwendet. Das Salt wird dabei statisch vorgegeben, denn
	 * Ziel ist es ein für die Kombination aus MasterPW + domain + username
	 * eindeutigen und immer wieder reproduzierbaren Hashwert zu generieren.
	 * 
	 * Grundidee dahinter ist, dass damit lediglich domain und username als
	 * daten gespeichert werden müssen und wenn der benutzer dass dazugehörige
	 * PW abfragen möchte einfach die methode mit den genannten argumenten
	 * aufgerufen wird und der eindeutige hashwert (da das salz fix vorgegeben
	 * ist) zurückgegeben wird. Das ist sicher, da durch anwedung von PBKDF2 aus
	 * dem resultierendem hash nicht auf die eingabe-argumente zurückgeschlossen
	 * werden kann.
	 * 
	 * Die verwendete Hash-Funktion SHA512 liefert jedoch einen 512 byte langen
	 * Hashwert zurück. Dies eignet sich natürlich nicht als Eingabe bei
	 * Webdiesten oder Ähnlichem. Aus diesem Grund wird die
	 * generatePasswordFromBytes Methode verwendet, um aus den 512 bytes ein
	 * lesbares password mit einer selbst festlegbaren Länge zu erstellen.
	 * 
	 * @param domain
	 * @param username
	 * @param masterPassword
	 * @param saltBytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] hashedBytesPBKDF2(String domain, String username, String masterPassword)
			throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {

		byte[] saltBytes = salt.getBytes("UTF-8");

		String domainspecificPasswordInput = domain + username + masterPassword;
		char[] passwordChars = domainspecificPasswordInput.toCharArray();
		// byte[] saltBytes = salt.getBytes();

		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_LENGTH);
		SecretKey keyFactory = secretKeyFactory.generateSecret(spec);
		byte[] hashedPassword = keyFactory.getEncoded();
		return hashedPassword;

		// return String.format("%x", new BigInteger(hashedPassword));
	}
	
	public static byte[] hashedBytesPBKDF2StaticSalt(String domain, String username, String masterPassword)
			throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {

		byte[] saltBytes = "loi8sa7gl2fsz".getBytes("UTF-8");

		String domainspecificPasswordInput = domain + username + masterPassword;
		char[] passwordChars = domainspecificPasswordInput.toCharArray();
		// byte[] saltBytes = salt.getBytes();

		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_LENGTH);
		SecretKey keyFactory = secretKeyFactory.generateSecret(spec);
		byte[] hashedPassword = keyFactory.getEncoded();
		return hashedPassword;

		// return String.format("%x", new BigInteger(hashedPassword));
	}
	
	
	public byte[] hashMasterPasswordPBKDF2(String masterPassword, byte[] randomSalt) throws NoSuchAlgorithmException, InvalidKeySpecException{

		char[] passwordChars = masterPassword.toCharArray();
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(passwordChars, randomSalt, ITERATIONS, KEY_LENGTH);
		SecretKey keyFactory = secretKeyFactory.generateSecret(spec);
		byte[] hashedPassword = keyFactory.getEncoded();
		return hashedPassword;
	}
	
	
	

	/**
	 * Die Methode verarbeitet einen ByteArray zu einem String von fetlegbarer
	 * Länge auf Basis eines Alphabets, welches alle ASCII Zeichen umfasst.
	 * 
	 * Funktionsweise: Die Länge des Alphabetes umfasst 93 Zeichen. Der
	 * ByteArray wird als extrem sehr großer Integer-Wert interpretiert. In der
	 * for schleife baut man Zeichen für Zeichen das resultierenden PW-String
	 * auf (so lange das resultierende PW werden soll). Das geschieht, indem man
	 * den BigInt-wert des byteArray-argumentes Modulo der alphabetlänge rechnet
	 * (also 999 quadriliarden/93). Das ergibt wieder einen sehr großen int wert
	 * und - hierbei nun interessant - einen rest zwischen 0 und 92 welchen man
	 * als Auswahlpointer für ein Zeichen aus dem Alphabet werwendet.
	 * Damit nicht jedes Mal dasselbe Zeichen gezogen wird setzt man den
	 * bigHashNumber nach jeder Berechnung auf den neuen wert der sich aus der
	 * division bigHashNumber/alphabetlänge (=93 hier) ergibt.
	 * 
	 * @param hashedBytes
	 * @param passwordLength
	 * @return
	 */

	public static String generatePasswordFromBytes(byte[] hashedBytes, int passwordLength) {

		BigInteger alphabetLen = BigInteger.valueOf(ALPHABET.length());
		String pw = "";
		BigInteger bigHashNumber = new BigInteger(hashedBytes);
		for (int i = 0; i < passwordLength; i++) {

			pw = pw + ALPHABET.charAt(bigHashNumber.mod(alphabetLen).intValue());
			bigHashNumber = bigHashNumber.divide(alphabetLen);
		}
		return pw;
	}
	
	
	
}
