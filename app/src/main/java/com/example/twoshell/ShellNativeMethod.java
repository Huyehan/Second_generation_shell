package com.example.twoshell;

public class ShellNativeMethod {
    static {
        System.loadLibrary("native-lib");
    }

    public static native int loadDexFile(byte[] dex,long dexlen);
}
