package tech.beepbeep.beept05.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import tech.beepbeep.beept05.R;
import tech.beepbeep.beept05.models.ChargerObject;
import tech.beepbeep.beept05.utils.BeepPreAuthException;
import static tech.beepbeep.beept05.utils.Utils.logActivityResult;
import static tech.beepbeep.beept05.utils.Utils.createPreAuthIntentForPreAuthReq;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class InfoActivity extends AppCompatActivity {

    private static final int T05PRE_AUTH_CODE = 123;
    private static final int T05POST_AUTH_CODE = 124;
    private static final String TAG = "INFO-ACTIVITY";
    private SharedPreferences sharedPref;
    private Dialog myDialog;
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

        myDialog = new Dialog(this);
    }

    public void sendChargingRequest(View v) {
        ImageView btnClose;
        CardView btnSend;
        myDialog.setContentView(R.layout.activity_popup);
        btnClose = myDialog.findViewById(R.id.iconNext);
        btnSend = myDialog.findViewById(R.id.cardView2);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String maxAmount = "1";
                    String preAuthText = "Set Pre-Auth Text here!";
                    Intent intent = createPreAuthIntentForPreAuthReq(maxAmount, preAuthText);
                    startActivityForResult(intent, T05PRE_AUTH_CODE);
                    myDialog.dismiss();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logActivityResult(requestCode, resultCode, data, TAG);

        if (requestCode == T05PRE_AUTH_CODE || requestCode == T05POST_AUTH_CODE) {
            String resJSONString = data.getStringExtra("response");
            try {
                JSONObject resJSON = new JSONObject(resJSONString);
                Log.i(TAG, resJSON.getString("status"));
                if (resJSON.getString("status").equals("Approved")) {
                    if (requestCode == T05PRE_AUTH_CODE) {
                        boolean isPreAuth = sharedPref.getBoolean("isPreAuthSession", false);
                        String preAuthSession = sharedPref.getString("preAuthSession", "NULL");
                        if (!isPreAuth || preAuthSession.contentEquals("NULL")) {

                        } else {
                            throw new BeepPreAuthException("Not possible for you to pre-auth as there is already a pre-auth sent! Capture it first");
                        }
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("isPreAuthSession", true);
                        editor.putString("preAuthSession", resJSON.getString("sessionId"));
                        editor.apply();
                    }
                    if (requestCode == T05POST_AUTH_CODE) {
                        boolean isPreAuth = sharedPref.getBoolean("isPreAuthSession", false);
                        String preAuthSession = sharedPref.getString("preAuthSession", "NULL");
                        if (!isPreAuth || preAuthSession.contentEquals("NULL")) {
                            throw new BeepPreAuthException("Not possible for you to capture pre-auth when there is no pre-auth to begin with");
                        } else {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("isPreAuthSession", false);
                            editor.putString("preAuthSession", "NULL");
                            editor.apply();
                        }
                    }
                }
                else {
                    Log.w(TAG, "ERROR JENG JENG JENG");
                    Log.i(TAG, resJSON.getString("message"));
                }
                myDialog.dismiss();
                finish();
                startActivity(getIntent());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (BeepPreAuthException e) {
                e.printStackTrace();
            }

        } else {
            Log.e(TAG, "Request Code " + requestCode + " unsupported!");
        }
    }

}