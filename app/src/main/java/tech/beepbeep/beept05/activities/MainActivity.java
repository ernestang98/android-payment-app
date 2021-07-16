package tech.beepbeep.beept05.activities;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONException;
import org.json.JSONObject;
import tech.beepbeep.beept05.R;
import tech.beepbeep.beept05.adapters.ChargerListAdapter;
import tech.beepbeep.beept05.models.ChargerObject;
import tech.beepbeep.beept05.utils.BeepPreAuthException;
import tech.beepbeep.beept05.utils.ClickListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

// https://stackoverflow.com/questions/37621934/inflateexception-binary-xml-file-line-8-error-inflating-class-imageview
// https://code.luasoftware.com/tutorials/android/android-use-recylerview-as-viewpager/
// https://www.figma.com/file/FKgC7gb2UHXOpMWunWBWq0/EV-Charger-CC-Terminal-UI-Draft?node-id=1%3A171
// https://stackoverflow.com/questions/8631095/how-to-prevent-going-back-to-the-previous-activity

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN-ACTIVITY";

    List<ChargerObject> sampleData = new ArrayList<>(
        Arrays.asList(
            new ChargerObject(
                    "CHARGER-01",
                    "AC1",
                    "22 kWh",
                    "Condo A",
                    "HKD1.50/kWh",
                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
            ),
            new ChargerObject(
                    "CHARGER-02",
                    "AC2",
                    "23 kWh",
                    "Condo B",
                    "HKD1.50/kWh",
                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
            ),
            new ChargerObject(
                    "CHARGER-03",
                    "AC3",
                    "24 kWh",
                    "Condo C",
                    "HKD1.50/kWh",
                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
            ),
            new ChargerObject(
                    "CHARGER-04",
                    "AC4",
                    "25 kWh",
                    "Condo D",
                    "HKD1.50/kWh",
                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
            ),
            new ChargerObject(
                    "CHARGER-05",
                    "AC5",
                    "26 kWh",
                    "Condo E",
                    "HKD1.50/kWh",
                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
            )
//            ),
//            new ChargerObject(
//                    "AC4",
//                    "25 kWh",
//                    "Condo D",
//                    "HKD1.50/kWh",
//                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
//            )
        )
    );



    // Uncomment SnapHelper to enable Page Snap
    // I cannot do up a 1 row 2 column LayoutManager, so I designed my XML to have 2 charger at once (limitation)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView chargerList = findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        chargerList.setLayoutManager(layoutManager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(chargerList);

        ChargerListAdapter chargerListAdapter = new ChargerListAdapter(this, sampleData, new ClickListener() {
            @Override
            public void onCardClicked(int position) {

            }
        });

        chargerList.setAdapter(chargerListAdapter);
        Log.i(TAG, String.valueOf(chargerList.getAdapter().getItemCount()));

    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        logActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == T05PAY_CODE || requestCode == T05PRE_AUTH_CODE ||
