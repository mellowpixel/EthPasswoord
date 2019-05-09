package com.mellowpixels;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Date;


public class RegisterController {

    Blockchain blockchain;
    private String default_wallet_dir = "keystore/";

    @FXML
    public PasswordField newPassword1;
    @FXML
    public PasswordField newPassword2;
    @FXML
    public PasswordField keystorePassword;
    @FXML
    public Label wrongPasswordlabel;
    @FXML
    public Label passNotMatchlabel;
    @FXML
    public RadioButton newKeystoreRadio;
    @FXML
    public RadioButton existingKeystoreRadio;
    @FXML
    public ToggleGroup accountType;
    @FXML
    public Button browseFilesButton;



    public RegisterController() {
        this.blockchain = Sys.getInstance().blockchain;
    }


    @FXML
    public void initialize() {
        ChangeListener<String> changed = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                wrongPasswordlabel.setVisible(false);
                passNotMatchlabel.setVisible(false);
            }
        };

        this.newPassword1.textProperty().addListener(changed);
        this.newPassword2.textProperty().addListener(changed);
        this.keystorePassword.textProperty().addListener(changed);
    }


    @FXML
    public void toggleSelection() {
        boolean existingKeystore = this.existingKeystoreRadio.isSelected();

        this.newPassword1.setDisable(existingKeystore);
        this.newPassword2.setDisable(existingKeystore);

        this.browseFilesButton.setDisable(!existingKeystore);
        this.keystorePassword.setDisable(!existingKeystore);
    }



    @FXML
    public void browseFiles() {
        File keystore;
        FileChooser fileCh = new FileChooser();
        fileCh.setTitle("Locate the keystore or wallet file");
        fileCh.setInitialDirectory(new File(System.getProperty("user.home")));
        keystore = fileCh.showOpenDialog(PasswBankGUI.window);
        JSONObject conf = new JSONObject();

        conf.put("wallet_path", keystore.getAbsolutePath());

        if(this.saveConfig(conf)) {
            blockchain.setWalletFile(keystore);
        }
    }




    private boolean saveConfig(JSONObject conf) {
        try {

            FileOutputStream fos = new FileOutputStream("./etpconfig");
            System.out.println(conf.toJSONString());
            System.out.println(Base64.getMimeEncoder().encode(conf.toJSONString().getBytes()));
            fos.write(Base64.getMimeEncoder().encode(conf.toJSONString().getBytes()));
//            fos.write(Base64.getMimeDecoder().decode(conf.toJSONString().getBytes("")));
            fos.close();

            return true;
        } catch (Exception e) {
            System.out.println("Can't save config "+ e.getMessage());

            return false;
        }
    }




    @FXML
    public void saveAll() {
        String selection = this.accountType.getSelectedToggle().getUserData().toString();

        switch(selection) {
            case "new" : this.createNewKeystore();
                break;

            case "existing": this.activateExistingKeystore();
                break;
        }
    }



    public void activateExistingKeystore() {
        String masterPassword = this.keystorePassword.getText();

        try{
            this.blockchain.getCredentials(masterPassword);
        } catch (Exception e) {
            System.out.println("Can't connect to the blockchain.");
        }

        if(this.blockchain.isAuthenticated()) {
            this.keystorePassword.setText(null);
            this.wrongPasswordlabel.setVisible(false);
            Controller.newAccount = true;
            Sys.getInstance().mainController.connectAndSync();
            PasswBankGUI.window.setScene(PasswBankGUI.mainScene);
        } else {
            this.wrongPasswordlabel.setVisible(true);
            System.out.println("Not Authorized.");
        }
    }



    public void createNewKeystore() {
        String walletFile;
        if(this.newPassword1.getText().equals(this.newPassword2.getText())){
            try {
                JSONObject conf = new JSONObject();
                File keystoreDir = new File(this.default_wallet_dir);

                if(!keystoreDir.isFile())
                    keystoreDir.mkdir();

                walletFile = this.blockchain.generateNewWallet(this.newPassword1.getText(), keystoreDir.getAbsolutePath());
                File keystore = new File(keystoreDir.getAbsolutePath() + "/" + walletFile);

                conf.put("wallet_path", keystore.getAbsolutePath());

                if(this.saveConfig(conf)) {
                    blockchain.setWalletFile(keystore);
                    this.blockchain.getCredentials(this.newPassword1.getText());
                }

                if(this.blockchain.isAuthenticated()) {
                    Controller.newAccount = true;
                    Sys.getInstance().mainController.connectAndSync();
                    PasswBankGUI.window.setScene(PasswBankGUI.mainScene);
                }

            } catch (Exception e) {
                System.out.println("Unable to create new keystore" + e.getMessage());
            }
        } else {
            this.passNotMatchlabel.setVisible(true);
        }
    }

}