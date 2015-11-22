package com.bizfit.bizfit;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * Created by Atte on 22.11.2015.
 */
public class SavingAndReading {
    static public void save(Tracker t) throws IOException{
        FileOutputStream f_out = new FileOutputStream(t.name+".tracker");
        ObjectOutputStream obj_out = new ObjectOutputStream (f_out);
        obj_out.writeObject ( t );
    }

    static public Tracker read(String name) throws IOException, ClassNotFoundException{
        FileInputStream f_in = new FileInputStream(name+".tracker");
        ObjectInputStream obj_in = new ObjectInputStream (f_in);
        Object obj = obj_in.readObject();
        return (Tracker) obj;
    }
}

