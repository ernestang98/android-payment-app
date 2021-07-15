package tech.beepbeep.beept05.utils;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class Utils {

    public static void logActivityResult(int requestCode, int resultCode, Intent data, String TAG) {
        Log.i(TAG, "Logging Activity Result...");
        Log.i(TAG, "Request Code: " + requestCode);
        Log.i(TAG, "Result Code: " + resultCode);
        Log.i(TAG, "Intent Code: " + data);
    }

    public static Intent createPreAuthIntentForPreAuthReq(String maxAmount, String preAuthText) throws UnsupportedEncodingException {
        Intent intent = new Intent("com.t05.t05pay.PRE_AUTH");
        intent.setDataAndType(null, "text/plain");
        String queryString = URLEncoder.encode("maxAmount=" + Integer.parseInt(maxAmount) + "&preAuthText=" + preAuthText, "UTF-8");
        intent.putExtra("queryString", queryString);
        return intent;
    }

    public static Intent createPaymentIntentForPaymentReq(int amount, boolean printEnabled, String customPrintData) throws UnsupportedEncodingException {
        Intent intent = new Intent("com.t05.t05pay.PAY");
        intent.setDataAndType(null, "text/plain");
        String customOrderId = UUID.randomUUID().toString();
        String queryString = URLEncoder.encode("amount=" + amount + "&customOrderId=" + customOrderId + "&printEnabled=" + printEnabled + "&customPrintData=" + customPrintData, "UTF-8");
        intent.putExtra("queryString", queryString);
        return intent;
    }

    public static Intent createPostAuthIntentForPostAuthReq(String amount, String sessionId) throws UnsupportedEncodingException {
        Intent intent = new Intent("com.t05.t05pay.POST_AUTH");
        intent.setDataAndType(null, "text/plain");
        String queryString = URLEncoder.encode("amount=" + Integer.parseInt(amount) + "&sessionId=" +  sessionId, "UTF-8");
        intent.putExtra("queryString", queryString);
        return intent;
    }

    public static Intent createVoidIntentForVoidReq(View view) {
        Intent intent = new Intent("com.t05.t05pay.VOID_LATEST");
        intent.setDataAndType(null, "text/plain");
        return intent;
    }

}
