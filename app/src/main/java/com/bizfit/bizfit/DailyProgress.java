package com.bizfit.bizfit;

import com.bizfit.bizfit.utils.OurDateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

/**
 * Contains users progress in single tracking period
 */
public class DailyProgress implements java.io.Serializable {
    private List<DayPool> dayPool = new ArrayList<DayPool>(0);
    int id;

    /**
     * Constructs new DailyProgress from given list of daySingles and DailyProgress ID
     * @param list List containing DaySingles that DailyProgress will hold
     * @param id    ID of DailyProgress
     */
    public DailyProgress(List<DaySingle> list, int id) {
        Comparator<DaySingle> comparator = new Comparator<DaySingle>() {
            @Override
            public int compare(DaySingle lhs, DaySingle rhs) {
                return (int) (lhs.getTime() - rhs.getTime());
            }
        };
        Collections.sort(list, comparator);
        for (int i = 0; i < list.size(); i++) {
            DaySingle single=addDailyProgress(list.get(i).getAmount(), list.get(i).getTime());
            single.id=list.get(i).id;
        }
        this.id = id;
    }

    public DailyProgress(User user) {
        id=user.getNextFreeDailyProgressID();

    }

    /**
     * Default constructor
     */
    public DailyProgress() {

    }

    /**
     * Creates DaySingle from given information and returns it without saving it to anywhere
     * @param time
     * @param amount
     * @return
     */
    public DaySingle createDaySingle(long time, float amount) {
        return new DaySingle(time, amount);
    }

