package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import model.UserAccount;

public class AppProperties {
	
	public static void saveProperties(UserAccount ua){
		Properties prop = new Properties();
		OutputStream output = null;

		try {

			output = new FileOutputStream("config.properties");

			// set the properties value
			prop.setProperty("password", ua.getPassword());
//			prop.setProperty("dbuser", "mkyong");
//			prop.setProperty("dbpassword", "password");

			// save properties to project root folder
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	
//	public static Properties loadProperties(){
//		Properties prop = new Properties();
//		InputStream input = null;
//
//		try {
//
//			input = new FileInputStream("config.properties");
//
//			// load a properties file
//			prop.load(input);
//
//			// get the property value and print it out
//			System.out.println(prop.getProperty("password"));
////			System.out.println(prop.getProperty("dbpassword"));
//			
//
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		} finally {
//			if (input != null) {
//				try {
//					input.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			
//		}
//		return prop;
//
//	  }
	
	
	
	
	
	public static Properties loadProperties() throws IOException{
		Properties prop = new Properties();
		InputStream input = null;

		input = new FileInputStream("config.properties");

			// load a properties file
		prop.load(input);

			// get the property value and print it out
		//System.out.println(prop.getProperty("password"));
//			System.out.println(prop.getProperty("dbpassword"));
			
		input.close();

		return prop;

	  }
}

	

