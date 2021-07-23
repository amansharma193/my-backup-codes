package com.transporteruser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkUtility  {
    public static boolean checkInternetConnection(Context context){
        boolean connection=false;
        ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
        if (activeNetwork!=null){
            connection=true;
        }
        else {
            Toast.makeText(context,"Check Internet Connection",Toast.LENGTH_SHORT).show();
        }
        return connection;
    }
}
