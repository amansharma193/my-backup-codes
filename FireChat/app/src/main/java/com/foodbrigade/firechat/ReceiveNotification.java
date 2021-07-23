package com.foodbrigade.firechat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class ReceiveNotification extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        updateToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String>map=remoteMessage.getData();
        String title=map.get("title");
        String desc=map.get("body");
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String channelid="My channel";
        String channelName="My channel";
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(channelid,channelName,NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder nb=new NotificationCompat.Builder(this,channelid);
        nb.setContentTitle(title);
        nb.setContentText(desc);
        Log.e("spanshoe","  "+channelid+"   "+channelName+"  "+map);
        nb.setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1,nb.build());
    }

    private void updateToken(String token) {
        String userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootReference= FirebaseDatabase.getInstance().getReference("Users");
        HashMap<String ,Object>hm=new HashMap<>();
        hm.put("token",token);
        rootReference.child(userkey).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(ReceiveNotification.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
