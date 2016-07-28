package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
import util.AES;
import util.AES2_new;
import util.PBKDF2;
import util.RandomPasswordGenerator;

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
	
	private AES aes = new AES();
	private AES2_new aes2_new = new AES2_new();

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
				// byte[] salt = PBKDF2.getSalt();
				// vorerst noch statisches salz, ich könnte es unischtbar in der
				// tabelle mitspeichern

				
				byte[] salt = "+a,v9H".getBytes("UTF-8");
				byte[] hashedBytes = PBKDF2.hashedBytesPBKDF2(textfieldDomainUrl.getText(), textfieldUsername.getText(), "master_M)FPass92f33g", salt);
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
					this.dataList = aes2_new.importDataListFromFile(enteredPw, file);
					tableView.setItems(dataList);
				} catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException
						| InvalidKeySpecException | NoSuchPaddingException | InvalidAlgorithmParameterException
						| IllegalBlockSizeException | BadPaddingException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				/*
				this.dataList = aes.decryptDataListFromFile(enteredPw, file);
				tableView.setItems(dataList);
				*/
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
				//writeDataListCipheredToFile(file, dataList);
				try {
				
					aes2_new.exportDataListContainerToFile(dataList, enteredPw, file);
					
					//aes.encryptDataListToFile(dataList, enteredPw, file);

				} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
						| NoSuchPaddingException | InvalidParameterSpecException | IllegalBlockSizeException
						| BadPaddingException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}}
	}

	public void btnEncriptor() {

		try {
			// byte[] salt = PBKDF2.getSalt();
			// vorerst noch statisches salz, ich könnte es unischtbar in der
			// tabelle mitspeichern
			byte[] salt = "+a,v9H".getBytes("UTF-8");
			byte[] hashedBytes = PBKDF2.hashedBytesPBKDF2("www.yahoo.de", "testuser", "master_M)FPass92f33g", salt);
			System.out.println("ByteArrayLenght = " + hashedBytes.length);
			System.out.println(PBKDF2.generatePasswordFromBytes(hashedBytes, 12));

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * High Level Algorithm : 1. Generate a AES key (specify the Key size during
	 * this phase) 2. Create the Cipher 3. To Encrypt : Initialize the Cipher
	 * for Encryption 4. To Decrypt : Initialize the Cipher for Decryption
	 * ------------ Step 1. Generate an AES key using KeyGenerator Initialize
	 * the keysize to 128 bits (16 bytes)
	 * 
	 * Step 2. Generate an Initialization Vector (IV) a. Use SecureRandom to
	 * generate random bits The size of the IV matches the blocksize of the
	 * cipher (128 bits for AES) b. Construct the appropriate IvParameterSpec
	 * object for the data to pass to Cipher's init() method
	 * 
	 * final int AES_KEYLENGTH = 128; // change this as desired for the security
	 * level you want byte[] iv = new byte[AES_KEYLENGTH / 8]; // Save the IV
	 * bytes or send it in plaintext with the encrypted data so you can decrypt
	 * the data later SecureRandom prng = new SecureRandom();
	 * prng.nextBytes(iv);
	 * 
	 * Step 3. Create a Cipher by specifying the following parameters a.
	 * Algorithm name - here it is AES b. Mode - here it is CBC mode c. Padding
	 * - e.g. PKCS7 or PKCS5
	 * 
	 * Step 4. Initialize the Cipher for Encryption
	 */

	private void writeDataListCipheredToFile(File file, ObservableList<DataEntry> dataList) {
		try {

			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec key = new SecretKeySpec("0123456789abcdef".getBytes("UTF-8"), "AES");
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
			CipherOutputStream cos = new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(file)), c);

			ObjectOutputStream oos = new ObjectOutputStream(cos);

			oos.writeObject(new ArrayList<DataEntry>(dataList));
			oos.flush();
			oos.close();
			System.out.println("exportiert!!");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ObservableList<DataEntry> getDataEntriesFromCypheredFile(File file) {
		ArrayList<DataEntry> del = new ArrayList<DataEntry>();
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec key = new SecretKeySpec("0123456789abcdef".getBytes("UTF-8"), "AES");
			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
			CipherInputStream cis = new CipherInputStream(new BufferedInputStream(new FileInputStream(file)), c);

			ObjectInputStream ois = new ObjectInputStream(cis);

			del = (ArrayList<DataEntry>) ois.readObject();
			ois.close();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return FXCollections.observableArrayList(del);	
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
