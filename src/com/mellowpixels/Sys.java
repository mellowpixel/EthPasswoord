package com.mellowpixels;

public class Sys {

    public Blockchain blockchain;
    public Controller mainController;

    private static Sys sysInstance = new Sys();

    public static Sys getInstance() {
        return sysInstance;
    }

    private Sys() {
        this.blockchain = new Blockchain();
    }
}
