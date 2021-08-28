package com.example.application.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ChargerManager";

    private static final String CHARGER_TABLE = "charger";
    private static final String CHARGER_TABLE_ID = "charger_id";
    private static final String CHARGER_TABLE_PHONE_NUMBER = "charger_phone";
    private static final String CHARGER_TABLE_SESSION_ID = "charger_session" ;
    private static final String CHARGER_TABLE_START_TIME = "charger_start" ;
    private static final String TAG = "DB-HANDLER";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE "
                + CHARGER_TABLE
                + "("
                + CHARGER_TABLE_ID + " Text PRIMARY KEY,"
                + CHARGER_TABLE_PHONE_NUMBER + " TEXT,"
                + CHARGER_TABLE_SESSION_ID + " TEXT,"
                + CHARGER_TABLE_START_TIME + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + CHARGER_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void tagDetailsToCharger(String chargerId, String chargerPhoneNumber, String chargerSessionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CHARGER_TABLE_ID, chargerId);
        values.put(CHARGER_TABLE_PHONE_NUMBER, chargerPhoneNumber);
        values.put(CHARGER_TABLE_SESSION_ID, chargerSessionId);
        Date d = new Date();
        values.put(CHARGER_TABLE_START_TIME, d.toString());
        db.insert(CHARGER_TABLE, null, values);
        db.close();
    }

    public boolean checkIfChargerIsVacant(String chargerId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                CHARGER_TABLE,
                new String[] {
                    CHARGER_TABLE_ID,
                    CHARGER_TABLE_PHONE_NUMBER,
                    CHARGER_TABLE_SESSION_ID,
                    CHARGER_TABLE_START_TIME
                },
                CHARGER_TABLE_ID + "=?",
                new String[] {
                    String.valueOf(chargerId)
                },
                null, null, null, null
        );
        boolean result = cursor.moveToFirst();
        System.out.println(result);
        db.close();
        cursor.close();
        return !result;
    }

    public String returnValueTaggedToChargerId(String chargerId, int value) throws BeepDbHandlerException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                CHARGER_TABLE,
                new String[] {
                        CHARGER_TABLE_ID,
                        CHARGER_TABLE_PHONE_NUMBER,
                        CHARGER_TABLE_SESSION_ID,
                        CHARGER_TABLE_START_TIME
                },
                CHARGER_TABLE_ID + "=?",
                new String[] {
                        String.valueOf(chargerId)
                },
                null, null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
            String phoneNumberRegistered = cursor.getString(value);
            cursor.close();
            db.close();
            return phoneNumberRegistered;
        }
        else {
            db.close();
            throw new BeepDbHandlerException("charger id does not even exist!!");
        }
    }

    public boolean authDetailsTaggedToChargerId(String chargerId, String sessionId, String phoneNumber) throws BeepDbHandlerException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                CHARGER_TABLE,
                new String[] {
                        CHARGER_TABLE_ID,
                        CHARGER_TABLE_PHONE_NUMBER,
                        CHARGER_TABLE_SESSION_ID,
                        CHARGER_TABLE_START_TIME
                },
                CHARGER_TABLE_ID + "=?",
                new String[] {
                        String.valueOf(chargerId)
                },
                null, null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
            String phoneNumberRegistered = cursor.getString(1);
            System.out.println("PHONE NUMBER: " + phoneNumberRegistered);
            String sessionIdRegistered = cursor.getString(2);
            System.out.println("SESSION ID: " + sessionIdRegistered);
            System.out.println(sessionId);
            if (!phoneNumberRegistered.equals(phoneNumber)) throw new BeepDbHandlerException("Phone number does not match");
            if (!sessionIdRegistered.equals(sessionId)) throw new BeepDbHandlerException("Session id does not match");
            cursor.close();
            db.close();
            return true;
        }
        else {
            db.close();
            throw new BeepDbHandlerException("charger id does not even exist!!");
        }
    }

    // Deleting single contact
    public void vacantTheCharger(String chargerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
            CHARGER_TABLE,
            CHARGER_TABLE_ID + " = ?",
            new String[] { chargerId }
        );
        db.close();
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + CHARGER_TABLE);
        db.close();
    }

    public void createTable() {

    }

}
