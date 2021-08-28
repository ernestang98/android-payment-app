package com.example.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.example.application.activities.MainActivity;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
