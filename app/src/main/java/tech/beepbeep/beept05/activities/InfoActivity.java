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
import tech.beepbeep.beept05.utils.DatabaseHandler;

import static tech.beepbeep.beept05.utils.Utils.logActivityResult;
import static tech.beepbeep.beept05.utils.Utils.createPreAuthIntentForPreAuthReq;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

// Charger ID -> NULL/PHONE
// PHONE -> NULL/[{ CHARGER ID : SESSION ID }]
// https://stackoverflow.com/questions/25376688/how-to-store-all-keys-from-a-specific-value-from-sharedpreferences
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
    private EditText phoneNumber;

    private final DatabaseHandler db = new DatabaseHandler(this);
    private final ChargerObject chargerObject = new Gson()
            .fromJson(getIntent().getStringExtra("data"), ChargerObject.class);

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        System.out.println(chargerObject);
        myDialog = new Dialog(this);
        // check if charger is tagged to phoneNumber

        String chargerIdAvailability = sharedPref.getString(chargerObject.getChargerId(), "NULL");
        String chargerIdAvailability = db.getPhoneNumberTaggedToCharger(chargerObject.getChargerId());
        if (chargerIdAvailability.equals("NULL")) {
            setContentView(R.layout.activity_info);
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
        else {
            setContentView(R.layout.activity_charging);
        }
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
                    // If you can come here means charge_id is tagged to NULL
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

        try {
            if (requestCode != T05POST_AUTH_CODE && requestCode != T05PRE_AUTH_CODE) {
                throw new BeepPreAuthException("Unsupported error code! Only accept PreAuth as of now!");
            }
            String resJSONString = data.getStringExtra("response");
            JSONObject resJSON = new JSONObject(resJSONString);
            Log.i(TAG, resJSON.getString("status"));
            if (!resJSON.getString("status").equals("Approved") || resultCode != RESULT_OK) {
                throw new BeepPreAuthException("Transaction failed: " + resJSON.getString("message"));
            }

            if (requestCode == T05PRE_AUTH_CODE) {
                String chargerIsAvailable = sharedPref.getString(chargerObject.getChargerId(), "NULL");
                if (!chargerIsAvailable.equals("NULL")) {
                    throw new BeepPreAuthException("You are pre-authing... MUST BE NULL, something is wrong");
                }
                // only if you are successful will you want to tag the chargerID to the phoneNumber
                phoneNumber = myDialog.findViewById(R.id.phone);
                String phoneString = phoneNumber.getText().toString();
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(chargerObject.getChargerId(), phoneString);

                // Check if number exists in numberKeys
                Set<String> phoneNumberSet = sharedPref.getStringSet("phoneNumberSet", new HashSet<>());
                if (phoneNumberSet.isEmpty()) {
                    phoneNumberSet.add(phoneString);
                    editor.putStringSet("phoneNumberSet", phoneNumberSet);
                }
                Set<String> setOfSessionIdTaggedToPhone = sharedPref.getStringSet(phoneString, new HashSet<>());
                if (!setOfSessionIdTaggedToPhone.isEmpty()) {
                    if (setOfSessionIdTaggedToPhone.contains(resJSON.getString("sessionId"))) {
                        throw new BeepPreAuthException("Duplicate sessionId, how can this be?!");
                    }
                }
                setOfSessionIdTaggedToPhone.add(resJSON.getString("sessionId"));

                // only apply changes if everything is successful!
                editor.apply();
                myDialog.dismiss();
                finish();
                Intent intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
            }
            if (requestCode == T05POST_AUTH_CODE) {
                String phoneNumberTaggedToChargerId = sharedPref.getString(chargerObject.getChargerId(), "NULL");
                if (phoneNumberTaggedToChargerId.equals("NULL")) throw new BeepPreAuthException("You are post-authing... MUST BE NOT NULL, something is wrong");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(chargerObject.getChargerId(), "NULL");
                Set<String> setOfSessionIdTaggedToPhone = sharedPref.getStringSet(phoneNumberTaggedToChargerId, new HashSet<>());
                if (setOfSessionIdTaggedToPhone.isEmpty() || !setOfSessionIdTaggedToPhone.contains(resJSON.getString("sessionId"))) throw new BeepPreAuthException("Session ID tagged to phone cannot be found!");
                editor.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (BeepPreAuthException e) {
            e.printStackTrace();
        }
    }

}