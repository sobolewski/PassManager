package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import model.UserAccount;
import util.AppProperties;
import util.PBKDF2;

/** Controls the login screen */
public class LoginController {
	@FXML
	private TextField userName;
	@FXML
	private PasswordField passwordField1, passwordField2;
	@FXML
	private Button btnEnter, btnSetMasterpassword;
	@FXML
	private Label labelMessage;

	private Main main;

	public void setMain(Main main) {
		this.main = main;
	}

	public void initialize() {
	}

	public void handleEnterBtn() {
		Properties prop = new Properties();
		try {
			prop = AppProperties.loadProperties();
		} catch (FileNotFoundException e) {
			// AppProperties.saveProperties(new UserAccount("",""));
			labelMessage.setText("Bitte ein Masterpassword festlegen!");
			labelMessage.setTextFill(Color.rgb(21, 117, 84));
			passwordField2.setVisible(true);
			btnEnter.setVisible(false);
			btnSetMasterpassword.setVisible(true);

			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		PBKDF2 pbkdf2 = new PBKDF2();
		byte[] saltBytes = Base64.getDecoder().decode(prop.getProperty("xYga7hGn94nmlfaaLdb.yb1mfbkb-ycurrymitschrankerotweiss"));
		byte[] pwBytes = Base64.getDecoder().decode(prop.getProperty("q9p39gAuj3S439gnfx+O<5kxjbnykbcurrymitschrankerotweiss"));
		try {
			byte[] pwHash = pbkdf2. hashMasterPasswordPBKDF2(passwordField1.getText(), saltBytes);
			if (Arrays.equals(pwBytes, pwHash)) {
				main.mainWindow();
			} else {
				labelMessage.setText("Das Password ist falsch");
				labelMessage.setTextFill(Color.rgb(21, 117, 84));
				return;
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setMasterpassword() {
		if (passwordField1.getText().length() >= 3) {

			if (passwordField1.getText().equals(passwordField2.getText())) {

				SecureRandom random = new SecureRandom();
				byte salt[] = new byte[20];
				random.nextBytes(salt);

				PBKDF2 pbkdf2 = new PBKDF2();
				try {
					byte[] hashedPw = pbkdf2.hashMasterPasswordPBKDF2(passwordField1.getText(), salt);
					AppProperties.saveProperties(new UserAccount("", hashedPw, salt));
					main.mainWindow();
					
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			} else {
				Alert infoAlert = new Alert(AlertType.ERROR);
				infoAlert.setHeaderText("Stimmt nicht überein!");
				infoAlert.setTitle("Error");
				infoAlert.setContentText("Das password stimmt nicht überein");
				infoAlert.showAndWait();
			}
		} else {
			Alert infoAlert = new Alert(AlertType.ERROR);
			infoAlert.setHeaderText("Mindestens 4 Zeichen!");
			infoAlert.setTitle("Error");
			infoAlert.setContentText("Das Password muss mindestens 4 Zeichen lang sein");
			infoAlert.showAndWait();
		}
	}
}
