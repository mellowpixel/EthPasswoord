package com.mellowpixels;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class LoginController {

    Blockchain blockchain;
    String walletDir = "/Users/coder/CODING/Etherium/node/wallet/";

    @FXML
    public PasswordField password;
    @FXML
    public Label accessDeniedLabel;


    public LoginController() {
        this.blockchain = Sys.getInstance().blockchain;
    }


    @FXML
    public void initialize() {
        System.out.println("Login Controller initialized");

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
            this.blockchain.getOrMakeWallet(masterPassword, this.walletDir);
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
}