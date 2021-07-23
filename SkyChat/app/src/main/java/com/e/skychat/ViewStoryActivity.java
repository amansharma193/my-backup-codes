package com.e.skychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.e.skychat.beans.Stories;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class ViewStoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{



    private StoriesProgressView storiesProgressView;
    private ImageView image;

    private int counter = 0;
    private ArrayList<Stories> resources;
    TextView tvCaption;

    private final long[] durations = new long[]{
            500L, 1000L, 1500L, 4000L, 5000L, 1000,
    };

    long pressTime = 0L;
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
        setContentView(R.layout.activity_view_story);
        tvCaption = findViewById(R.id.tvCaption);
        Intent in = getIntent();
        resources = (ArrayList<Stories>) in.getSerializableExtra("storyList");
        if(resources != null) {
            storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);

            storiesProgressView.setStoriesCount(resources.size());

            storiesProgressView.setStoryDuration(3000L);

            storiesProgressView.setStoriesListener(this);

            counter = 0;
            storiesProgressView.startStories(counter);

            image = findViewById(R.id.image);

            Picasso.get().load(resources.get(counter).getImageUrl()).into(image);
            if(!resources.get(counter).getText().equals("")){
                tvCaption.setVisibility(View.VISIBLE);
                tvCaption.setText(resources.get(counter).getText());
            }
            else
                tvCaption.setVisibility(View.GONE);

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
        }
        else
            finish();
    }

    @Override
    public void onNext() {
        if((counter+1)<resources.size()){
            ++counter;
            Picasso.get().load(resources.get(counter).getImageUrl()).into(image);
            if(!resources.get(counter).getText().equals("")){
                tvCaption.setVisibility(View.VISIBLE);
                tvCaption.setText(resources.get(counter).getText());
            }
            else
                tvCaption.setVisibility(View.GONE);
        }

        else
            finish();
    }

    @Override
    public void onPrev() {
        if ((counter - 1) > 0) {
            --counter;
            if(!resources.get(counter).getText().equals("")){
                tvCaption.setVisibility(View.VISIBLE);
                tvCaption.setText(resources.get(counter).getText());
            }
            else
                tvCaption.setVisibility(View.GONE);
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

        super.onDestroy();
    }
}
