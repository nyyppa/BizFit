package com.bizfit.bizfit;


import android.database.Cursor;
import android.provider.ContactsContract;

import com.bizfit.bizfit.activities.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SaveState implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8425799364006222365L;
    ArrayList<Tracker> trackers;
    public String user;
    LastUser lastUser;

    /**
     * Do not manually construct new saveStates, rather call SaveState.getInstance(String user)
     *
     * @param user User name
     */
    SaveState(String user) {
        this.user = user;
        if (trackers == null) {
            trackers = new ArrayList<Tracker>(0);
        }
    }

    public static List<String> getUsers() {
        String[] s = MainActivity.activity.getFilesDir().list();
        List<String> users = new ArrayList<String>(0);
        for (String m : s) {
            if (m.endsWith(".SaveState")) {
                String[] split = m.split(File.separator);
                users.add(split[split.length - 1].replaceAll(".SaveState", ""));
            }
        }

        if (users.isEmpty()) {
            Cursor c = MainActivity.activity.getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
            c.moveToFirst();
            users.add(c.getString(c.getColumnIndex("display_name")));
            c.close();

        }
        return users;
    }

    /**
     * encrypts user name before saving it to memory
     *
     * @param out
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        try {
            user = Encrypt.encrypt(user);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out.defaultWriteObject();
        System.out.println(user + "testi");
        try {
            user=Encrypt.decrypt(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(user+"testi");
    }

    /**
     * decrypts user name when reading it from memory
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            user = Encrypt.decrypt(user);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    /**
     * If user has SaveState reads it from memory and returns it, if no data
     * is found for user it constructs new SaveState for that user
     * and returns it
     *
     * @param user User name
     * @return SaveState for the user
     */
    public static SaveState getInstance(String user) {
        FileInputStream f_in = null;
        try {
            //File file = new File(MainActivity.activity.getFilesDir(), Encrypt.encrypt(user) + ".SaveState");
            File file = new File(MainActivity.activity.getFilesDir(), user + ".SaveState");
            if(file.exists()){
                f_in = new FileInputStream(file);
            }
            //f_in = new FileInputStream(Encrypt.encrypt(user)+".SaveState");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ObjectInputStream obj_in = null;
        SaveState s = null;
        if (f_in != null) {
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
        if (s == null) {
            s = new SaveState(user);
        }
        s.createLastUser();
        try {
            s.lastUser.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    private void createLastUser() {
        this.lastUser = new LastUser();
    }

    /**
     * Adds tracker to users information and then saves everything to the memory
     *
     * @param t Tracker to add for user
     * @return ArrayList containing all of the users Trackers
     */
    public ArrayList<Tracker> addTracker(Tracker t) {
        trackers.add(t);
        t.parentSaveState = this;
        System.out.println("meh");
        try {
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trackers;
    }

    /**
     * @return Returns all the users trackers
     */
    public ArrayList<Tracker> getTrackers() {
        return trackers;
    }

    /**
     * Removes Tracker from users infromation and then saves everything
     *
     * @param t Tracker to remove
     * @return ArrayList containing all of the users Trackers
     */
    public ArrayList<Tracker> removeTracker(Tracker t) {
        Iterator<Tracker> iterator = trackers.iterator();
        while (iterator.hasNext()) {
            Tracker current = iterator.next();
            if (current == t) {
                iterator.remove();
                t.parentSaveState = null;
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

    /**
     * Saves users current information
     *
     * @throws Exception Everything that can go wrong
     */
    public void save() throws Exception {
        //File file = new File(MainActivity.activity.getFilesDir(), Encrypt.encrypt(user) + ".SaveState");
        File file = new File(MainActivity.activity.getFilesDir(), user + ".SaveState");
        FileOutputStream f_out = new FileOutputStream(file);
        //FileOutputStream f_out = new FileOutputStream(Encrypt.encrypt(user)+".SaveState");
        ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
        obj_out.writeObject(this);
        obj_out.close();
        System.out.println(user+"hehehiehiehiehei");
    }

    public static SaveState getLastUser() {
        FileInputStream f_in = null;
        try {
            File file = new File(MainActivity.activity.getFilesDir(), "LastUser.LastUser");
            f_in = new FileInputStream(file);
            //f_in = new FileInputStream(Encrypt.encrypt(user)+".SaveState");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ObjectInputStream obj_in = null;
        LastUser s = null;
        if (f_in != null) {
            try {
                obj_in = new ObjectInputStream(f_in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Object obj = obj_in.readObject();
                s = (LastUser) obj;
                obj_in.close();
            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String user = null;
        if (s == null) {
            Cursor c = MainActivity.activity.getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
            if(c.moveToFirst()){
                user = (c.getString(c.getColumnIndex("display_name")));
            }else{
                user="Default";
            }
            c.close();
        } else {
            try {
                user = s.getLastUser();
            } catch (Exception e) {
                //e.printStackTrace();
                Cursor c = MainActivity.activity.getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
                if(c.moveToFirst()){
                    user = (c.getString(c.getColumnIndex("display_name")));
                }else{
                    user="Default";
                }
                c.close();
            }
        }
        System.out.println("olen tunniste "+user);
        return getInstance(user);
    }

    public class LastUser implements java.io.Serializable {
        String lastUser;

        String getLastUser() throws Exception {
            return Encrypt.decrypt(lastUser);
        }

        void save(String lastUser) throws Exception {
            this.lastUser = Encrypt.encrypt(lastUser);
            File file = new File(MainActivity.activity.getFilesDir(), "LastUser.LastUser");
            FileOutputStream f_out = new FileOutputStream(file);
            //FileOutputStream f_out = new FileOutputStream(Encrypt.encrypt(user)+".SaveState");
            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(new LastUser());
            obj_out.close();
        }
    }

    public class Users implements java.io.Serializable{
        List<ArrayList<String>> UserFiles=new ArrayList<ArrayList<String>>(0);
        int fileId=0;
        public String getFileName(String user){
            try {
                user=Encrypt.encrypt(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for(int i=0;i<UserFiles.size();i++){
                if(UserFiles.get(i).get(1).equals(user)){
                    return UserFiles.get(i).get(0);
                }
            }
            return null;
        }

        public String addUserFile(String user){
            ArrayList<String>temp=new ArrayList<String>(2);
            fileId++;
            temp.add(0,fileId+"");
            try {
                temp.add(1,Encrypt.encrypt(user));
            } catch (Exception e) {
                e.printStackTrace();
            }

            UserFiles.add(temp);
            return fileId+"";
        }
    }
}
