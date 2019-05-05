package com.mellowpixels;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.Random;


public class Controller {

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
}