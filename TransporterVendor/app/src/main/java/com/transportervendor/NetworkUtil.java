package com.transportervendor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public static boolean getConnectivityStatus(Context context){
        boolean connection=false;
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
        if(activeNetwork!=null){
            connection=true;
        }
        return connection;
    }
}
