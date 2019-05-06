package com.mellowpixels;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.WindowEvent;

import java.util.Random;


public class Controller {

    Blockchain blockchain = Sys.getInstance().blockchain;

    @FXML
    public TextField resourceType;
    @FXML
    public TextField resource;
    @FXML
    public TextField login;
    @FXML
    public PasswordField password;
    @FXML
    public TextField passwordText;
    @FXML
    public Group newCredentialsWrapper;
    @FXML
    public Button generatePassButton;
    @FXML
    public Button submitButton;
    @FXML
    public Button lockScrButton;
    @FXML
    public TableView passwordsTable;



    public Controller () {
    }



    @FXML
    public void initialize() {

        TableColumn<String, CredentialsRecord> resourceType = new TableColumn<>("Resource Name");
        resourceType.setCellValueFactory(new PropertyValueFactory<>("resourceType"));

        TableColumn<String, CredentialsRecord> resource = new TableColumn<>("Resource");
        resource.setCellValueFactory(new PropertyValueFactory<>("resource"));

        TableColumn<String, CredentialsRecord> login = new TableColumn<>("Login");
        login.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<String, CredentialsRecord> password = new TableColumn<>("Pasword");
        password.setCellValueFactory(new PropertyValueFactory<>("password"));

        this.passwordsTable.getColumns().add(resourceType);
        this.passwordsTable.getColumns().add(resource);
        this.passwordsTable.getColumns().add(login);
        this.passwordsTable.getColumns().add(password);

        this.passwordsTable.getItems().add(new CredentialsRecord("Gmail", "http://gmail.com", "coder", "password1"));
        this.passwordsTable.getItems().add(new CredentialsRecord("Facebook", "http://facebook.com", "dimitry@example.com", "TopSecret*"));
        this.passwordsTable.getItems().add(new CredentialsRecord("My Website CMS", "http://coder.com/wp_admin", "admin", "admin"));
    }



    public static void connectAndSync() {
        try{
            Sys.getInstance().blockchain.connect();
        } catch (Exception e) {
            System.out.println("Can't connect to blockchain");
        }


        try{
            Sys.getInstance().blockchain.loadContract("0x3bde7df5e80d93caa97866c6a5ca768efc8bf88a");
        } catch (Exception e) {
            System.out.println("Can't fetch passwords.");
        }


        try{
            Sys.getInstance().blockchain.fetchPasswordsFromBlockchain();
        } catch (Exception e) {
            System.out.println("Can't fetch passwords.");
        }
    }


    @FXML
    public void submitNewCredentials() {
        this.maskPassword();
        System.out.println("Saving new credentials record.");

        System.out.println("" + resourceType.getText());
        System.out.println("" + resource.getText());
        System.out.println("" + login.getText());
        System.out.println("" + password.getText());

    }



    @FXML
    public void generatePassword() {
        int passLen = 10;
        String password = "";
        String[][] symbols = {
                {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"},
                {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"},
                {".",",","?","/","<",">","`","~",";",":","\"","'","\\","|","[","]","{","}","!","@","#","$","%","^","&","*","(",")","-","_","+","="},
                {"0","1","2","3","4","5","6","7","8","9"}
        };

        Random ran = new Random();

        while(password.length() < passLen) {
            String[] sset = symbols[ran.nextInt(4)];
            password += sset[ran.nextInt(sset.length)];
        }

        this.password.setVisible(false);
        this.passwordText.setVisible(true);

        this.passwordText.setText(password);
        this.password.setText(password);
        this.passwordText.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                maskPassword();
            }
        });
        /*this.passwordText.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                maskPassword();
            }
        });*/
    }


    private void maskPassword() {
        this.password.setVisible(true);
        this.passwordText.setVisible(false);
    }


    public void lockScreen() {
        PasswBankGUI.window.setScene(PasswBankGUI.loginScene);
    }
}