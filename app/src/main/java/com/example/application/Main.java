package com.example.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.example.application.activities.MainActivity;
import com.example.application.utils.DatabaseHandler;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

//        DatabaseHandler db = new DatabaseHandler(this);
//        db.tagDetailsToCharger("1", "123", "Mock Data");
//        db.dropTable();
//        this.deleteDatabase("ChargerManager");

        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
