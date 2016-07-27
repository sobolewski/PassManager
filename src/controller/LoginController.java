package controller;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import model.UserAccount;
import util.AppProperties;


/** Controls the login screen */
public class LoginController {
  @FXML private TextField userName;
  @FXML private PasswordField passwordField1, passwordField2;
  @FXML private Button btnEnter, btnSetMasterpassword;
  @FXML private Label labelMessage;
  
  
  
	private Main main;
	
	public void setMain(Main main){
		this.main = main;
	}
	
	public void initialize(){
	}
	
	public void handleEnterBtn(){
		Properties prop = new Properties();
		try {
			prop = AppProperties.loadProperties();
		} catch (FileNotFoundException e) {
			//AppProperties.saveProperties(new UserAccount("",""));
			labelMessage.setText("Bitte ein Masterpassword festlegen!");
			labelMessage.setTextFill(Color.rgb(21, 117, 84));
			passwordField2.setVisible(true);
			btnEnter.setVisible(false);
			btnSetMasterpassword.setVisible(true);

			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(passwordField1.getText().equals(prop.getProperty("password"))){
			main.mainWindow();
		}
		else{
			labelMessage.setText("Das Password ist falsch");
			labelMessage.setTextFill(Color.rgb(21, 117, 84));
		}
		
	}
	
	public void setMasterpassword(){
		if (passwordField1.getText().length()>=4){
			if(passwordField1.getText().equals(passwordField2.getText())){
				AppProperties.saveProperties(new UserAccount("",passwordField1.getText()));
				
				
				main.mainWindow();
			}
			else{
					Alert infoAlert = new Alert(AlertType.ERROR);
					infoAlert.setHeaderText("Stimmt nicht überein!");
					infoAlert.setTitle("Error");
					infoAlert.setContentText("Das password stimmt nicht überein");
					infoAlert.showAndWait();
				}
		}
		else{
			Alert infoAlert = new Alert(AlertType.ERROR);
			infoAlert.setHeaderText("Mindestens 4 Zeichen!");
			infoAlert.setTitle("Error");
			infoAlert.setContentText("Das Password muss mindestens 4 Zeichen lang sein");
			infoAlert.showAndWait();
			
		}


	}
	
 // public void initialize() {}
  
//  public void initManager(final LoginManager loginManager) {
//    loginButton.setOnAction(new EventHandler<ActionEvent>() {
//      @Override public void handle(ActionEvent event) {
//        String sessionID = authorize();
//        if (sessionID != null) {
//          loginManager.authenticated(sessionID);
//        }
//      }
//    });
//  }

  /**
   * Check authorization credentials.
   * 
   * If accepted, return a sessionID for the authorized session
   * otherwise, return null.
   */   
//  private String authorize() {
//    return 
//      "open".equals(userName.getText()) && "sesame".equals(password.getText()) 
//            ? generateSessionID() 
//            : null;
//  }
  
  private static int sessionID = 0;

  private String generateSessionID() {
    sessionID++;
    return "xyzzy - session " + sessionID;
  }
}
