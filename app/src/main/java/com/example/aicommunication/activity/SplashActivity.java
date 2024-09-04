package com.example.aicommunication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aicommunication.R;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class SplashActivity extends AppCompatActivity {
    int count = 0;
    ImageView imageView;
    int[] imageID = {
            R.drawable.splash01, R.drawable.splash02, R.drawable.splash03
    };
    int[] randomID = new int[imageID.length];
    private final Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (count > imageID.length - 1) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                SplashActivity.this.finish();
            } else {
                imageView.setImageResource(randomID[count]);
                count++;
                mHandler.postDelayed(mRunnable, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sp = getSharedPreferences("AIConnect",MODE_PRIVATE);
        if(!sp.getBoolean("splash",true)){
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        Set<Integer> set = new LinkedHashSet<>();
        Random random = new Random();
        while (set.size() < imageID.length) {
            int num = random.nextInt(imageID.length);
            set.add(num);
        }
        Iterator<Integer> it = set.iterator();
        for (int i = 0; i < 3; i++) {
            if (it.hasNext()) {
                int id = it.next();
                if (id >= 0 && id < imageID.length)
                    randomID[i] = imageID[id];
                else
                    randomID[i] = imageID[0];
            }
        }
        imageView = findViewById(R.id.imageSplash);
        mHandler.post(mRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

}