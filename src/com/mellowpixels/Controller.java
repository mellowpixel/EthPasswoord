package com.mellowpixels;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.WindowEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Random;


public class Controller {

    Blockchain blockchain = Sys.getInstance().blockchain;

    @FXML
    public Label ballanceLabel;
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
        Sys.getInstance().mainController = this;
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

    }



    public void connectAndSync() {
        Blockchain bc = Sys.getInstance().blockchain;

        try{
            bc.connect();
            this.ballanceLabel.setText("BALLANCE: " + bc.getBallance() + " ETH");
        } catch (Exception e) {
            System.out.println("Can't connect to blockchain");
        }


        try{
            bc.loadContract("0x3bde7df5e80d93caa97866c6a5ca768efc8bf88a");
        } catch (Exception e) {
            System.out.println("Can't load Contract.");
        }


        try{
            JSONArray credentialsData = bc.fetchPasswordsFromBlockchain();
            this.updateCredentialsRecords(credentialsData);
        } catch (Exception e) {
            System.out.println("Can't fetch passwords.");
        }
    }




    public void updateCredentialsRecords(JSONArray credentialsList) {

        for(int i = 0; i < credentialsList.size(); i++) {
            JSONObject jo = (JSONObject)credentialsList.get(i);
            this.passwordsTable.getItems().add(new CredentialsRecord(
                    jo.get("resourceType").toString(),
                    jo.get("resource").toString(),
                    jo.get("login").toString(),
                    jo.get("password").toString()));
        }

    }




    @FXML
    public void submitNewCredentials()
            throws Exception
    {
        this.maskPassword();
        System.out.println("Saving new credentials record.");

        CredentialsRecord enc =
                this.encryptCredentialsRecord(
                        resourceType.getText(),
                        resource.getText(),
                        login.getText(),
                        password.getText());

    /*    System.out.println("" + resourceType.getText() + " - " + enc.getResourceType());
        System.out.println("" + resource.getText() + " - " + enc.getResource());
        System.out.println("" + login.getText() + " - " + enc.getLogin());
        System.out.println("" + password.getText() + " - " + enc.getPassword());*/

        try {
            blockchain.savePasswordsToBlockchain(
                    enc.getResourceType(),
                    enc.getResource(),
                    enc.getLogin(),
                    enc.getPassword());
        } catch (Exception e) {
            System.out.println("Can't save password to blockchain" + e.getMessage());
        }


        try{
            JSONArray credentialsData = blockchain.fetchPasswordsFromBlockchain();
            this.updateCredentialsRecords(credentialsData);
        } catch (Exception e) {
            System.out.println("Can't fetch passwords.");
        }
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
    }




    private CredentialsRecord encryptCredentialsRecord(String resourceType, String resource, String login, String password)
            throws Exception
    {
        Encryption enc = new Encryption(blockchain.credentials.getEcKeyPair().getPrivateKey().toString().substring(0, 16));

        try {
            return new CredentialsRecord(
                    enc.encrypt(resourceType),
                    enc.encrypt(resource),
                    enc.encrypt(login),
                    enc.encrypt(password));
        } catch (Exception e) {
            System.out.println("Can't encrypt credentials"+e.getMessage());
            return null;
        }
    }




    private void maskPassword() {
        this.password.setVisible(true);
        this.passwordText.setVisible(false);
    }


    public void lockScreen() {
        PasswBankGUI.window.setScene(PasswBankGUI.loginScene);
    }
}