package com.mellowpixels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Convert;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Blockchain {
    private static final Logger log = LoggerFactory.getLogger(Blockchain.class);
    private String etherAPIEndpoint = "https://ropsten.infura.io/v3/6360239c11f64a1599fbf9655c4f0d96";
//    private String etherAPIEndpoint = "http://127.0.0.1:8545";
    public Sys sys = Sys.getInstance();
    public Web3j connection = null;
    public  Credentials credentials = null;
    private boolean authenticated = false;
    private File walletFile = null;
    public PasswordsBank passwordsBank = null;
    private ContractGasProvider contractGasProvider = new ContractGasProvider() {
        @Override
        public BigInteger getGasPrice(String contractFunc) {
            return Convert.toWei("8", Convert.Unit.GWEI).toBigInteger();
        }

        @Override
        public BigInteger getGasPrice() {
            return Convert.toWei("8", Convert.Unit.GWEI).toBigInteger();
        }

        @Override
        public BigInteger getGasLimit(String contractFunc) {
            return BigInteger.valueOf(7000000L);
        }

        @Override
        public BigInteger getGasLimit() {
            return BigInteger.valueOf(7000000L);
        }
    };

    public TransactionManager txManager;


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
    */



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
        TransactionReceiptProcessor txRtProc;

        this.connection = Web3j.build(new HttpService(this.etherAPIEndpoint));

        txRtProc = new NoOpProcessor(this.connection);
        this.txManager = new RawTransactionManager(this.connection, this.credentials, ChainId.ROPSTEN, txRtProc);

        String msg = "Connected to Ethereum client version: " + this.connection.web3ClientVersion().send()
                .getWeb3ClientVersion();

        sys.log(msg);

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
//            System.out.println("Public: " + credentials.getEcKeyPair().getPublicKey().toString(16));
//            System.out.println("Private: " + credentials.getEcKeyPair().getPrivateKey().toString(16));

        } catch (Exception e) {
            this.authenticated = false;
            System.out.println("Can't load the wallet. " + e.getMessage());
//            System.out.println("Password: " + password);
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



    public void setWalletFile(File walletFile) {
        this.walletFile = walletFile;
    }



    public String deployContract()
            throws Exception
    {
        log.info("Deploying PasswordsBank contract.");

        PasswordsBank passwordsBank = PasswordsBank
                .deploy(this.connection, this.credentials, contractGasProvider).sendAsync().get();

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
                        this.txManager,
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



    public CompletableFuture<TransactionReceipt> savePasswordsToBlockchain(String resourceType, String resource, String login, String password)
            throws Exception
    {
        log.info("Save passwords to blockchain.");
        CompletableFuture<TransactionReceipt> transaction = this.passwordsBank.addNewPassword(resourceType, resource, login, password).sendAsync();
        return transaction;
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
