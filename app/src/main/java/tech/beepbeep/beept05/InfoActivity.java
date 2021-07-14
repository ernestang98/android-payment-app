package tech.beepbeep.beept05;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class InfoActivity extends AppCompatActivity {

    private TextView chargerName;
    private TextView chargerLocation;
    private TextView chargerPower;
    private TextView chargerPrice;
    private TextView chargerDescription;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();
        String jsonData = intent.getStringExtra("data");
        Gson gson = new Gson();
        ChargerObject chargerObject = gson.fromJson(jsonData, ChargerObject.class);
        System.out.println(chargerObject);

        chargerName = findViewById(R.id.textView3);
        chargerName.setText(chargerObject.getChargerName());

        chargerLocation = findViewById(R.id.textView4);
        chargerLocation.setText(chargerObject.getChargerLocation());

        chargerPower = findViewById(R.id.textView11);
        chargerPower.setText(chargerObject.getChargerPower());

        chargerPrice = findViewById(R.id.textView5);
        chargerPrice.setText("Price: " + chargerObject.getChargerPrice());

        chargerDescription = findViewById(R.id.textView10);
        chargerDescription.setText(chargerObject.getChargerDescription());
    }
}