//                requestCode == T05VOID_LATEST || requestCode == T05POST_AUTH_CODE) {
//            String resJSONString = data.getStringExtra("response");
//            try {
//                JSONObject resJSON = new JSONObject(resJSONString);
//                Log.i(TAG, resJSON.getString("status"));
//                if (resJSON.getString("status").equals("Approved")) {
//                    if (requestCode ==  T05PRE_AUTH_CODE) {
//                        Log.i(TAG, "LOLOOLOLOL");
//                        Log.i(TAG, resJSON.getString("sessionId"));
//                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.putBoolean("isPreAuthSession", true);
//                        editor.putString("preAuthSession", resJSON.getString("sessionId"));
//                        editor.apply();
//                        Log.w(TAG, String.valueOf(sharedPref.getBoolean("isPreAuthSession", false)));
//                        Log.w(TAG, String.valueOf(sharedPref.getString("preAuthSession", "WRONGGGGG")));
//                    }
//                    if (requestCode == T05POST_AUTH_CODE) {
//                        boolean isPreAuth = sharedPref.getBoolean("isPreAuthSession", false);
//                        String preAuthSession = sharedPref.getString("preAuthSession", "NULL");
//                        if (!isPreAuth || preAuthSession.contentEquals("NULL")) {
//                            throw new BeepPreAuthException("Not possible for you to capture pre-auth when there is no pre-auth to begin with");
//                        } else {
//                            SharedPreferences.Editor editor = sharedPref.edit();
//                            editor.putBoolean("isPreAuthSession", false);
//                            editor.putString("preAuthSession", "NULL");
//                            editor.apply();
//                        }
//                    }
//                }
//                else {
//                    Log.w(TAG, "ERROR JENG JENG JENG");
//                    Log.i(TAG, resJSON.getString("message"));
//                }
//                myDialog.dismiss();
//                finish();
//                startActivity(getIntent());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (BeepPreAuthException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            Log.e(TAG, "Request Code " + requestCode + " unsupported!");
//        }
//    }
//
//    public void sendRequest(View v) {
//        String idString = v.getResources().getResourceEntryName(v.getId());
//        TextView btnClose;
//        Button btnFollow;
//        switch(idString) {
//            case "button1":
//                myDialog.setContentView(R.layout.activity_payment_popup);
//                btnClose = myDialog.findViewById(R.id.txtclose);
//                btnFollow = myDialog.findViewById(R.id.btnfollow);
//                btnFollow.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            EditText amountText = myDialog.findViewById(R.id.amount_i);
//                            int amount = (int) Math.ceil(Double.parseDouble(amountText.getText().toString()));
//
//                            CheckBox printChecked = myDialog.findViewById(R.id.print);
//                            boolean printEnabled = printChecked.isEnabled();
//
//                            EditText customText = myDialog.findViewById(R.id.custom_i);
//                            String customPrintData = customText.getText().toString();
//
//                            sendPaymentReq(amount, printEnabled, customPrintData);
//
//                            myDialog.dismiss();
//
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                btnClose.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        myDialog.dismiss();
//                    }
//                });
//                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                myDialog.show();
//                break;
//            case "button2":
//                Button b = (Button) v;
//                String textString = b.getText().toString();
//                if (textString.equals("Pre-Auth")) {
//                    myDialog.setContentView(R.layout.activity_preauth_popup);
//                    btnClose = myDialog.findViewById(R.id.txtclose);
//                    btnFollow = myDialog.findViewById(R.id.btnfollow);
//                    btnFollow.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            try {
//                                EditText maxAmountText = myDialog.findViewById(R.id.amount_i);
//                                String maxAmount = String.valueOf((int) Math.ceil(Double.parseDouble(maxAmountText.getText().toString())));
//
//                                EditText customText = myDialog.findViewById(R.id.text_i);
//                                String preAuthText = customText.getText().toString();
//
//                                sendPreAuthReq(maxAmount, preAuthText);
//
//                                myDialog.dismiss();
//
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                } else {
//                    myDialog.setContentView(R.layout.activity_postauth_popup);
//                    btnClose = myDialog.findViewById(R.id.txtclose);
//                    btnFollow = myDialog.findViewById(R.id.btnfollow);
//                    btnFollow.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            try {
//                                String sessionId = sharedPref.getString("preAuthSession", "NULL");
//
//                                if (sessionId.contentEquals("NULL")) throw new NullPointerException();
//
//                                EditText amountText = myDialog.findViewById(R.id.amount_i);
//                                String amount = String.valueOf((int) Math.ceil(Double.parseDouble(amountText.getText().toString())));
//
//                                sendPostAuthReq(amount, sessionId);
//
//                                myDialog.dismiss();
//
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//                btnClose.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        myDialog.dismiss();
//                    }
//                });
//                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                myDialog.show();
//                break;
//            case "button3":
//                sendVoidReq(v);
//                break;
//            default:
//                break;
//        }
//    }
}