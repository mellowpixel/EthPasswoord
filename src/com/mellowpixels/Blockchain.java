package com.mellowpixels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;


import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class Blockchain {
    private static final Logger log = LoggerFactory.getLogger(Blockchain.class);
    private String etherAPIEndpoint = "https://ropsten.infura.io/v3/6360239c11f64a1599fbf9655c4f0d96";
//    private String etherAPIEndpoint = null;
    private Web3j connection = null;
    public  Credentials credentials = null;
    private boolean authenticated = false;
    private File walletFile = null;
    private PasswordsBank passwordsBank = null;
    private ContractGasProvider contractGasProvider = new ContractGasProvider() {
        @Override
        public BigInteger getGasPrice(String contractFunc) {
            return BigInteger.valueOf(200000L);
        }

        @Override
        public BigInteger getGasPrice() {
            return BigInteger.valueOf(200000L);
        }

        @Override
        public BigInteger getGasLimit(String contractFunc) {
            return BigInteger.valueOf(3000000L);
        }

        @Override
        public BigInteger getGasLimit() {
            return BigInteger.valueOf(3000000L);
        }
    };

    /*
    * METAMASK
    * oblige ocean glimpse cluster pond unfair guide fault tip flock arrow once
    *
    * INFURA
    * PROJECT ID: 6360239c11f64a1599fbf9655c4f0d96
    * SECRET:     fd13ccff4f4f4f44879fbaa1f4674dcc
    * 
    * └─▪ ./geth --rpcapi personal,db,eth,net,web3 --syncmode=fast --rpc --testnet --keystore ./wallet --datadir ./chaindata
    *
    * Contract: 0x3bde7df5e80d93caa97866c6a5ca768efc8bf88a
    */

    public static void main(String[] args) throws Exception {

        Blockchain app = new Blockchain();

//        String walletDir = "/Users/coder/Library/Ethereum/keystore/";
        String password = "Sp2k68s151";
        String walletDir = "/Users/coder/CODING/Etherium/node/wallet/";

        app.getOrMakeWallet(password, walletDir);
        app.getCredentials(password);
        app.connect();
        app.loadContract("0x3bde7df5e80d93caa97866c6a5ca768efc8bf88a");
        app.savePasswordsToBlockchain("ftp", "ftp://mellowpoxels.com", "boss", "sdoie'>?<");
        app.fetchPasswordsFromBlockchain();
//        app.deployContract();
//        app.createNewPasswordBankAccount();
    }



    public void getOrMakeWallet(String password, String walletDir) throws Exception {
        String walletFile = null;

        ArrayList<String> walletFileNames = this.getWalletFileName(walletDir);

        if(walletFileNames.size() == 0){
            System.out.println("No Wallet File");
            walletFile = this.generateNewWallet(password, walletDir);
        } else {
            walletFile = walletFileNames.get(0);
        }

        this.walletFile = new File(walletDir + walletFile);
    }




    public void connect() throws Exception {

        this.connection = this.etherAPIEndpoint == null
                ? Web3j.build(new HttpService())
                : Web3j.build(new HttpService(this.etherAPIEndpoint));

        System.out.println("Connected to Ethereum client version: "
                + this.connection
                .web3ClientVersion()
                .send()
                .getWeb3ClientVersion());

        this.printBallance();

    }



    public void printBallance()
            throws Exception
    {
        BigInteger wei = this.connection.ethGetBalance(this.credentials.getAddress(), DefaultBlockParameterName.LATEST)
                .sendAsync().get().getBalance();

        BigDecimal eth = Convert.fromWei(String.valueOf(wei), Convert.Unit.ETHER);

        System.out.println("The account ballance is: " + String.valueOf(eth) + " ETH / " + String.valueOf(wei) + " wei");
    }




    public String getBallance()
            throws Exception
    {
        BigInteger wei = this.connection.ethGetBalance(this.credentials.getAddress(), DefaultBlockParameterName.LATEST)
                .sendAsync().get().getBalance();

        BigDecimal eth = Convert.fromWei(String.valueOf(wei), Convert.Unit.ETHER);

        return String.valueOf(eth);
    }




    public String generateNewWallet(String password, String dir)
            throws Exception
    {
        String walletName = WalletUtils.generateNewWalletFile(password, new File(dir), false);
        System.out.println("New wallet created: " + dir + walletName);

        return walletName;
    }

    



    public void getCredentials(String password)
            throws Exception
    {
        String walletpath = this.walletFile.getAbsolutePath();
        System.out.println("Loading credentials.\nWallet file: " + walletpath);

        if(!walletFile.isFile()) {
            System.out.println("Can't load credentials. Wallet file: " + walletpath + " doesn't exist.");
            return;
        }

        try{
            this.credentials = WalletUtils.loadCredentials(password, walletpath);
            this.authenticated = true;
            System.out.println("Address: " + credentials.getAddress());
            System.out.println("Public: " + credentials.getEcKeyPair().getPublicKey().toString(16));
            System.out.println("Private: " + credentials.getEcKeyPair().getPrivateKey().toString(16));

        } catch (Exception e) {
            this.authenticated = false;
            System.out.println("Can't load the wallet. " + e.getMessage());
            System.out.println("Password: " + password);
            System.out.println("Wallet Path: " + walletpath);
        }
    }




    public boolean isAuthenticated() {
        return this.authenticated;
    }




    public ArrayList<String> getWalletFileName(String dir) {
        File folder = new File(dir);
        ArrayList<String> walletFiles = new ArrayList<>();

        for( final File file : folder.listFiles()){
            if(file.isFile() && file.getName().contains("UTC--")){
                System.out.println("Found wallet file: " + file.getName());
                walletFiles.add(file.getName());
            }
        }

        return walletFiles;
    }




    public String deployContract()
            throws Exception
    {
        log.info("Deploying PasswordsBank contract.");

        PasswordsBank passwordsBank = PasswordsBank
                .deploy(this.connection, this.credentials, contractGasProvider).send();

        String contractAddress = passwordsBank.getContractAddress();

        log.info("Deployed contract address: " + contractAddress);

        this.printBallance();

        return contractAddress;
    }




    public void loadContract(String contractAddress)
            throws Exception
    {
        this.passwordsBank =
                PasswordsBank.load(contractAddress,
                        this.connection,
                        this.credentials,
                        contractGasProvider);

        if(!this.passwordsBank.isValid()) {
            log.error("The PasswordsBank Contract is not valid at address:" + contractAddress);
        }
    }



    public TransactionReceipt createNewPasswordBankAccount()
            throws Exception
    {
        log.info("Making new Password Account.");
        TransactionReceipt txReceipt = this.passwordsBank.newPasswordAccount().send();

        log.info("Transaction Receipt.");
        log.info("Gas Used: " + txReceipt.getGasUsed().toString(10));

        this.printBallance();

        return txReceipt;
    }



    public TransactionReceipt savePasswordsToBlockchain(String resourceType, String resource, String login, String password)
            throws Exception
    {
        log.info("Save passwords to blockchain.");
        TransactionReceipt txReceipt = this.passwordsBank.addNewPassword(resourceType, resource, login, password).send();

        log.info("Transaction Receipt.");
        log.info("Gas Used: " + txReceipt.getGasUsed().toString(10));

        this.printBallance();

        return txReceipt;
    }



    public JSONArray fetchPasswordsFromBlockchain()
            throws Exception
    {
        log.debug("Fetching Passwords");

        RemoteCall<String> passwordsJSON = passwordsBank.getPasswords();
        String jsonStr = passwordsJSON.send();
        JSONArray passObj = (JSONArray) new JSONParser().parse(jsonStr);

        log.info("Array content");

        for(int i = 0; i < passObj.size(); i++) {
            JSONObject jo = (JSONObject)passObj.get(i);
            System.out.println(
                            "resource Type: "   + jo.get("resourceType") +
                            " | resource: "     + jo.get("resource") +
                            " | login: "        + jo.get("login") +
                            " | password: "     + jo.get("password"));
        }

        return passObj;
    }



    public String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }
}
