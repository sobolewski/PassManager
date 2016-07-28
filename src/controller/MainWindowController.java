package controller;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Optional;
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
import model.UserAccount;
import util.AESProcessor;
import util.PBKDF2;

public class MainWindowController {

	@FXML
	private Button btnChangeText, btnLoginScreen, btnOpenDB, btnExportDB, btnEncriptor, btnSave, btnDelete,
			btnGeneratePW, btnRowSelectTest, btnCopy;
	@FXML Label labelNorth;
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
		dataList.add(new DataEntry("www.yahoo.de", "hoNk99", "apfuh98f"));
		dataList.add(new DataEntry("www.gmail.de", "hoNk99", "ar3g24A"));
		dataList.add(new DataEntry("www.zalando.de", "hoNk99", "Öm.;A=ÜF§"));
	}
	
	public void copyPwToClipboard(){
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
	
	public void btnRowSelectTest(){
		TablePosition pos = tableView.getSelectionModel().getSelectedCells().get(0);
		int row = pos.getRow();

		// Item here is the table view type:
		DataEntry dE = tableView.getItems().get(row);

		TableColumn col = pos.getTableColumn();

		// this gives the value in the selected cell:
		String data = (String) col.getCellObservableValue(dE).getValue();
		System.out.println("The selected row = "+row);
		System.out.println("The selected column = "+col);
		System.out.println("The selected data in the cell is = "+data);
	}

	public void generatePw() {
		
		if (!textfieldDomainUrl.getText().isEmpty() & !textfieldUsername.getText().isEmpty()) {
			try {
				byte[] hashedBytes = PBKDF2.hashedBytesPBKDF2(textfieldDomainUrl.getText(), textfieldUsername.getText(), "master_M)FPass92f33g");
				textfieldPassword.setText(PBKDF2.generatePasswordFromBytes(hashedBytes, 12));
				textfieldPassword.setVisible(true);

			} catch (NoSuchAlgorithmException | InvalidKeySpecException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			Alert infoAlert = new Alert(AlertType.INFORMATION);
			infoAlert.setHeaderText("you must enter domain and username");
			infoAlert.setTitle("Info");
			infoAlert.setContentText("To add an entry use the bottom textfields");
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
			textfieldPassword.setVisible(false);
		} else {
			showInfoAlert();
		}
	}

	public void showInfoAlert() {
		Alert infoAlert = new Alert(AlertType.INFORMATION);
		infoAlert.setHeaderText("No data entered!");
		infoAlert.setTitle("Info");
		infoAlert.setContentText("To add an entry use the bottom textfields");

		infoAlert.showAndWait();
	}
	
	public String showEnterPwDialog() {
		TextInputDialog tid = new TextInputDialog();
		tid.setTitle("Input");
		tid.setContentText("Please confirm your Masterpassword: ");
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
		UserAccount.setPassword("qqqq");
		
		String enteredPw = showEnterPwDialog();
		System.out.println(enteredPw);
		
		if(enteredPw.equals(UserAccount.getPassword())){
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("File mit Passwörtern auswählen");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Database files", "*.db");
			fileChooser.getExtensionFilters().add(extFilter);
			File file = fileChooser.showOpenDialog(main.primaryStage);
			
			if (file != null) {
				
				try {
					this.dataList = aesProcessor.importDataListFromFile(enteredPw, file);
					tableView.setItems(dataList);
				} catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException
						| InvalidKeySpecException | NoSuchPaddingException | InvalidAlgorithmParameterException
						| IllegalBlockSizeException | BadPaddingException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// File.separator
	public void handleExportDBBtn() {

		UserAccount.setPassword("qqqq");
		
		String enteredPw = showEnterPwDialog();
		System.out.println(enteredPw);
		
		if(enteredPw.equals(UserAccount.getPassword())){
			
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("DB exportieren");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Database files", "*.db");
			fileChooser.getExtensionFilters().add(extFilter);
			File file = fileChooser.showSaveDialog(main.primaryStage);
			if (file != null) {
				try {
				
					aesProcessor.exportDataListContainerToFile(dataList, enteredPw, file);

				} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
						| NoSuchPaddingException | InvalidParameterSpecException | IllegalBlockSizeException
						| BadPaddingException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void btnEncriptor() {

		try {
			byte[] hashedBytes = PBKDF2.hashedBytesPBKDF2("www.yahoo.de", "testuser", "master_M)FPass92f33g");
			System.out.println("ByteArrayLenght = " + hashedBytes.length);
			System.out.println(PBKDF2.generatePasswordFromBytes(hashedBytes, 12));

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public void showTooltipGenerate() {
		btnGeneratePW.setText("Reveal the PW");
	}
	public void hideTooltipGenerate() {
		btnGeneratePW.setText("");
	}
	public void labelSaveEntered(){
		labelNorth.setText("Save Entry");
	}
	public void labelSaveExited(){
		labelNorth.setText("");
	}
	public void labelCopyEntered(){
		labelNorth.setText("Copy selected Cell to Clipboard");
	}
	public void labelCopyExited(){
		labelNorth.setText("");
	}
	public void labelDeleteEntered(){
		labelNorth.setText("Delete selected Entry");
	}
	public void labelDeleteExited(){
		labelNorth.setText("");
	}
	public void labelEncryptEntered(){
		labelNorth.setText("Show Password");
	}
	public void labelEncryptExited(){
		labelNorth.setText("");
	}

}
