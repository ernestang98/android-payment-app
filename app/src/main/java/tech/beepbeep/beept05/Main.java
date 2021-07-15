package tech.beepbeep.beept05;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import tech.beepbeep.beept05.activities.MainActivity;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
