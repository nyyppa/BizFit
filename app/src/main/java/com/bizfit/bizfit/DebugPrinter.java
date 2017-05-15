package com.bizfit.bizfit;

import java.util.Objects;

/**
 * Created by Atte Ylivrronen on 23.8.2016.
 */
public class DebugPrinter {
    public  static void Debug(Object message){
        if(message==null)
        {
            Thread.currentThread().dumpStack();
        }
        System.out.println(message);
    }
    public static void Debug(Object message,boolean dumpStackTrace){
        Debug(message);
        if(dumpStackTrace){
            Thread.currentThread().dumpStack();
        }
    }
}
