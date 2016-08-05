package controller;
	
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;


public class Main extends Application {

	Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		//mainWindow();
		loginWindow();

	}
	
	public void mainWindow(){
		
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindowView.fxml"));
			Parent root = loader.load();
			MainWindowController controler = loader.getController();
			controler.setMain(this);
			Font.loadFont(getClass().getResourceAsStream("/resources/fonts/OpenSans-Regular.ttf"), 14);
			Font.loadFont(getClass().getResourceAsStream("/resources/fonts/OpenSans-Semibold.ttf"), 14);
			Font.loadFont(getClass().getResourceAsStream("/resources/fonts/OpenSans-Light.ttf"), 14);
			Font.loadFont(getClass().getResourceAsStream("/resources/fonts/OpenSans-Bold.ttf"), 14);
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/view/darktheme.css").toExternalForm());
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void loginWindow(){
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/LoginView.fxml"));
			Parent root = loader.load();
			LoginController controler = loader.getController();
			controler.setMain(this);
		
			Scene scene = new Scene(root);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	
	
	/**
	 * Returns the person file preference, i.e. the file that was last opened.
	 * The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getPersonFilePath() {
	    Preferences prefs = Preferences.userNodeForPackage(Main.class);
	    String filePath = prefs.get("filePath", null);
	    if (filePath != null) {
	        return new File(filePath);
	    } else {
	        return null;
	    }
	}

	
	
	/**
	 * Sets the file path of the currently loaded file. The path is persisted in
	 * the OS specific registry.
	 * 
	 * @param file the file or null to remove the path
	 * von http://code.makery.ch/library/javafx-8-tutorial/part5/
	 */
	public void setPersonFilePath(File file) {
	    Preferences prefs = Preferences.userNodeForPackage(Main.class);
	    if (file != null) {
	        prefs.put("filePath", file.getPath());

	        // Update the stage title.
	        primaryStage.setTitle("AddressApp - " + file.getName());
	    } else {
	        prefs.remove("filePath");

	        // Update the stage title.
	        primaryStage.setTitle("AddressApp");
	    }
	}
}
