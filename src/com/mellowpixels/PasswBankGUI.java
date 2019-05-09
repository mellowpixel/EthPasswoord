package com.mellowpixels;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class PasswBankGUI extends Application {

    public static Scene loginScene, registerScene, mainScene;
    public static Stage window;


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        File configF = new File("etpconfig");
        window = primaryStage;

        Parent loginRoot = FXMLLoader.load(getClass().getResource("login_layout.fxml"));
        Parent registerRoot = FXMLLoader.load(getClass().getResource("register_layout.fxml"));
        Parent mainRoot = FXMLLoader.load(getClass().getResource("main_layout.fxml"));

        loginScene = new Scene(loginRoot, 700, 700);
        registerScene = new Scene(registerRoot, 700, 700);
        mainScene = new Scene(mainRoot, 700, 700);

        loginScene.getStylesheets().add("style.css");
        registerScene.getStylesheets().add("style.css");
        mainScene.getStylesheets().add("style.css");

        window.setResizable(false);
        window.setTitle("Ethereum Passwords Bank");


        if(configF.exists() && configF.isFile()){
            Path path = FileSystems.getDefault().getPath(configF.getPath());
            byte[] configContent = Files.readAllBytes(path);
            System.out.println("configContent");
            String s = new String(Base64.getMimeDecoder().decode(configContent));
            System.out.println(s);
            JSONObject config = (JSONObject) new JSONParser().parse(s);

            if(config.containsKey("wallet_path") && new File(config.get("wallet_path").toString()).isFile()) {
                LoginController.keystorePath = config.get("wallet_path").toString();
                window.setScene(loginScene);
            } else {
                System.out.println("Set Scene: Register has config");
                window.setScene(registerScene);
            }
        } else {
            System.out.println("Set Scene: Register no config");
            window.setScene(registerScene);
        }

        window.show();
//        primaryStage.
    }
}
