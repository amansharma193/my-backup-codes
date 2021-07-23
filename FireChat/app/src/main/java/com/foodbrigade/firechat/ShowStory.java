package com.foodbrigade.firechat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.operation.Merge;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class ShowStory extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    private static int PROGRESS_COUNT = 0;

    private StoriesProgressView storiesProgressView;
    private ImageView image;
    TextView tvcaption;
    private int counter = 0;
    private ArrayList<Story> resources ;
    String userkey;
    DatabaseReference rootReference;
    EmojiconTextView txtstatus;
    private final long[] durations = new long[]{
            500L, 1000L, 1500L, 4000L, 5000L, 1000,
    };
    Merge merge;
    long pressTime = 0L;
    VideoView video;
    long limit = 500L;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show_story);
        image =  findViewById(R.id.image);
        Intent in=getIntent();
        tvcaption=findViewById(R.id.tvcaption);
        video=findViewById(R.id.video);
        userkey= in.getStringExtra("user");
        txtstatus=findViewById(R.id.txtstatus);
        rootReference= FirebaseDatabase.getInstance().getReference();
        rootReference.child("Stories").child(userkey).addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    resources=new ArrayList<>();
                    PROGRESS_COUNT= (int) snapshot.getChildrenCount();
                    Iterator<DataSnapshot> itr= snapshot.getChildren().iterator();
                    while(itr.hasNext()){
                        DataSnapshot ds=itr.next();
                        Story story=ds.getValue(Story.class);
                        resources.add(story);
                    }
                    storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
                    storiesProgressView.setStoriesCount(PROGRESS_COUNT);
                    storiesProgressView.setStoryDuration(5000L);
                    // or
                    // storiesProgressView.setStoriesCountWithDurations(durations);
                    storiesProgressView.setStoriesListener(ShowStory.this);
