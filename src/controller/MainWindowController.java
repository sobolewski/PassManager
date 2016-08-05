package controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import model.DataEntry;
import util.AESProcessor;
import util.AppProperties;
import util.PBKDF2;

public class MainWindowController {

	@FXML
	private Button btnChangeText, btnLoginScreen, btnOpenDB, btnExportDB, btnSave, btnDelete, btnGeneratePW, btnCopy;
	@FXML
	Label labelNorth;
	@FXML
	private TableView<DataEntry> tableView;
	@FXML
	private TableColumn<DataEntry, String> columnUrl, columnUsername, columnPassword;
	@FXML
	private TextField textfieldDomainUrl, textfieldUsername, textfieldPassword;
	@FXML
	private HBox hboxMenu;

	private ObservableList<DataEntry> dataList = FXCollections.observableArrayList();
	private Main main;
	private AESProcessor aesProcessor = new AESProcessor();

	// wird immer aufgerufen beim starten des controllers
	public void initialize() {
		columnUrl.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("url"));
		columnUsername.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("username"));
		columnPassword.setCellValueFactory(new PropertyValueFactory<DataEntry, String>("password"));
	}

	public void setMain(Main main) {
		this.main = main;
		initTestData();
		tableView.setItems(dataList);
	}

	public void initTestData() {
		dataList.add(new DataEntry("www.example.com", "max-Muster", "password1"));
	}

	public void copyPwToClipboard() {
		TablePosition pos = tableView.getSelectionModel().getSelectedCells().get(0);
		int row = pos.getRow();
		DataEntry dE = tableView.getItems().get(row);
		TableColumn col = pos.getTableColumn();
		String data = (String) col.getCellObservableValue(dE).getValue();
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();
		content.putString(data);
		clipboard.setContent(content);
	}

	/*
	 * public void btnRowSelectTest(){ TablePosition pos =
	 * tableView.getSelectionModel().getSelectedCells().get(0); int row =
	 * pos.getRow();
	 * 
	 * // Item here is the table view type: DataEntry dE =
	 * tableView.getItems().get(row);
	 * 
	 * TableColumn col = pos.getTableColumn();
	 * 
	 * // this gives the value in the selected cell: String data = (String)
	 * col.getCellObservableValue(dE).getValue();
	 * System.out.println("The selected row = "+row);
	 * System.out.println("The selected column = "+col);
	 * System.out.println("The selected data in the cell is = "+data); }
	 */

	public void generatePw() {

		if (!textfieldDomainUrl.getText().isEmpty() & !textfieldUsername.getText().isEmpty()) {
			try {
				Properties prop = new Properties();
				prop = AppProperties.loadProperties();
	
				byte[] hashedBytes = PBKDF2.hashedBytesPBKDF2StaticSalt(textfieldDomainUrl.getText(), textfieldUsername.getText(),
						prop.getProperty("q9p39gAuj3S439gnfx+O<5kxjbnykbcurrymitschrankerotweiss"));
				textfieldPassword.setText(PBKDF2.generatePasswordFromBytes(hashedBytes, 12));
				textfieldPassword.setVisible(true);

			} catch (NoSuchAlgorithmException | InvalidKeySpecException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				Alert infoAlert = new Alert(AlertType.ERROR);
				infoAlert.setHeaderText("No File access!");
				infoAlert.setTitle("Error");
				infoAlert.setContentText("#no access to useraccount!");
				infoAlert.showAndWait();
				e.printStackTrace();
			}
		} else {
			Alert infoAlert = new Alert(AlertType.INFORMATION);
			infoAlert.setHeaderText("you must enter domain and username");
			infoAlert.setTitle("Info");
			infoAlert.setContentText("#to add an entry use the textfields");
			infoAlert.showAndWait();
		}
	}

	public void saveNewEntry() {
		if (!textfieldDomainUrl.getText().isEmpty() || !textfieldUsername.getText().isEmpty()
				|| !textfieldPassword.getText().isEmpty()) {
			dataList.add(new DataEntry(textfieldDomainUrl.getText(), textfieldUsername.getText(),
					textfieldPassword.getText()));
			textfieldDomainUrl.clear();
			textfieldUsername.clear();
			textfieldPassword.clear();
		} else {
			showInfoAlert();
		}
	}

	public void showInfoAlert() {
		Alert infoAlert = new Alert(AlertType.INFORMATION);
		infoAlert.setHeaderText("No data entered!");
		infoAlert.setTitle("Info");
		infoAlert.setContentText("#to add an entry use the textfields");
		infoAlert.showAndWait();
	}

	public String showEnterPwDialog() {
		TextInputDialog tid = new TextInputDialog();
		tid.setTitle("Input");
		tid.setContentText("#confirm your Masterpassword: ");
		Optional<String> result = tid.showAndWait();
		return result.get();
	}

	public void deleteDataEntry() {
		ObservableList<DataEntry> selected, all;
		all = tableView.getItems();
		selected = tableView.getSelectionModel().getSelectedItems();
		selected.forEach(all::remove);
	}

	public void showLoginScreen() {
		main.loginWindow();
	}

	public void openDB() {

		String enteredPw = showEnterPwDialog();
		Properties prop = new Properties();
		try {
			prop = AppProperties.loadProperties();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		byte[] saltBytes = Base64.getDecoder()
				.decode(prop.getProperty("xYga7hGn94nmlfaaLdb.yb1mfbkb-ycurrymitschrankerotweiss"));
		byte[] pwBytes = Base64.getDecoder()
				.decode(prop.getProperty("q9p39gAuj3S439gnfx+O<5kxjbnykbcurrymitschrankerotweiss"));

		PBKDF2 pbkdf2 = new PBKDF2();
		try {
			byte[] pwHash = pbkdf2.hashMasterPasswordPBKDF2(enteredPw, saltBytes);
			if (Arrays.equals(pwBytes, pwHash)) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Choose database to import");
				fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Database files", "*.db");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showOpenDialog(main.primaryStage);

				if (file != null) {
					try {
						this.dataList = aesProcessor.importDataListFromFile(enteredPw, file);
						tableView.setItems(dataList);
						labelNorth.setText("#import complete");
					} catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException
							| InvalidKeySpecException | NoSuchPaddingException | InvalidAlgorithmParameterException
							| IllegalBlockSizeException | BadPaddingException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Alert infoAlert = new Alert(AlertType.ERROR);
					infoAlert.setHeaderText("No File access!");
					infoAlert.setTitle("Error");
					infoAlert.setContentText("#no access to selected File!");
					infoAlert.showAndWait();
				}
			} else {
				Alert infoAlert = new Alert(AlertType.ERROR);
				infoAlert.setHeaderText("Password Mismatch!");
				infoAlert.setTitle("Error");
				infoAlert.setContentText("#the entered password was not correct!");
				infoAlert.showAndWait();
			}

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// File.separator
	public void handleExportDBBtn() {

		String enteredPw = showEnterPwDialog();
		// salz holen,
		// pwHash holen,
		// enteredPw mit salz hashen
		// beide hashes vergleichen

		Properties prop = new Properties();
		try {
			prop = AppProperties.loadProperties();
		} catch (IOException e2) {
			Alert infoAlert = new Alert(AlertType.ERROR);
			infoAlert.setHeaderText("#No File!");
			infoAlert.setTitle("Error");
			infoAlert.setContentText("#no access to properties!");
			infoAlert.showAndWait();
			
			e2.printStackTrace();
		}
		byte[] saltBytes = Base64.getDecoder()
				.decode(prop.getProperty("xYga7hGn94nmlfaaLdb.yb1mfbkb-ycurrymitschrankerotweiss"));
		byte[] pwBytes = Base64.getDecoder()
				.decode(prop.getProperty("q9p39gAuj3S439gnfx+O<5kxjbnykbcurrymitschrankerotweiss"));

		PBKDF2 pbkdf2 = new PBKDF2();
		try {
			byte[] pwHash = pbkdf2.hashMasterPasswordPBKDF2(enteredPw, saltBytes);
			if (Arrays.equals(pwBytes, pwHash)) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("DB export");
				fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Database files", "*.db");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showSaveDialog(main.primaryStage);
				if (file != null) {
					try {

						aesProcessor.exportDataListContainerToFile(dataList, enteredPw, file);
						labelNorth.setText("#export complete");

					} catch (InvalidKeyException e ) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Alert infoAlert = new Alert(AlertType.ERROR);
						infoAlert.setHeaderText("Invalid Key");
						infoAlert.setTitle("Error");
						infoAlert.setContentText("It seems that your JavaEnvironment does not allow secure keyleangths."
								+ " In order allow a secure export of your data please download and install the"
								+ " Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 8");
						infoAlert.showAndWait();
					} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Alert infoAlert = new Alert(AlertType.ERROR);
								infoAlert.setHeaderText("IOException");
								infoAlert.setTitle("Error");
								infoAlert.setContentText("#");
								infoAlert.showAndWait();
					} catch (BadPaddingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Alert infoAlert = new Alert(AlertType.ERROR);
								infoAlert.setHeaderText("bad padding occured");
								infoAlert.setTitle("Error");
								infoAlert.setContentText("!");
								infoAlert.showAndWait();
					} catch (IllegalBlockSizeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Alert infoAlert = new Alert(AlertType.ERROR);
						infoAlert.setHeaderText("IllegalBlockSize");
						infoAlert.setTitle("Error");
						infoAlert.setContentText("!");
						infoAlert.showAndWait();
					} catch (InvalidParameterSpecException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Alert infoAlert = new Alert(AlertType.ERROR);
						infoAlert.setHeaderText("InvalidParameterSpec");
						infoAlert.setTitle("Error");
						infoAlert.setContentText("!");
						infoAlert.showAndWait();
					} catch (NoSuchPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Alert infoAlert = new Alert(AlertType.ERROR);
						infoAlert.setHeaderText("NoSuchPadding");
						infoAlert.setTitle("Error");
						infoAlert.setContentText("!");
						infoAlert.showAndWait();
					} catch (InvalidKeySpecException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Alert infoAlert = new Alert(AlertType.ERROR);
						infoAlert.setHeaderText("InvalidKeySpec");
						infoAlert.setTitle("Error");
						infoAlert.setContentText("!");
						infoAlert.showAndWait();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Alert infoAlert = new Alert(AlertType.ERROR);
						infoAlert.setHeaderText("NoSuchAlgorithm");
						infoAlert.setTitle("Error");
						infoAlert.setContentText("!");
						infoAlert.showAndWait();
					}finally{
						
					}}
				else {
					Alert infoAlert = new Alert(AlertType.ERROR);
					infoAlert.setHeaderText("No File access!");
					infoAlert.setTitle("Error");
					infoAlert.setContentText("No access to selected File!");
					infoAlert.showAndWait();
				}
			} else{
				Alert infoAlert = new Alert(AlertType.ERROR);
				infoAlert.setHeaderText("Password Mismatch!");
				infoAlert.setTitle("Error");
				infoAlert.setContentText("#the entered password was not correct!");
				infoAlert.showAndWait();
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
			Alert infoAlert = new Alert(AlertType.ERROR);
			infoAlert.setHeaderText("Catch pbkdf2!");
			infoAlert.setTitle("Error");
			infoAlert.setContentText("!");
			infoAlert.showAndWait();
			e1.printStackTrace();
		}
	}

	public void showTooltipGenerate() {
		labelNorth.setText("#generate a password");
	}

	public void hideTooltipGenerate() {
		labelNorth.setText("");
	}

	public void labelSaveEntered() {
		labelNorth.setText("#save entry");
	}

	public void labelSaveExited() {
		labelNorth.setText("");
	}

	public void labelCopyEntered() {
		labelNorth.setText("#copy selected cell to clipboard");
	}

	public void labelCopyExited() {
		labelNorth.setText("");
	}

	public void labelDeleteEntered() {
		labelNorth.setText("#delete selected entry");
	}

	public void labelDeleteExited() {
		labelNorth.setText("");
	}
}
