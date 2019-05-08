package com.mellowpixels;

import java.math.BigInteger;
import java.util.*;

public class Sys {

    public Blockchain blockchain;
    public Controller mainController;
    private static ArrayList<String> logs;
    public static Map<String, BigInteger> pendingTransactions;

    private static Sys sysInstance = new Sys();

    public static Sys getInstance() {
        return sysInstance;
    }

    private Sys() {
        this.blockchain = new Blockchain();
        this.logs = new ArrayList<>();
        this.pendingTransactions = new HashMap<>();
    }


    public static void log(String message){
        Sys.logs.add(message);
    }


    public static ArrayList<String> getLogs(int start, int length) {
        ArrayList<String> output = new ArrayList<>();

        for (int i = start; i < start + length; i++) {
            if(i < output.size())
                output.add(Sys.logs.get(i));
        }

        return output;
    }


    public static ArrayList<String> getLogs(int topNum) {
        ArrayList<String> output = new ArrayList<>();

        for (int i = Sys.logs.size()-1; i >= 0; i--) {
            if(i < Sys.logs.size()){
                output.add(Sys.logs.get(i));
            }
        }

        return output;
    }



    public static void addTransaction(String hash, BigInteger timestamp) {
        Sys.pendingTransactions.put(hash, timestamp);
    }



    public static void removeTransaction(String hash, BigInteger timestamp) {
        try {
            Sys.pendingTransactions.remove(hash, timestamp);
        } catch (Exception e) {
            System.out.println("Can't remove pending transaction.");
        }
    }



    public static Map<String, BigInteger> getPendingTrx(int topNum) {
        Map<String, BigInteger> output = new HashMap<>();

        Sys.pendingTransactions.forEach((k, v) -> {
            if(output.size() < topNum) {
                output.put(k, v);
            }
        });

        return output;
    }

}
