package com.news.app.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.news.app.R;

public class ConditionsActivity extends AppCompatActivity {

    private ImageView btnBackConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        btnBackConditions = findViewById(R.id.btnBackConditions);

        btnBackConditions.setOnClickListener(v -> finish());
    }
}
