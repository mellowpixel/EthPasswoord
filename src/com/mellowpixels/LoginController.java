package com.mellowpixels;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;


public class LoginController {

    Blockchain blockchain;
    public static String keystorePath;

    @FXML
    public PasswordField password;
    @FXML
    public Label accessDeniedLabel;


    public LoginController() {
        this.blockchain = Sys.getInstance().blockchain;
    }


    @FXML
    public void initialize() {
        this.password.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                accessDeniedLabel.setVisible(false);
            }
        });

    }


    @FXML
    public void signIn() {
        System.out.println("Signing in");
        String masterPassword = this.password.getText();


        try{
            blockchain.setWalletFile(new File(this.keystorePath));
            this.blockchain.getCredentials(masterPassword);
        } catch (Exception e) {
            System.out.println("Can't connect to the blockchain.");
        }

        if(this.blockchain.isAuthenticated()) {
            this.password.setText(null);
            this.accessDeniedLabel.setVisible(false);
            Sys.getInstance().mainController.connectAndSync();
            PasswBankGUI.window.setScene(PasswBankGUI.mainScene);
        } else {
            this.accessDeniedLabel.setVisible(true);
            System.out.println("Not Authorized.");
        }

    }


    @FXML
    public void goToRegistration() {
        PasswBankGUI.window.setScene(PasswBankGUI.registerScene);
    }
}