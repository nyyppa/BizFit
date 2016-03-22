package com.bizfit.bizfit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;


public class DailyProgress implements java.io.Serializable{
	private List<DayPool>dayPool=new ArrayList<DayPool>(0);

	public DailyProgress(List<DaySingle> list){
        Comparator<DaySingle> comparator=new Comparator<DaySingle>() {
            @Override
            public int compare(DaySingle lhs, DaySingle rhs) {
                return (int) (lhs.getTime()-rhs.getTime());
            }
        };
        Collections.sort(list,comparator);
        for(int i=0;i<list.size();i++){
            addDailyProgress(list.get(i).getAmount(),list.get(i).getTime());
        }
	}
    public DailyProgress(){


    }

    public List<DaySingle> prepForDataBase(){
        List<DaySingle>list=new ArrayList<DaySingle>();
        for(int i=0;i<dayPool.size();i++){
            List<DaySingle> daySingleList=dayPool.get(i).daySingle;
            for(int j=0;j<daySingleList.size();j++){
                list.add(daySingleList.get(j));
            }
        }
        Comparator<DaySingle> comparator=new Comparator<DaySingle>() {
            @Override
            public int compare(DaySingle lhs, DaySingle rhs) {
                return (int) (lhs.getTime()-rhs.getTime());
            }
        };
        Collections.sort(list,comparator);
        return list;
    }

	public List<DayPool> getDayPoolList(){
		return dayPool;
	}

	public void addDailyProgress(float amount, long time){
		if(dayPool.size()==0){
			dayPool.add(new DayPool(amount, time));
		}else if(dayPool.get(dayPool.size()-1).sameDate(time)){
			dayPool.get(dayPool.size()-1).addDaySingle(amount, time);
		}else{
			dayPool.add(new DayPool(amount, time));
		}
	}

	public void undoLast(){
		if(dayPool.size()!=0){
			boolean wasLast=dayPool.get(dayPool.size()-1).removeLast();
			if(wasLast){
				removeLast();
			}
		}
	}

	private void removeLast(){
		ListIterator<DayPool>iterator=dayPool.listIterator();
		while (iterator.hasNext()){
			iterator.next();
		}
		iterator.remove();
	}


	public class DayPool implements java.io.Serializable{
		float TotalAmount;
		long time;
		List<DaySingle>daySingle=new ArrayList<DaySingle>(0);
		DayPool(float amount, long time){
			this.time=time;
			TotalAmount+=amount;
		}
		public void addDaySingle(float amount,long time){
			daySingle.add(new DaySingle(time,amount));
			TotalAmount+=amount;
		}
		public OurDateTime getDayTime(){
			return new OurDateTime(time);
		}
		public float getTotalAmount(){
			return TotalAmount;
		}
		public long getTime(){
			return time;
		}
		private boolean sameDate(long timeTime){
			GregorianCalendar ca=new GregorianCalendar();
			ca.setTimeInMillis(time);
			GregorianCalendar ba=new GregorianCalendar();
			ba.setTimeInMillis(timeTime);
			if(ca.get(Calendar.DAY_OF_MONTH)==ba.get(Calendar.DAY_OF_MONTH)){
				return true;
			}else{
				return false;
			}
		}

		public boolean removeLast(){
			ListIterator<DaySingle> iterator=daySingle.listIterator();
			DaySingle a=null;
			while(iterator.hasNext()){
				a=iterator.next();
			}
			TotalAmount-=a.getAmount();
			iterator.remove();
			if(daySingle.size()==0){
				return true;
			}
			return false;

		}
	}


	private class DaySingle implements java.io.Serializable{
		private long time;
		private float amount;
		DaySingle(long Time,float amount) {
		// TODO Auto-generated constructor stub
			this.time=Time;
			this.amount=amount;
		}
		public long getTime(){
			return time;
		}
		public float getAmount(){
			return amount;
		}

	}
}
