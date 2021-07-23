package com.transportervendor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.transportervendor.adapter.ChatAdapter;
import com.transportervendor.apis.UserService;
import com.transportervendor.beans.Message;
import com.transportervendor.beans.Transporter;
import com.transportervendor.beans.User;
import com.transportervendor.databinding.ChatActivityBinding;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    ChatActivityBinding binding;
    String userId,token;
    DatabaseReference firebaseDatabase;
    ChatAdapter adapter;
    ArrayList<Message> al;

    private boolean checkLanguage() {
        SharedPreferences mprefs = getSharedPreferences("Transporter", MODE_PRIVATE);
        String s = mprefs.getString("language", "");
        if (s.equalsIgnoreCase("hindi")) {
            return true;
        }
        return false;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatActivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Intent in = getIntent();
        userId = (String) in.getCharSequenceExtra("id");
        UserService.UserApi userApi = UserService.getUserApiInstance();
        Call<User> call = userApi.getUser(userId);
        if (NetworkUtil.getConnectivityStatus(this)) {
            String s="Please wait...";
            if (checkLanguage()){
                s="कृपया प्रतीक्षा करें...";
            }
            final CustomProgressDialog pd=new CustomProgressDialog(ChatActivity.this,s);
            pd.setCancelable(true);
            pd.setTitle("Loading...");
            pd.show();
            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    pd.dismiss();
                    if (response.code() == 200) {
                        getSupportActionBar().setTitle(response.body().getName());
                        token=response.body().getToken();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(ChatActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        binding.mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    binding.mic.setImageResource(R.drawable.mic);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                binding.etmsg.setText("");
                binding.mic.setImageResource(R.drawable.mic_yellow);
                binding.etmsg.setHint("Listening...");
            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                binding.mic.setImageResource(R.drawable.mic);
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                binding.etmsg.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        binding.rv.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        binding.btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etmsg = binding.etmsg.getText().toString();
                if (etmsg.isEmpty()) {
                    binding.etmsg.setError("this field can't be empty.");
                    return;
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sd = new SimpleDateFormat("MMM dd, yyyy");
                String date = sd.format(c.getTime());
                sd = new SimpleDateFormat("hh:mm a");
                String time = sd.format(c.getTime());
                if (NetworkUtil.getConnectivityStatus(ChatActivity.this)) {
                    final String id = FirebaseDatabase.getInstance().getReference().push().getKey();
                    final Message message = new Message(etmsg, date, time, FirebaseAuth.getInstance().getCurrentUser().getUid(), id, userId, System.currentTimeMillis());
                    firebaseDatabase.child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userId).child(id).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseDatabase.child("Messages").child(userId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            try {
                                                RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);
                                                String url = "https://fcm.googleapis.com/fcm/send";
                                                SharedPreferences shared = getSharedPreferences("Transporter", Context.MODE_PRIVATE);
                                                String json = shared.getString("Transporter", "");
                                                Gson gson = new Gson();
                                                Transporter transporter = gson.fromJson(json, Transporter.class);
                                                JSONObject data = new JSONObject();
                                                data.put("title", "New Message");
                                                data.put("body", "From : " + transporter.getName());
                                                JSONObject notification_data = new JSONObject();
                                                notification_data.put("data", data);
                                                notification_data.put("to", token);
                                                JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new com.android.volley.Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        Toast.makeText(ChatActivity.this, "Message sent.", Toast.LENGTH_SHORT).show();
                                                        binding.etmsg.setText("");
                                                    }
                                                }, new com.android.volley.Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.e("spanshoe", "error..." + error.getMessage());
                                                        Toast.makeText(ChatActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }) {
                                                    @Override
                                                    public Map<String, String> getHeaders() {
                                                        String api_key_header_value = "Key=AAAAWv788Wk:APA91bFW0Z_ISKSzu2ZD97ouIZde3jHsaKSvxLG2_adRdmaUCeQ5Jv88XpcNa2o06RruMbRIWF0gYgh6VPYknq-ELrXgIEmp3SVeu3YTH_2cVmEDUT3Jbg1u6N5OxsacPVIFKqkkBhyp";
                                                        Map<String, String> headers = new HashMap<>();
                                                        headers.put("Content-Type", "application/json");
                                                        headers.put("Authorization", api_key_header_value);
                                                        return headers;
                                                    }
                                                };
                                                queue.add(request);
                                            } catch (Exception e) {
                                                Toast.makeText(ChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else
                    Toast.makeText(ChatActivity.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();

            }
        });
        al = new ArrayList<>();
        firebaseDatabase.child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userId).orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Message msg = snapshot.getValue(Message.class);
                    al.add(msg);
                    Collections.sort(al);
                    adapter = new ChatAdapter(ChatActivity.this, al);
                    binding.rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    binding.rv.smoothScrollToPosition(adapter.getItemCount());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
