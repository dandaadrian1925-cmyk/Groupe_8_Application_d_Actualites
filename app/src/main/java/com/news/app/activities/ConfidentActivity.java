package com.news.app.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.news.app.R;

public class ConfidentActivity extends AppCompatActivity {

    private ImageView btnBackConfident;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confident);

        btnBackConfident = findViewById(R.id.btnBackConfident);

        btnBackConfident.setOnClickListener(v -> finish());
    }
}
