package tech.beepbeep.beept05;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifTextView;

public class LoadingActivity extends AppCompatActivity {

    private GifTextView loadingGif;
    private ImageView beepLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        beepLogo = findViewById(R.id.logo2);
        loadingGif = findViewById(R.id.logo3);
        beepLogo.getLayoutParams().height = height / 3;
        run();
    }

    private void run() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(LoadingActivity.this, MainActivity.class);
                LoadingActivity.this.startActivity(mainIntent);
                LoadingActivity.this.finish();
            }
        }, 5000);
    }

}