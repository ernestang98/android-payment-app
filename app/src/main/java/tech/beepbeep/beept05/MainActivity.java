package tech.beepbeep.beept05;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Dialog myDialog;
    private final String TAG = "T05-TERMINAL";
    private final int T05PAY_CODE = 1;
    private final int T05PRE_AUTH_CODE = 2;
    private final int T05POST_AUTH_CODE = 3;
    private final int T05VOID_LATEST = 4;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDialog = new Dialog(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        ImageView beepLogo = findViewById(R.id.logo);
        beepLogo.getLayoutParams().height = height / 8;

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isPreAuth = sharedPref.getBoolean("isPreAuthSession", false);
        Log.i(TAG, String.valueOf(isPreAuth));

        if (isPreAuth) {
            Button auth = findViewById(R.id.button2);
            auth.setText("Capture");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logActivityResult(requestCode, resultCode, data);

        if (requestCode == T05PAY_CODE || requestCode == T05PRE_AUTH_CODE ||
                requestCode == T05VOID_LATEST || requestCode == T05POST_AUTH_CODE) {
            String resJSONString = data.getStringExtra("response");
            try {
                JSONObject resJSON = new JSONObject(resJSONString);
                Log.i(TAG, resJSON.getString("status"));
                if (resJSON.getString("status").equals("Approved")) {
                    if (requestCode ==  T05PRE_AUTH_CODE) {
                        Log.i(TAG, "LOLOOLOLOL");
                        Log.i(TAG, resJSON.getString("sessionId"));
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("isPreAuthSession", true);
                        editor.putString("preAuthSession", resJSON.getString("sessionId"));
                        editor.apply();
                        Log.w(TAG, String.valueOf(sharedPref.getBoolean("isPreAuthSession", false)));
                        Log.w(TAG, String.valueOf(sharedPref.getString("preAuthSession", "WRONGGGGG")));
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

    public void sendRequest(View v) {
        String idString = v.getResources().getResourceEntryName(v.getId());
        TextView btnClose;
        Button btnFollow;
        switch(idString) {
            case "button1":
                myDialog.setContentView(R.layout.activity_payment_popup);
                btnClose = myDialog.findViewById(R.id.txtclose);
                btnFollow = myDialog.findViewById(R.id.btnfollow);
                btnFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            EditText amountText = myDialog.findViewById(R.id.amount_i);
                            int amount = (int) Math.ceil(Double.parseDouble(amountText.getText().toString()));

                            CheckBox printChecked = myDialog.findViewById(R.id.print);
                            boolean printEnabled = printChecked.isEnabled();

                            EditText customText = myDialog.findViewById(R.id.custom_i);
                            String customPrintData = customText.getText().toString();

                            sendPaymentReq(amount, printEnabled, customPrintData);

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
                break;
            case "button2":
                Button b = (Button) v;
                String textString = b.getText().toString();
                if (textString.equals("Pre-Auth")) {
                    myDialog.setContentView(R.layout.activity_preauth_popup);
                    btnClose = myDialog.findViewById(R.id.txtclose);
                    btnFollow = myDialog.findViewById(R.id.btnfollow);
                    btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                EditText maxAmountText = myDialog.findViewById(R.id.amount_i);
                                String maxAmount = String.valueOf((int) Math.ceil(Double.parseDouble(maxAmountText.getText().toString())));

                                EditText customText = myDialog.findViewById(R.id.text_i);
                                String preAuthText = customText.getText().toString();

                                sendPreAuthReq(maxAmount, preAuthText);

                                myDialog.dismiss();

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    myDialog.setContentView(R.layout.activity_postauth_popup);
                    btnClose = myDialog.findViewById(R.id.txtclose);
                    btnFollow = myDialog.findViewById(R.id.btnfollow);
                    btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                String sessionId = sharedPref.getString("preAuthSession", "NULL");

                                if (sessionId.contentEquals("NULL")) throw new NullPointerException();

                                EditText amountText = myDialog.findViewById(R.id.amount_i);
                                String amount = String.valueOf((int) Math.ceil(Double.parseDouble(amountText.getText().toString())));

                                sendPostAuthReq(amount, sessionId);

                                myDialog.dismiss();

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();
                break;
            case "button3":
                sendVoidReq(v);
                break;
            default:
                break;
        }
    }

    public void sendPaymentReq(int amount, boolean printEnabled, String customPrintData) throws UnsupportedEncodingException {
        Intent intent = new Intent("com.t05.t05pay.PAY");
        intent.setDataAndType(null, "text/plain");
        String customOrderId = UUID.randomUUID().toString();
//        int amount = 1;
//        String customPrintData = "custom print statement";
//        boolean printEnabled = false;
        String queryString = URLEncoder.encode("amount=" + amount + "&customOrderId=" + customOrderId + "&printEnabled=" + printEnabled + "&customPrintData=" + customPrintData, "UTF-8");
        intent.putExtra("queryString", queryString);
        startActivityForResult(intent, T05PAY_CODE);
    }

    public void sendPreAuthReq(String maxAmount, String preAuthText) throws UnsupportedEncodingException {
        Intent intent = new Intent("com.t05.t05pay.PRE_AUTH");
        intent.setDataAndType(null, "text/plain");
        String customOrderId = UUID.randomUUID().toString();
//        int amount = 1;
//        String preAuthText = "custom print statement";
        String queryString = URLEncoder.encode("maxAmount=" + Integer.parseInt(maxAmount) + "&preAuthText=" + preAuthText, "UTF-8");
        intent.putExtra("queryString", queryString);
        startActivityForResult(intent, T05PRE_AUTH_CODE);
    }

    public void sendPostAuthReq(String amount, String sessionId) throws UnsupportedEncodingException {
        Intent intent = new Intent("com.t05.t05pay.POST_AUTH");
        intent.setDataAndType(null, "text/plain");
//        int amount = 1;
//        String sessionId = "dummy";
        String queryString = URLEncoder.encode("amount=" + Integer.parseInt(amount) + "&sessionId=" +  sessionId, "UTF-8");
        intent.putExtra("queryString", queryString);
        startActivityForResult(intent, T05POST_AUTH_CODE);
    }

    public void sendVoidReq(View view) {
        Intent intent = new Intent("com.t05.t05pay.VOID_LATEST");
        intent.setDataAndType(null, "text/plain");
        startActivityForResult(intent, T05VOID_LATEST);
    }

    private void logActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Logging Activity Result...");
        Log.i(TAG, "Request Code: " + requestCode);
        Log.i(TAG, "Result Code: " + resultCode);
        Log.i(TAG, "Intent Code: " + data);

    }

    public void testLoadingScreen(View view) {
        Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
        startActivity(intent);
    }


}