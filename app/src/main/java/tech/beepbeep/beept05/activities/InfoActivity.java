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
import org.json.JSONObject;
import tech.beepbeep.beept05.R;
import tech.beepbeep.beept05.models.ChargerObject;
import tech.beepbeep.beept05.utils.*;

import java.io.UnsupportedEncodingException;

import static tech.beepbeep.beept05.utils.Utils.*;

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
    private static ChargerObject chargerObject;
    private static boolean chargerIdAvailability;

    private final DatabaseHandler db = new DatabaseHandler(this);

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
//        db.dropTable();
//        this.deleteDatabase("ChargerManager");
        chargerObject = new Gson()
                .fromJson(getIntent().getStringExtra("data"), ChargerObject.class);
        chargerIdAvailability = db.checkIfChargerIsVacant(chargerObject.getChargerId());
        myDialog = new Dialog(this);
        // check if charger is tagged to phoneNumber

        if (chargerIdAvailability) {
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

            chargerName = findViewById(R.id.textView111);
            chargerName.setText(chargerObject.getChargerName());

            chargerPower = findViewById(R.id.textView11);
            chargerPower.setText(chargerObject.getChargerPower());
        }
    }

    public void sendRequest(View v) {
        if (chargerIdAvailability) {
            sendChargingRequest(v);
        }
        else {
            sendCaptureRequest(v);
        }
    }

    public void sendCaptureRequest(View v) {
        ImageView btnClose;
        CardView btnSend;
        myDialog.setContentView(R.layout.activity_popup);
        btnClose = myDialog.findViewById(R.id.iconNext);
        btnSend = myDialog.findViewById(R.id.cardView2);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    phoneNumber = myDialog.findViewById(R.id.phone);
                    String phoneEntered = phoneNumber.getText().toString();
                    if (phoneEntered.equals(db.returnValueTaggedToChargerId(chargerObject.getChargerId(), 1))) {
                        String maxAmount = "1";
                        Intent intent = createPreAuthIntentForPreAuthReq(maxAmount, db.returnValueTaggedToChargerId(chargerObject.getChargerId(), 2));
                        startActivityForResult(intent, T05POST_AUTH_CODE);
                        myDialog.dismiss();
                    }
                    else {
                        throw new BeepPostAuthException("Phone number do not match");
                    }
                } catch (UnsupportedEncodingException | BeepDbHandlerException | BeepPostAuthException e) {
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
                    String amount = "1";
                    String preAuthText = "Pre-auth text";
                    Intent intent = createPreAuthIntentForPreAuthReq(amount, preAuthText);
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
                throw new BeepException("Unsupported error code! Only accept PreAuth as of now!");
            }
            String resJSONString = data.getStringExtra("response");
            JSONObject resJSON = new JSONObject(resJSONString);
            Log.i(TAG, resJSON.getString("status"));
            if (!resJSON.getString("status").equals("Approved") || resultCode != RESULT_OK) {
                throw new BeepPreAuthException("Transaction failed: " + resJSON.getString("message"));
            }

            if (requestCode == T05PRE_AUTH_CODE) {
                if (!chargerIdAvailability) {
                    throw new BeepPreAuthException("You are pre-authing... MUST BE vacant, something is wrong");
                }
                // only if you are successful will you want to tag the chargerID to the phoneNumber
                phoneNumber = myDialog.findViewById(R.id.phone);
                String phoneString = phoneNumber.getText().toString();
                String sessionString = resJSON.getString("sessionId");
                db.tagDetailsToCharger(chargerObject.getChargerId(), phoneString, sessionString);

                // only apply changes if everything is successful!
                myDialog.dismiss();

                finish();
                Intent intent = new Intent(this, InfoActivity.class);
                Gson gson = new Gson();
                String stringData = gson.toJson(chargerObject);
                intent.putExtra("data", stringData);
                startActivity(intent);
            }
            if (requestCode == T05POST_AUTH_CODE) { ;
                if (chargerIdAvailability) throw new BeepPostAuthException("You are post-authing... MUST BE occupied, something is wrong");
                phoneNumber = myDialog.findViewById(R.id.phone);
                String phoneString = phoneNumber.getText().toString();
                String sessionString = db.returnValueTaggedToChargerId(chargerObject.getChargerId(), 2);
                boolean authenticate = db.authDetailsTaggedToChargerId(chargerObject.getChargerId(), sessionString, phoneString);
                if (authenticate) {
                    db.vacantTheCharger(chargerObject.getChargerId());
                    myDialog.dismiss();
                    finish();
                    Intent intent = new Intent(this, InfoActivity.class);
                    Gson gson = new Gson();
                    String stringData = gson.toJson(chargerObject);
                    intent.putExtra("data", stringData);
                    startActivity(intent);
                } else {
                    throw new BeepDbHandlerException("Authentication somehow failed!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
     }

}