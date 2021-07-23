package com.transporterapi;

import java.io.*;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FirbaseIntialize {
	@PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = this.getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://transporter-vendor.firebaseio.com")
                    .setStorageBucket("transporter-vendor.appspot.com")
                    .build();
            if(FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
