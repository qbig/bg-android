package sg.com.bigspoon.www.data;

import java.util.ArrayList;

import android.widget.TextView;

public class ThreadSafeSingleton {
	
	//This singleton stores the global variables
	 
    private static ThreadSafeSingleton instance;
	public int totalOrderNumber=0;
	public TextView corner;
	
	//The three parameters below are for Global New Items
	public ArrayList<String> number = new ArrayList<String>();
	public ArrayList<String> itemname = new ArrayList<String>();
	public ArrayList<String> price = new ArrayList<String>();
     
    private ThreadSafeSingleton(){}
     
    public static synchronized ThreadSafeSingleton getInstance(){
        if(instance == null){
            instance = new ThreadSafeSingleton();
        }
        return instance;
    }
     
}