    /**
     * Creates DailyProgress from given JSON
     * @param jsonObject JSON containing nessessary information
     */
    public DailyProgress(JSONObject jsonObject) {
        try {
            JSONArray jsonArray=jsonObject.getJSONArray("DaySingle");
            id=jsonObject.getInt("id");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject object=jsonArray.getJSONObject(i);
                DaySingle d=addDailyProgress((float)(object.getDouble("amount")),object.getLong("time"));
                d.id=object.getInt("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts DailyProgress to JSON
     * @return  JSON containing all information from DailyProgress
     */
    public JSONObject toJSon(){
        JSONObject jsonObject=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        for (int i = 0; i < dayPool.size(); i++) {
            List<DaySingle> daySingleList = dayPool.get(i).daySingle;
            for (int j = 0; j < daySingleList.size(); j++) {
                jsonArray.put(daySingleList.get(j).ToJson());
            }
        }

        try {
            jsonObject.put("id",id);
            jsonObject.put("DaySingle",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    /**
     * Converts DailyProgress to easier saving to SQL based database
     * @return List containing all DaySingles in this DailyProgress sorted for creation time
     */
    public List<DaySingle> prepForDataBase() {
        List<DaySingle> list = new ArrayList<DaySingle>();
        for (int i = 0; i < dayPool.size(); i++) {
            List<DaySingle> daySingleList = dayPool.get(i).daySingle;
            for (int j = 0; j < daySingleList.size(); j++) {
                list.add(daySingleList.get(j));
            }
        }
        Comparator<DaySingle> comparator = new Comparator<DaySingle>() {
            @Override
            public int compare(DaySingle lhs, DaySingle rhs) {
                return (int) (lhs.getTime() - rhs.getTime());
            }
        };
        Collections.sort(list, comparator);
        return list;
    }

    /**
     *
     * @return DayPool list containing users added progress
     */
    public List<DayPool> getDayPoolList() {
        return dayPool;
    }

    /**
     * Adds progress for user and returns daySingle that was created as the result
     * @param amount    How much user addded progress
     * @param time  When user added progress
     * @return  DaySingle created from given information
     */
    public DaySingle addDailyProgress(float amount, long time) {
        if (dayPool.size() == 0) {
            dayPool.add(new DayPool(amount, time));
        } else if (dayPool.get(dayPool.size() - 1).sameDate(time)) {
            dayPool.get(dayPool.size() - 1).addDaySingle(amount, time);
        } else {
            dayPool.add(new DayPool(amount, time));
        }
        return dayPool.get(dayPool.size() - 1).getLast();
    }

    /**
     * undoes last time user added progress
     */
    public void undoLast() {
        if (dayPool.size() != 0) {
            boolean wasLast = dayPool.get(dayPool.size() - 1).removeLast();
            if (wasLast) {
                removeLast();
            }
        }
    }


    /**
     * removes lastDayPool from DailyProgress
     */
    private void removeLast() {
        ListIterator<DayPool> iterator = dayPool.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.remove();
    }


    /**
     * Container class for daySingle's
     *
     * Holds single days user added progress
     */
    public class DayPool implements java.io.Serializable {

        long time;
        List<DaySingle> daySingle = new ArrayList<DaySingle>(0);

        /**
         * Creates new DayPool and adds first daySingle with given information
         * @param amount    amount user added progress
         * @param time  time user added progress
         */
        DayPool(float amount, long time) {
            this.time = time;
            daySingle.add(new DaySingle(time,amount));
        }

        /**
         * returns last created DaySingle
         * @return
         */
        private DaySingle getLast(){
            return daySingle.get(daySingle.size()-1);
        }

        /**
         * adds new DaySingle from given information to DayPool
         * @param amount    amount user added progress
         * @param time  time when user added progress
         */
        public void addDaySingle(float amount, long time) {
            daySingle.add(new DaySingle(time, amount));
  ;
        }

        /**
         * Converts DayPools creation time to OurDateTime
         * @return OurDateTime made from DayPools creationTime
         */
        public OurDateTime getDayTime() {
            return new OurDateTime(time);
        }

        /**
         *
         * @return  Total amount of users added progress during single day for single tracker
         */
        public float getTotalAmount() {
            float TotalAmount=0;
            for(int i=0;i<daySingle.size();i++){
                TotalAmount+=daySingle.get(i).getAmount();
            }
            return TotalAmount;
        }

        /**
         *
         * @return Time when DayPool was created
         */
        public long getTime() {
            return time;
        }

        /**
         * Checks if given time's day of month is same as time's held in DayPool
         * @param timeTime  Time to Compare
         * @return  if day of month is same returns true, else false
         */
        private boolean sameDate(long timeTime) {
            GregorianCalendar ca = new GregorianCalendar();
            ca.setTimeInMillis(time);
            GregorianCalendar ba = new GregorianCalendar();
            ba.setTimeInMillis(timeTime);
            if (ca.get(Calendar.DAY_OF_MONTH) == ba.get(Calendar.DAY_OF_MONTH)) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * Removes last instance of user added progress
         * @return  if it was last instance in this DayPool
         */
        public boolean removeLast() {
            ListIterator<DaySingle> iterator = daySingle.listIterator();
            DaySingle a = null;
            while (iterator.hasNext()) {
                a = iterator.next();
            }
            iterator.remove();
            if (daySingle.size() == 0) {
                return true;
            }
            return false;

        }
    }

    /**
     * Holds single instance of users added progress
     */
    public class DaySingle implements java.io.Serializable {
        private long time;
        private float amount;
        public int id = -1;

        /**
         *
         * @param Time  Creation time
         * @param amount    Amount the user added progress
         */
        DaySingle(long Time, float amount) {
            // TODO Auto-generated constructor stub
            this.time = Time;
            this.amount = amount;
            //id=
        }

        /**
         * Retuns creation time of DaySingle
         * @return  Creation Time
         */
        public long getTime() {
            return time;
        }

        /**
         * Retuns amount held in this DaySingle
         * @return  Amount held in this DaySingle
         */
        public float getAmount() {
            return amount;
        }

        /**
         * Converts DaySingle to JSON
         * @return  JSON containing all of daySingles information
         */
        private JSONObject ToJson(){
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("time",time);
                jsonObject.put("amount",amount);
                jsonObject.put("id",id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

    }
}
