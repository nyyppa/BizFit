package com.bizfit.bizfit;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class SaveState implements java.io.Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 8425799364006222365L;
    ArrayList<Tracker> trackers;
    String user;

    SaveState(String user){
        this.user=user;
        if(trackers==null){
            trackers=new ArrayList<Tracker>(0);
        }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException{
        try {
            user=Encrypt.encrypt(user);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        try {
            user=Encrypt.decrypt(user);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static SaveState getInstance(String user){
        FileInputStream f_in = null;
        try {
            //File file = new File(MainActivity.activity.getFilesDir(), Encrypt.encrypt(user)+".SaveState");
            //FileOutputStream f_out = new FileOutputStream(file);
            f_in = new FileInputStream(Encrypt.encrypt(user)+".SaveState");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ObjectInputStream obj_in=null;
        SaveState s=null;
        if (f_in!=null) {
            try {
                obj_in = new ObjectInputStream(f_in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Object obj = obj_in.readObject();
                s = (SaveState) obj;
                obj_in.close();
            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(s==null){
            s=new SaveState(user);
        }
        return s;
    }

    public ArrayList<Tracker> addTracker(Tracker t){
        trackers.add(t);
        try {
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trackers;
    }

    public ArrayList<Tracker> getTrackers(){
        return trackers;
    }

    public ArrayList<Tracker> removeTracker(Tracker t){
        Iterator<Tracker> iterator=trackers.iterator();
        while(iterator.hasNext()){
            Tracker current=iterator.next();
            if(current==t){
                iterator.remove();
                break;
            }
        }
        try {
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trackers;
    }

    public void save() throws Exception{
        //File file = new File(MainActivity.activity.getFilesDir(), Encrypt.encrypt(user)+".SaveState");
        //FileOutputStream f_out = new FileOutputStream(file);
        FileOutputStream f_out = new FileOutputStream(Encrypt.encrypt(user)+".SaveState");
        ObjectOutputStream obj_out = new ObjectOutputStream (f_out);
        obj_out.writeObject (this);
        obj_out.close();
    }
}
