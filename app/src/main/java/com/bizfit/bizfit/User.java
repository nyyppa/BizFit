package com.bizfit.bizfit;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class User implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8425799364006222365L;
    ArrayList<Tracker> trackers;
    public String userName;
    LastUser lastUser;
    private transient static File saveDir;
    private transient static Context context;
    private transient static User currentUser;

    public Tracker getTrackerByIndex(int index){
        return trackers.get(index);
    }


    public static void update(Context c){
        User u;
        context=c;
        u=getLastUser();
        for(int i=0;i<u.trackers.size();i++){
            u.trackers.get(i).update();
        }
    }
    /**
     * Do not manually construct new saveStates, rather call User.getInstance(String userName)
     *
     * @param userName User NAME
     */
    User(String userName) {
        this.userName = userName;
        if (trackers == null) {
            trackers = new ArrayList<Tracker>(0);
        }
    }

    /**
     * Checks if userName has already Tracker with given NAME
     * @param name  NAME to check
     * @return  true if tracker is found and false if tracker is not found
     */
    public boolean isTrackerNameAlreadyInUse(String name){
        for(int i=0;i<trackers.size();i++){
            if(name.equals(trackers.get(i).getName())){
                return true;
            }
        }
        return false;
    }

    public static List<String> getUsers() {
        String[] s = MainActivity.activity.getFilesDir().list();
        List<String> users = new ArrayList<String>(0);
        for (String m : s) {
            if (m.endsWith(".User")) {
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
     * encrypts userName NAME before saving it to memory
     *
     * @param out
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        try {
            userName = Encrypt.encrypt(userName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out.defaultWriteObject();
        try {
            userName =Encrypt.decrypt(userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * decrypts userName NAME when reading it from memory
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            userName = Encrypt.decrypt(userName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    /**
     * If userName has User reads it from memory and returns it, if no data
     * is found for userName it constructs new User for that userName
     * and returns it
     *
     * @param user User NAME
     * @return User for the userName
     */
    public static User getInstance(String user) {
        Users users=loadUsers();
        FileInputStream f_in = null;
        try {
            //File file = new File(MainActivity.activity.getFilesDir(), Encrypt.encrypt(userName) + ".User");
            File file = new File(context.getFilesDir(), users.getFileName(user) + ".User");
            if(file.exists()){
                f_in = new FileInputStream(file);
            }
            //f_in = new FileInputStream(Encrypt.encrypt(userName)+".User");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ObjectInputStream obj_in = null;
        User s = null;
        if (f_in != null) {
            try {
                obj_in = new ObjectInputStream(f_in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Object obj = obj_in.readObject();
                s = (User) obj;
                obj_in.close();
            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (s == null) {
            s = new User(user);
        }
        s.createLastUser();
        try {
            s.lastUser.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public SortedTrackers getTimeRemainingSortedTrackers(boolean ascending){
        final int asc=ascending?1:-1;
        SortedTrackers sorted=new SortedTrackers();
        Comparator<Tracker> t=new Comparator<Tracker>() {
            @Override
            public int compare(Tracker lhs, Tracker rhs) {
                return asc*(lhs.getRemainingTimeMillis()-rhs.getRemainingTimeMillis());
            }
        };
        Collections.sort(sorted.currentTrackers, t);
        Collections.sort(sorted.expiredTrackers, t);
        return sorted;
    }

    public SortedTrackers getProgressComparedToTimeSortedTrackers(boolean ascending){
        final int asc=ascending?1:-1;
        SortedTrackers sorted=new SortedTrackers();
        Comparator<Tracker> t=new Comparator<Tracker>() {
            @Override
            public int compare(Tracker lhs, Tracker rhs) {
                return asc*((int)(lhs.getProgressComperedToTime()*100)-(int)(rhs.getProgressComperedToTime()*100));
            }
        };
        Collections.sort(sorted.currentTrackers, t);
        Collections.sort(sorted.expiredTrackers, t);
        return sorted;
    }

    public SortedTrackers getAlpapheticalSortedTrackers(boolean ascending){
        final int asc=ascending?1:-1;
        SortedTrackers sorted=new SortedTrackers();
        Comparator<Tracker> t=new Comparator<Tracker>() {
            @Override
            public int compare(Tracker lhs, Tracker rhs) {
                return asc*(lhs.getName().compareToIgnoreCase(rhs.getName()));
            }
        };
        Collections.sort(sorted.currentTrackers, t);
        Collections.sort(sorted.expiredTrackers, t);
        return sorted;
    }

    private void createLastUser() {
        this.lastUser = new LastUser();
    }

    /**
     * Adds tracker to users information and then saves everything to the memory
     *
     * @param t Tracker to add for userName
     * @return ArrayList containing all of the users Trackers
     */
    public ArrayList<Tracker> addTracker(Tracker t) {
        trackers.add(t);
        t.parentUser = this;
        try {
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trackers;
    }
    private void updateIndexes(){
        for(int i=0;i<trackers.size();i++){
            trackers.get(i).index=i;
        }
    }
    /**
     * @return Returns all the users trackers
     */
    public Tracker[] getTrackers() {
        Tracker[] t=new Tracker[trackers.size()];
        return trackers.toArray(t);
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
                t.parentUser = null;
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
        //File file = new File(MainActivity.activity.getFilesDir(), Encrypt.encrypt(userName) + ".User");
        if(saveDir==null){
            saveDir=context.getFilesDir();
        }
        File file = new File(saveDir, loadUsers().getFileName(userName) + ".User");
        FileOutputStream f_out = new FileOutputStream(file);
        //FileOutputStream f_out = new FileOutputStream(Encrypt.encrypt(userName)+".User");
        ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
        obj_out.writeObject(this);
        obj_out.close();
        //lastUser.save(userName);
    }
    private static Users loadUsers(){
        FileInputStream f_in = null;
        try {
            File file = new File(context.getFilesDir(), "Users.Users");
            f_in = new FileInputStream(file);
            //f_in = new FileInputStream(Encrypt.encrypt(userName)+".User");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ObjectInputStream obj_in = null;
        Users s = null;
        if (f_in != null) {
            try {
                obj_in = new ObjectInputStream(f_in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Object obj = obj_in.readObject();
                s = (Users) obj;
                obj_in.close();
            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(s==null){
            s=new Users();
        }
        return s;
    }
    public static User getLastUser() {
        if(currentUser!=null){
            return currentUser;
        }
        FileInputStream f_in = null;
        if(MainActivity.activity!=null){
            context=MainActivity.activity;
        }
        try {
            File file = new File(context.getFilesDir(), "LastUser.LastUser");
            f_in = new FileInputStream(file);
            //f_in = new FileInputStream(Encrypt.encrypt(userName)+".User");
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
            final AccountManager manager = AccountManager.get(context);
            final Account[] accounts = manager.getAccountsByType("com.google");
            final int size = accounts.length;
            String[] names = new String[size];
            for (int i = 0; i < size; i++) {
                names[i] = accounts[i].name;

            }
            if(names.length>0){
                user=names[0];
            }else{
                user="default";
            }
        } else {
            try {
                user = s.getLastUser();
            } catch (Exception e) {
                e.printStackTrace();
                final AccountManager manager = AccountManager.get(context);
                final Account[] accounts = manager.getAccountsByType("com.google");
                final int size = accounts.length;
                String[] names = new String[size];
                for (int i = 0; i < size; i++) {
                    names[i] = accounts[i].name;

                }
                if(names.length>0){
                    user=names[0];
                }else{
                    user="default";
                }

            }
        }
        User currentUser=getInstance(user);
        return currentUser;
    }

    public class LastUser implements java.io.Serializable {
        String lastUser;

        String getLastUser() throws Exception {
            return Encrypt.decrypt(lastUser);
        }

        void save(String lastUser) throws Exception {
            if(saveDir==null){
                saveDir=context.getFilesDir();
            }
            this.lastUser = Encrypt.encrypt(lastUser);
            File file = new File(saveDir, "LastUser.LastUser");
            FileOutputStream f_out = new FileOutputStream(file);
            //FileOutputStream f_out = new FileOutputStream(Encrypt.encrypt(userName)+".User");
            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(this);
            obj_out.close();
        }
    }

    public static class  Users implements java.io.Serializable{
        List<ArrayList<String>> UserFiles=new ArrayList<ArrayList<String>>(0);
        int fileId=0;
        public void save()throws Exception {
            if(saveDir==null){
                saveDir=context.getFilesDir();
            }
            File file = new File(saveDir, "Users.Users");
            FileOutputStream f_out = new FileOutputStream(file);
            //FileOutputStream f_out = new FileOutputStream(Encrypt.encrypt(userName)+".User");
            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(this);
            obj_out.close();
        }
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
            return addUserFile(user);
        }

        private String addUserFile(String user){
            ArrayList<String>temp=new ArrayList<String>(2);
            fileId++;
            temp.add(0,fileId+"");
            try {
                temp.add(1,user);
            } catch (Exception e) {
                e.printStackTrace();
            }

            UserFiles.add(temp);
            try {
                save();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fileId+"";
        }

    }

    public class SortedTrackers{
        public List<Tracker>currentTrackers=new ArrayList<Tracker>(0);
        public List<Tracker>expiredTrackers=new ArrayList<Tracker>(0);
        SortedTrackers(){
            for(int i=0;i<trackers.size();i++){
                if(trackers.get(i).completed){
                    expiredTrackers.add(trackers.get(i));
                }else {
                    currentTrackers.add(trackers.get(i));
                }
            }
        }
    }
}
