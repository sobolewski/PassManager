package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Properties;

import model.UserAccount;

public class AppProperties {

	public static void saveProperties(UserAccount ua) {
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			output = new FileOutputStream("config.properties");
			// set the properties value
			//wenn man binärdaten in einen string packen will -> base64 encoding
			
			String pwString = Base64.getEncoder().encodeToString(ua.getPassword());
			String saltString = Base64.getEncoder().encodeToString(ua.getSalt());
			
			prop.setProperty("q9p39gAuj3S439gnfx+O<5kxjbnykbcurrymitschrankerotweiss", pwString); //password
			prop.setProperty("xYga7hGn94nmlfaaLdb.yb1mfbkb-ycurrymitschrankerotweiss", saltString);	//salt

			
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

	public static Properties loadProperties() throws IOException {
		Properties prop = new Properties();
		InputStream input = new FileInputStream("config.properties");
		// load a properties file
		prop.load(input);
		// get the property value and print it out
		// System.out.println(prop.getProperty("password"));
		input.close();
		return prop;
	}
}
