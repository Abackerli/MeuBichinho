package com.example.meubichinho.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.meubichinho.R;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(MainActivity.this, FamilyTreeActivity.class));

    }
}
