package util;

import java.security.SecureRandom;

public class RandomPasswordGenerator {
	
	public static String generateRandomPW(int numberOfSigns){
		
				char randomPW[] = new char[numberOfSigns];
				SecureRandom prngPW = new SecureRandom();
				String alphabet = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&'()*+,-.:;<=>?@[]\\^_`{|}~";
			    for (int i = 0; i < numberOfSigns; i++) {
			    	randomPW[i] = alphabet.charAt(prngPW.nextInt(alphabet.length()));
			    }
		//wenn ich hier return randomPW.toString(); schreiben würde wäre das nur die interne hash-repräsentation des char-array (C@24j1)
		return new String(randomPW);
		
	}

	
}
