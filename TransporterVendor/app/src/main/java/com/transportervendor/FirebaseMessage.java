package com.transportervendor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class FirebaseMessage extends FirebaseMessagingService implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    TextToSpeech tts;
    String title,description;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String>map = remoteMessage.getData();
        title = map.get("title");
        description = map.get("body");
        tts=new TextToSpeech(getApplicationContext(), this);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "Test channel";
        String channelName = "Test";
        Intent in=new Intent(getApplicationContext(),HomeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
                0, in, 0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder nb = new NotificationCompat.Builder(getApplicationContext(),channelId);
        nb.setContentTitle(title);
        nb.setContentText(description);
        nb.setContentIntent(pi);
        nb.setSmallIcon(R.mipmap.ic_launcher);
        manager.notify(1,nb.build());
    }

    @Override
    public void onInit(int status) {
        tts.speak(title+" "+description,TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        tts.shutdown();
        tts = null;
    }
}
