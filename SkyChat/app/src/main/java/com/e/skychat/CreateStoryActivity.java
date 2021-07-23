package com.e.skychat;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class CreateStoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    private static final int PROGRESS_COUNT = 3;

    private StoriesProgressView storiesProgressView;
    private ImageView image;

    private int counter = 0;
    String resources[] = {"https://i.pinimg.com/originals/ce/5f/53/ce5f53437e291c48705428721406985c.jpg",
            "https://cbsnews2.cbsistatic.com/hub/i/r/2010/12/03/79ed9c0e-a644-11e2-a3f0-029118418759/thumbnail/1200x630/cfe704cb9840686e553d1ae1197d1466/365073.jpg",
            "https://firebasestorage.googleapis.com/v0/b/firechat-2cfae.appspot.com/o/Profile%20Images%2FGv7nnkg2N4PVcJ268Js9JSbJeQG2.jpg?alt=media&token=52a18cb2-9d39-45fc-baff-a384547f18be"
    };;

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
        setContentView(R.layout.activity_create_story);

        storiesProgressView =  findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(3);
        storiesProgressView.setStoryDuration(3000L);
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(this);
//        storiesProgressView.startStories();
        counter = 0;
        storiesProgressView.startStories(counter);

        image = (ImageView) findViewById(R.id.image);
        //image.setImageResource(resources[counter]);
        Picasso.get().load(resources[counter]).into(image);
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

    @Override
    public void onNext() {
        //image.setImageResource(resources[++counter]);
        Picasso.get().load(resources[++counter]).into(image);

    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        //image.setImageResource(resources[--counter]);
        Picasso.get().load(resources[--counter]).into(image);

    }

    @Override
    public void onComplete() {
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}
