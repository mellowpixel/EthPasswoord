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
import java.nio.file.Path;
import java.util.Base64;
import java.util.Date;


public class RegisterController {

    Blockchain blockchain;

    @FXML
    public PasswordField newPassword1;
    @FXML
    public PasswordField newPassword2;
    @FXML
    public PasswordField keystorePassword;
    @FXML
    public Label wrongPasswordlabel;



    public RegisterController() {
        this.blockchain = Sys.getInstance().blockchain;
    }


    @FXML
    public void initialize() {

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

        try {

            FileOutputStream fos = new FileOutputStream("./etpconfig");
            System.out.println("Config");
            System.out.println(conf.toJSONString());
            System.out.println("Decoded");
            System.out.println(Base64.getMimeEncoder().encode(conf.toJSONString().getBytes()));
            fos.write(Base64.getMimeEncoder().encode(conf.toJSONString().getBytes()));
//            fos.write(Base64.getMimeDecoder().decode(conf.toJSONString().getBytes("")));
            fos.close();
            blockchain.setWalletFile(keystore);

        } catch (Exception e) {
            System.out.println("Can't save config "+ e.getMessage());
        }
    }




    @FXML
    public void saveAll() {
        System.out.println("Saving All");
        String masterPassword = this.keystorePassword.getText();

        try{
            this.blockchain.getCredentials(masterPassword);
        } catch (Exception e) {
            System.out.println("Can't connect to the blockchain.");
        }

        if(this.blockchain.isAuthenticated()) {
            this.keystorePassword.setText(null);
            this.wrongPasswordlabel.setVisible(false);
            Sys.getInstance().mainController.connectAndSync();
            PasswBankGUI.window.setScene(PasswBankGUI.mainScene);
        } else {
            this.wrongPasswordlabel.setVisible(true);
            System.out.println("Not Authorized.");
        }
    }

}