package com.mellowpixels;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.WindowEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.response.EmptyTransactionReceipt;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class Controller {

    Blockchain blockchain = Sys.getInstance().blockchain;
    public Sys sys = Sys.getInstance();
    public ArrayList<String> pendingTrx;

    @FXML
    public Label ballanceLabel;
    @FXML
    public Label statusLabel;
    @FXML
    public Label addressLabel;
    @FXML
    public Label networkLabel;
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
    public Button generatePassButton;
    @FXML
    public Button submitButton;
    @FXML
    public Button lockScrButton;
    @FXML
    public TableView passwordsTable;



    public Controller () {

        Sys.getInstance().mainController = this;
        this.pendingTrx = new ArrayList<>();
        this.pollTransaction();
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
            this.addressLabel.setText("ADDRESS: " + bc.credentials.getAddress());

        } catch (Exception e) {
            System.out.println("Can't connect to blockchain");
            sys.log("Can't connect to blockchain");
        }


        /*try {
            bc.deployContract();
        } catch (Exception e) {
            System.out.println("Unable to deploy contract" + e.getMessage());
        }*/


        try{
//            bc.loadContract("0x3bde7df5e80d93caa97866c6a5ca768efc8bf88a");
            bc.loadContract("0xcc73ed5442e5de44fd40c4624684b8bbd94616d4");
        } catch (Exception e) {
            System.out.println("Can't load Contract.");
            sys.log("Can't load Contract.");
        }



        /*try {
            bc.createNewPasswordBankAccount();
        } catch (Exception e) {
            System.out.println("Unable to crete new account" + e.getMessage());
        }*/


        try{
            JSONArray credentialsData = bc.fetchPasswordsFromBlockchain();
            this.updateCredentialsRecords(credentialsData);
        } catch (Exception e) {
            System.out.println("Can't fetch passwords.");
            sys.log("Can't fetch passwords.");
        }
    }






    public void updateCredentialsRecords(JSONArray credentialsList) {

        this.passwordsTable.getItems().removeAll();

        for(int i = 0; i < credentialsList.size(); i++) {
            JSONObject jo = (JSONObject)credentialsList.get(i);

            try {
                CredentialsRecord record = this.decryptCredentialsRecord(
                        jo.get("resourceType").toString(),
                        jo.get("resource").toString(),
                        jo.get("login").toString(),
                        jo.get("password").toString());

                if(record.equals(null)) continue;

                this.passwordsTable.getItems().add(record);

            } catch (Exception e) {
                System.out.println("Unable to decrypt credentials");
            }
        }
    }





    @FXML
    public void submitNewCredentials()
            throws Exception
    {
        this.maskPassword();
        System.out.println("Saving new credentials record.");
        sys.log("Saving new credentials record.");


        CredentialsRecord enc =
                this.encryptCredentialsRecord(
                        resourceType.getText(),
                        resource.getText(),
                        login.getText(),
                        password.getText());

        CredentialsRecord tableRow = new CredentialsRecord(
                resourceType.getText(),
                resource.getText(),
                login.getText(),
                password.getText());

        this.passwordsTable.getItems().add(tableRow);

        this.saveCache();

        try {
            this.statusLabel.setText("Saving to Blockchain. Pending...");

            TransactionReceipt txr = blockchain.passwordsBank.addNewPassword(enc.getResourceType(), enc.getResource(), enc.getLogin(), enc.getPassword()).send();
            System.out.println("Transaction Hash. " + txr.getTransactionHash());
            String txHash = txr.getTransactionHash();

            Sys.addTransaction(txr.getTransactionHash(), BigInteger.valueOf(System.currentTimeMillis()));

        } catch (Exception e) {
            System.out.println("Can't save password to blockchain" + e.getMessage());
            sys.log("Can't save password to blockchain" + e.getMessage());
        }

    }




    private void pollTransaction() {
        final Timer timer = new Timer();
        final Controller self = this;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                self.pendingTrx.clear();

                Map<String, BigInteger> transactions = Sys.getPendingTrx(3);

                transactions.forEach((txHash, timestamp) -> {
                    int sec = (int)(System.currentTimeMillis() - timestamp.longValue()) / 1000;
                    int min = (int)Math.floor(sec / 60);
                    String timeElapsed = (min >= 10 ? min : "0" + min) + "min " + (sec % 60 >= 10 ? sec % 60 : "0" + sec % 60) + "sec";

                    try {
                        Optional<TransactionReceipt> txReceipt = blockchain.connection
                                .ethGetTransactionReceipt(txHash).send().getTransactionReceipt();

                        System.out.println("Transaction executed after "+timeElapsed+". "+txReceipt.get().getStatus());
                        Sys.log("Transaction executed after "+timeElapsed+". "+txReceipt.get().getStatus());
                        Sys.removeTransaction(txHash, timestamp);

                    } catch(Exception e) {
                        self.pendingTrx.add("Transaction "+txHash+" "+timeElapsed+" : Pending...");
                    }
                });

                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        self.printLogs();
                    }
                });

            }
        }, 1000, 1000);
    }





    public void refreshAll() throws Exception {

        String txHash = "0xb184d83ebb00dd860695316490df9f0e0859905f16013072c70ff1b09b15447f";
//        String txHash = "0x208e521d78df08684cb423db28d912a2444dc5ed506536d0514f9022b1867f1f";


        try {
            Optional<TransactionReceipt> txReceipt =
                    blockchain.connection.ethGetTransactionReceipt(txHash).send().getTransactionReceipt();

//            BigDecimal eth = Convert.fromWei(String.valueOf(wei), Convert.Unit.ETHER);
            BigInteger gp = blockchain.connection.ethGasPrice().send().getGasPrice();
            BigInteger wei = txReceipt.get().getGasUsed();

            System.out.println("Gas used:  "+wei.toString() + " Gp:"+gp);
        } catch (Exception e) {
            System.out.println("No Block number." + e.getMessage());
        }
    }





    private void saveCache() {

    }






    public void submitCredentialsSusccess(EthTransaction ethTx) {
        System.out.println("Ethereum Tx. " + ethTx.toString());
        System.out.println("Tx: " + ethTx.getTransaction().toString());
        Controller self = this;

        Platform.runLater(new Runnable() {
            @Override public void run() {

            }
        });


    }





    public Void submitCredentialsException(Throwable txReceipt) {

        System.out.println("Transaction timeout.");
        System.out.println(txReceipt.toString());
        Controller self = this;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                self.statusLabel.setText("Transaction timeout. " + txReceipt.getMessage());
            }
        });

        return null;
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
        Cryptograph crp = new Cryptograph(blockchain.credentials.getEcKeyPair().getPrivateKey().toString());

        try {
            return new CredentialsRecord(
                    crp.encrypt(resourceType),
                    crp.encrypt(resource),
                    crp.encrypt(login),
                    crp.encrypt(password));
        } catch (Exception e) {
            System.out.println("Can't encrypt credentials "+e.getMessage());
            return null;
        }
    }






    private CredentialsRecord decryptCredentialsRecord(String resourceType, String resource, String login, String password)
            throws Exception
    {
        Cryptograph crp = new Cryptograph(blockchain.credentials.getEcKeyPair().getPrivateKey().toString());

        CredentialsRecord decryptedrecord = null;

        try {
            decryptedrecord = new CredentialsRecord(
                    crp.decrypt(resourceType),
                    crp.decrypt(resource),
                    crp.decrypt(login),
                    crp.decrypt(password));

        } catch (Exception e) {
            System.out.println("Can't decrypt credentials "+e.getMessage());
        }

        return decryptedrecord;
    }




    public void printLogs(){
        String output = "";
        ArrayList<String> logs = sys.getLogs(6 - this.pendingTrx.size());

        for (String tx : this.pendingTrx) {
            output += tx + "\n";
        }

        for (String line : logs) {
            output += line + "\n";
        }

        this.statusLabel.setText(output);
    }




    private void maskPassword() {
        this.password.setVisible(true);
        this.passwordText.setVisible(false);
    }




    public void lockScreen() {
        this.passwordsTable.getItems().removeAll();
        PasswBankGUI.window.setScene(PasswBankGUI.loginScene);
    }
}