package com.transportervendor;

import com.transportervendor.beans.State;

import java.util.ArrayList;

public class Filter {
    public static ArrayList<String>al;
    public static ArrayList<String> getInstance(){
        if(al==null){
            al=new ArrayList<String>();
        }
        return al;
    }
}
