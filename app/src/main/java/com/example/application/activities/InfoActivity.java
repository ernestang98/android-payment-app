package com.example.application.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import com.google.gson.Gson;
import org.json.JSONObject;

import com.example.application.R;
import com.example.application.models.ChargerObject;
import com.example.application.utils.*;

import java.io.UnsupportedEncodingException;
import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.example.application.utils.Constants.HHMMDateFormat12Hrs;
import static com.example.application.utils.Constants.SimpleDateFormatString;
import static com.example.application.utils.Utils.*;

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
    private TextView totalCharged;
    private TextView totalHours;
    private EditText phoneNumber;
    private static ChargerObject chargerObject;
    private static boolean chargerIdAvailability;

    private final DatabaseHandler db = new DatabaseHandler(this);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
//        db.dropTable();
//        this.deleteDatabase("ChargerManager");

        boolean launchEnding = getIntent().getBooleanExtra("launchEnding", false);
        @Nullable String timeStarted = getIntent().getStringExtra("startTime");

        if (launchEnding && timeStarted != null) {
            try {
                Dialog tempDialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                tempDialog.setContentView(R.layout.charger_finish);
                TextView chargerLoc = tempDialog.findViewById(R.id.cf_main_group_header);
                chargerLoc.setText(chargerObject.getChargerLocation());
                TextView chargerName = tempDialog.findViewById(R.id.cf_main_group_subheader);
                chargerName.setText(chargerObject.getChargerName());

                DateFormat dateFormat = new SimpleDateFormat(HHMMDateFormat12Hrs, Locale.ENGLISH);
                String dateString = dateFormat.format(new Date()).toString();

                Calendar calendar = Calendar.getInstance();
                int year  = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day   = calendar.get(Calendar.DAY_OF_MONTH);
                String monthString = new DateFormatSymbols().getMonths()[month];
                TextView endTime = tempDialog.findViewById(R.id.cf_main_group_datetime);
                endTime.setText(monthString + " " + year + ", " + dateString);

                Date end = new Date();
                Date start = new SimpleDateFormat(SimpleDateFormatString, Locale.ENGLISH).parse(timeStarted);
                assert start != null;
                long difference = end.getTime() - start.getTime();
                long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
                long hours = TimeUnit.MILLISECONDS.toHours(difference);

                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                TextView totalCost = tempDialog.findViewById(R.id.cf_main_group_cost);
                totalCost.setText(chargerObject.getChargerCurrency() + " " + formatter.format(Double.parseDouble(chargerObject.getChargerPrice()) * seconds));

                TextView totalChargeTime = tempDialog.findViewById(R.id.cf_main_group_subgroup_2_text);
                totalChargeTime.setText("Total Charge Time: " + hours + " hrs " + minutes % 60 + " mins " + seconds % 60 + " secs");

                // Total charge amount = charge power * number of hours but I use seconds cause hours will be 0
                TextView totalChargeAmount = tempDialog.findViewById(R.id.cf_main_group_subgroup_3_text);
                totalChargeAmount.setText("Total Charge Amount: " + (int) (Integer.parseInt(chargerObject.getChargerPower()) * seconds) + "kWH");

                TextView totalChargeRate = tempDialog.findViewById(R.id.cf_main_group_subgroup_4_text);
                totalChargeRate.setText("Charge Rate: " + chargerObject.getChargerCurrency() + chargerObject.getChargerPrice() + chargerObject.getChargerRate());

                TextView paymentType = tempDialog.findViewById(R.id.cf_main_group_subgroup_1_text);
                @Nullable String paymentMethod = getIntent().getStringExtra("paymentMethod");
                if (paymentMethod == null) paymentMethod = "Credit Card";
                paymentType.setText("Payment Method: " + paymentMethod);

                ImageView closeBtn = tempDialog.findViewById(R.id.cf_close_group_container_close);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tempDialog.dismiss();
                    }
                });
                tempDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        chargerObject = new Gson()
                .fromJson(getIntent().getStringExtra("data"), ChargerObject.class);
        chargerIdAvailability = db.checkIfChargerIsVacant(chargerObject.getChargerId());
        myDialog = new Dialog(this);

        if (chargerIdAvailability) {
            setContentView(R.layout.activity_info);
            chargerName = findViewById(R.id.ai_charger_name);
            chargerName.setText(chargerObject.getChargerName());

            chargerLocation = findViewById(R.id.ai_charger_location);
            chargerLocation.setText(chargerObject.getChargerLocation());

            chargerPower = findViewById(R.id.ai_group_1_container_text);
            chargerPower.setText(chargerObject.getChargerPower() + chargerObject.getChargerPowerUnit());

            chargerPrice = findViewById(R.id.ai_charger_price);
            chargerPrice.setText("Price: " + chargerObject.getChargerCurrency() + chargerObject.getChargerPrice() + chargerObject.getChargerRate());

            chargerDescription = findViewById(R.id.ai_about_charger_subheader);
            chargerDescription.setText(chargerObject.getChargerDescription());
        }
        else {
            setContentView(R.layout.activity_charging);

            chargerLocation = findViewById(R.id.ac_group_1_container_1_text);
            chargerLocation.setText(chargerObject.getChargerLocation());

            chargerName = findViewById(R.id.ac_group_2_container_1_text);
            chargerName.setText(chargerObject.getChargerName());

            chargerPower = findViewById(R.id.ac_group_2_container_2_text);
            chargerPower.setText(chargerObject.getChargerPower() + chargerObject.getChargerPowerUnit());

            try {
                String startingTime = db.returnValueTaggedToChargerId(chargerObject.getChargerId(), 3);
                Date start = new SimpleDateFormat(SimpleDateFormatString, Locale.ENGLISH).parse(startingTime);
                Date end = new Date();
                assert start != null;
                Log.i(TAG, startingTime);
                long difference = end.getTime() - start.getTime();
                long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
                long hours = TimeUnit.MILLISECONDS.toHours(difference);
                totalHours = findViewById(R.id.ac_current_time);
                totalHours.setText("Current time: " + seconds + " seconds");
                final Handler someHandler = new Handler(getMainLooper());
                someHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Date end = new Date();
                        long difference = end.getTime() - start.getTime();
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
                        totalHours.setText("Current time: " + seconds + " seconds");
                        someHandler.postDelayed(this, 1000);
                    }
                }, 10);
            } catch (BeepDbHandlerException | ParseException e) {
                e.printStackTrace();
            }
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
        myDialog.setContentView(R.layout.charger_popup);
        btnClose = myDialog.findViewById(R.id.cp_group_1_container_close);
        btnSend = myDialog.findViewById(R.id.cp_group_2_button);
        TextView button = myDialog.findViewById(R.id.cp_group_2_button_container_text);
        button.setText("Stop Charging");
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    phoneNumber = myDialog.findViewById(R.id.cp_phone);
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
        myDialog.setContentView(R.layout.charger_popup);
        btnClose = myDialog.findViewById(R.id.cp_group_1_container_close);
        btnSend = myDialog.findViewById(R.id.cp_group_2_button);
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
                throw new BeepException("Unsupported error code! Only accept PreAuth & PostAuth as of now!");
            }
            String resJSONString = data.getStringExtra("response");
            JSONObject resJSON = new JSONObject(resJSONString);
            Log.i(TAG, resJSONString);
            Log.i(TAG, resJSON.getString("status"));
            if (!resJSON.getString("status").equals("Approved") || resultCode != RESULT_OK) {
                throw new BeepPreAuthException("Transaction failed: " + resJSON.getString("message"));
            }

            if (requestCode == T05PRE_AUTH_CODE) {
                if (!chargerIdAvailability) {
                    throw new BeepPreAuthException("You are pre-authing... MUST BE vacant, something is wrong");
                }
                // only if you are successful will you want to tag the chargerID to the phoneNumber
                phoneNumber = myDialog.findViewById(R.id.cp_phone);
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
                phoneNumber = myDialog.findViewById(R.id.cp_phone);
                String phoneString = phoneNumber.getText().toString();
                String sessionString = db.returnValueTaggedToChargerId(chargerObject.getChargerId(), 2);
                boolean authenticate = db.authDetailsTaggedToChargerId(chargerObject.getChargerId(), sessionString, phoneString);
                if (authenticate) {
                    String startTime = db.returnValueTaggedToChargerId(chargerObject.getChargerId(), 3);
                    String paymentMethod;
                    try {
                        paymentMethod = resJSON.getString("paymentType");
                    } catch (Exception e) {
                        e.printStackTrace();
                        paymentMethod = "Credit Card";
                    }
                    db.vacantTheCharger(chargerObject.getChargerId());
                    myDialog.dismiss();
                    finish();
                    Intent intent = new Intent(this, InfoActivity.class);
                    Gson gson = new Gson();
                    String stringData = gson.toJson(chargerObject);
                    intent.putExtra("data", stringData);
                    intent.putExtra("launchEnding", true);
                    intent.putExtra("startTime", startTime);
                    intent.putExtra("paymentMethod", paymentMethod);
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