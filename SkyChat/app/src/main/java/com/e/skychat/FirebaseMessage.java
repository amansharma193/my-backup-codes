package com.e.skychat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMessage extends FirebaseMessagingService {
    SharedPreferences sp = null;
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        updateToken(s);
    }

    private void updateToken(String token) {

      FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
      if(currentUser!=null){
          String currentUserId = currentUser.getUid();
          HashMap<String,Object> hm = new HashMap<>();
          hm.put("token",token);
          FirebaseDatabase.getInstance().getReference("Users")
                  .child(currentUserId).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful())
                     Toast.makeText(FirebaseMessage.this, "Token Updated", Toast.LENGTH_SHORT).show();
              }
          });
      }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("Tag======>",""+remoteMessage.getData());
        Map<String,String>map = remoteMessage.getData();
        String title = map.get("title");
        String description = map.get("body");
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "Test channel";
        String channelName = "Test";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder nb = new NotificationCompat.Builder(getApplicationContext(),channelId);
        nb.setContentTitle(title);
        nb.setContentText(description);
        nb.setSmallIcon(R.mipmap.ic_launcher);
        manager.notify(1,nb.build());
    }
}