//        storiesProgressView.startStories();
                    counter = 0;
                    storiesProgressView.startStories(counter);
                    // bind reverse view
                    View reverse = findViewById(R.id.reverse);
                    reverse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            storiesProgressView.reverse();
                        }
                    });
                    reverse.setOnTouchListener(onTouchListener);

                    // bind skip view
                    View skip = findViewById(R.id.skip);
                    skip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            storiesProgressView.skip();
                        }
                    });
                    skip.setOnTouchListener(onTouchListener);
                    Story story=resources.get(counter);
                    if (story.getType().equalsIgnoreCase("image")){
                        image.setVisibility(View.VISIBLE);
                        video.setVisibility(View.GONE);
                        txtstatus.setVisibility(View.GONE);
                        storiesProgressView.setStoryDuration(5000L);
                        Picasso.get().load(story.getUrl()).into(image);
                        if (!resources.get(counter).getText().equalsIgnoreCase("")){
                            tvcaption.setVisibility(View.VISIBLE);
                            tvcaption.setText(resources.get(counter).getText());
                        }
                        else{
                            tvcaption.setVisibility(View.GONE);
                        }
                    }
                    else if (story.getType().equalsIgnoreCase("video")){
                        video.setVisibility(View.VISIBLE);
                        image.setVisibility(View.GONE);
                        txtstatus.setVisibility(View.GONE);
                        tvcaption.setTextColor(R.color.black);
                        video.setVideoURI(Uri.parse(story.getUrl()));
                        storiesProgressView.setStoryDuration(30000L);
                        video.requestFocus();
                        video.start();
                        if (!resources.get(counter).getText().equalsIgnoreCase("")){
                            tvcaption.setVisibility(View.VISIBLE);
                            tvcaption.setTextColor(R.color.black);
                            tvcaption.setText(resources.get(counter).getText());
                        }
                        else{
                            tvcaption.setVisibility(View.GONE);
                        }
                    }
                    else if (story.getType().equalsIgnoreCase("text")){
                        txtstatus.setVisibility(View.VISIBLE);
                        txtstatus.setText(story.getText());
                        tvcaption.setVisibility(View.GONE);
                        video.setVisibility(View.GONE);
                        image.setVisibility(View.GONE);
                        storiesProgressView.setStoryDuration(5000L);
                        if (!resources.get(counter).getText().equalsIgnoreCase("")){
                            tvcaption.setVisibility(View.VISIBLE);
                            tvcaption.setText(resources.get(counter).getText());
                        }
                        else{
                            tvcaption.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onNext() {
        if ((counter +1 ) <resources.size()) {
            Story story=resources.get(++counter);
            if (story.getType().equalsIgnoreCase("image")){
                image.setVisibility(View.VISIBLE);
                video.setVisibility(View.GONE);
                txtstatus.setVisibility(View.GONE);
                storiesProgressView.setStoryDuration(5000L);
                Picasso.get().load(story.getUrl()).into(image);
                if (!resources.get(counter).getText().equalsIgnoreCase("")){
                    tvcaption.setVisibility(View.VISIBLE);
                    tvcaption.setText(resources.get(counter).getText());
                }
                else{
                    tvcaption.setVisibility(View.GONE);
                }
            }
            else if (story.getType().equalsIgnoreCase("video")){
                video.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                txtstatus.setVisibility(View.GONE);
                video.setVideoURI(Uri.parse(story.getUrl()));
                storiesProgressView.setStoryDuration(30000L);
                tvcaption.setTextColor(R.color.black);
                video.requestFocus();
                video.start();
                if (!resources.get(counter).getText().equalsIgnoreCase("")){
                    tvcaption.setVisibility(View.VISIBLE);
                    tvcaption.setTextColor(R.color.black);
                    tvcaption.setText(resources.get(counter).getText());
                }
                else{
                    tvcaption.setVisibility(View.GONE);
                }
            }
            else if (story.getType().equalsIgnoreCase("text")){
                txtstatus.setVisibility(View.VISIBLE);
                txtstatus.setText(story.getText());
                tvcaption.setVisibility(View.GONE);
                video.setVisibility(View.GONE);
                image.setVisibility(View.GONE);
                if (!resources.get(counter).getText().equalsIgnoreCase("")){
                    tvcaption.setVisibility(View.VISIBLE);
                    tvcaption.setText(resources.get(counter).getText());
                }
                else{
                    tvcaption.setVisibility(View.GONE);
                }
                storiesProgressView.setStoryDuration(5000L);
            }

        }
        else
            finish();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onPrev() {
        if ((counter - 1) >= 0) {
            Story story=resources.get(--counter);
            if (story.getType().equalsIgnoreCase("image")){
                image.setVisibility(View.VISIBLE);
                video.setVisibility(View.GONE);
                txtstatus.setVisibility(View.GONE);
                storiesProgressView.setStoryDuration(5000L);
                Picasso.get().load(story.getUrl()).into(image);
                if (!resources.get(counter).getText().equalsIgnoreCase("")){
                    tvcaption.setVisibility(View.VISIBLE);
                    tvcaption.setText(resources.get(counter).getText());
                }
                else{
                    tvcaption.setVisibility(View.GONE);
                }
            }
            else if (story.getType().equalsIgnoreCase("video")){
                video.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                txtstatus.setVisibility(View.GONE);
                video.setVideoURI(Uri.parse(story.getUrl()));
                video.requestFocus();
                storiesProgressView.setStoryDuration(30000L);
                tvcaption.setTextColor(R.color.black);
                video.start();
                storiesProgressView.setBackgroundColor(R.color.colorPrimaryDark);
                if (!resources.get(counter).getText().equalsIgnoreCase("")){
                    tvcaption.setVisibility(View.VISIBLE);
                    tvcaption.setTextColor(R.color.black);
                    tvcaption.setText(resources.get(counter).getText());
                }
                else{
                    tvcaption.setVisibility(View.GONE);
                }
            }
            else if (story.getType().equalsIgnoreCase("text")){
                txtstatus.setVisibility(View.VISIBLE);
                txtstatus.setText(story.getText());
                tvcaption.setVisibility(View.GONE);
                video.setVisibility(View.GONE);
                if (!resources.get(counter).getText().equalsIgnoreCase("")){
                    tvcaption.setVisibility(View.VISIBLE);
                    tvcaption.setText(resources.get(counter).getText());
                }
                else{
                    tvcaption.setVisibility(View.GONE);
                }
                image.setVisibility(View.GONE);
                storiesProgressView.setStoryDuration(5000L);
            }
        }
        else
            finish();
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}
