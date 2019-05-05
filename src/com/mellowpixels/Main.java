package com.mellowpixels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;


import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private String etherAPIEndpoint = null;
    private Web3j connection = null;
    private Credentials credentials = null;
    private File walletFile = null;
    ContractGasProvider contractGasProvider = new ContractGasProvider() {
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

        Main app = new Main();

//        String walletDir = "/Users/coder/Library/Ethereum/keystore/";
        String walletDir = "/Users/coder/CODING/Etherium/node/wallet/";
        String password = "Sp2k68s151";


        app.etherAPIEndpoint = "https://ropsten.infura.io/v3/6360239c11f64a1599fbf9655c4f0d96";
        app.getOrMakeWallet(password, walletDir);
        app.getCredentials(password);
        app.connect();
        app.loadContract();
//        app.deployContract();
//        app.getClientVersion();
//        app.customTransaction(credentials);
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



    public void printBallance() throws Exception {
        BigInteger wei = this.connection.ethGetBalance(this.credentials.getAddress(), DefaultBlockParameterName.LATEST)
                .sendAsync().get().getBalance();

        BigDecimal eth = Convert.fromWei(String.valueOf(wei), Convert.Unit.ETHER);

        System.out.println("The account ballance is: " + String.valueOf(eth) + " ETH / " + String.valueOf(wei) + " wei");
    }




    public String generateNewWallet(String password, String dir) throws Exception {
        String walletName = WalletUtils.generateNewWalletFile(password, new File(dir), false);
        System.out.println("New wallet created: " + dir + walletName);

        return walletName;
    }

    



    public void getCredentials(String password) throws Exception {
        String walletpath = this.walletFile.getAbsolutePath();
        System.out.println("Loading credentials.\nWallet file: " + walletpath);

        if(!walletFile.isFile()) {
            System.out.println("Can't load credentials. Wallet file: " + walletpath + " doesn't exist.");
            return;
        }

        try{
            this.credentials = WalletUtils.loadCredentials(password, walletpath);
            System.out.println("Address: " + credentials.getAddress());
            System.out.println("Public: " + credentials.getEcKeyPair().getPublicKey().toString(16));
            System.out.println("Private: " + credentials.getEcKeyPair().getPrivateKey().toString(16));

        } catch (Exception e) {
            System.out.println("Can't load the wallet. " + e.getMessage());
            System.out.println("Password: " + password);
            System.out.println("Wallet Path: " + walletpath);
        }
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




    public void deployContract() throws Exception {
        BigInteger GasPrice = null;
        BigInteger GasLimit = null;
        PasswordsBank passwordsBank = PasswordsBank
                .deploy(this.connection, this.credentials, contractGasProvider).send();

        System.out.println("Deployed contract address: " + passwordsBank.getContractAddress());
        this.printBallance();
    }




    public void loadContract() throws Exception {
        PasswordsBank passwordsBank = PasswordsBank
                .load("0x3bde7df5e80d93caa97866c6a5ca768efc8bf88a",
                        this.connection, this.credentials, contractGasProvider);

//        System.out.println("Making new Password Account. " + passwordsBank.newPasswordAccount().send());
        this.printBallance();

        System.out.println("Adding Passwords" + passwordsBank.addNewPassword("sftp", "sftp://mellow.com", "mellow", "Sp2k68s15").send());
        System.out.println("Adding Passwords" + passwordsBank.addNewPassword("ssh", "sftp://mellow.com", "coder", "Sp2k68s15").send());
        this.printBallance();

        System.out.println("Fetching Passwords");
        RemoteCall<String> passwordsJSON = passwordsBank.getPasswords();

        System.out.println(passwordsJSON.send());
    }




    public String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }
}

/*
public void testCreateAccountFromScratch() throws Exception {

	// create new private/public key pair
	ECKeyPair keyPair = Keys.createEcKeyPair();

	BigInteger publicKey = keyPair.getPublicKey();
	String publicKeyHex = Numeric.toHexStringWithPrefix(publicKey);

	BigInteger privateKey = keyPair.getPrivateKey();
	String privateKeyHex = Numeric.toHexStringWithPrefix(privateKey);

	// create credentials + address from private/public key pair
	Credentials credentials = Credentials.create(new ECKeyPair(privateKey, publicKey));
	String address = credentials.getAddress();

	// print resulting data of new account
	System.out.println("private key: '" + privateKeyHex + "'");
	System.out.println("public key: '" + publicKeyHex + "'");
	System.out.println("address: '" + address + "'\n");

	// test (1) check if it's possible to transfer funds to new address
	BigInteger amountWei = Convert.toWei("0.131313", Convert.Unit.ETHER).toBigInteger();
	transferWei(getCoinbase(), address, amountWei);

	BigInteger balanceWei = getBalanceWei(address);
	BigInteger nonce = getNonce(address);

	assertEquals("Unexpected nonce for 'to' address", BigInteger.ZERO, nonce);
	assertEquals("Unexpected balance for 'to' address", amountWei, balanceWei);

	// test (2) funds can be transferred out of the newly created account
	BigInteger txFees = Web3jConstants.GAS_LIMIT_ETHER_TX.multiply(Web3jConstants.GAS_PRICE);
	RawTransaction txRaw = RawTransaction
			.createEtherTransaction(
					nonce,
					Web3jConstants.GAS_PRICE,
					Web3jConstants.GAS_LIMIT_ETHER_TX,
					getCoinbase(),
					amountWei.subtract(txFees));

	// sign raw transaction using the sender's credentials
	byte[] txSignedBytes = TransactionEncoder.signMessage(txRaw, credentials);
	String txSigned = Numeric.toHexString(txSignedBytes);

	// send the signed transaction to the ethereum client
	EthSendTransaction ethSendTx = web3j
			.ethSendRawTransaction(txSigned)
			.sendAsync()
			.get();

	Error error = ethSendTx.getError();
	String txHash = ethSendTx.getTransactionHash();
	assertNull(error);
	assertFalse(txHash.isEmpty());

	waitForReceipt(txHash);

	assertEquals("Unexpected nonce for 'to' address", BigInteger.ONE, getNonce(address));
	assertTrue("Balance for 'from' address too large: " + getBalanceWei(address), getBalanceWei(address).compareTo(txFees) < 0);
}
*/
