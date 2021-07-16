package tech.beepbeep.beept05.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ChargerManager";

    private static final String CHARGER_TABLE = "charger";
    private static final String CHARGER_TABLE_ID = "charger_id";
    private static final String CHARGE_TABLE_PHONE_NUMBER = "charger_phone";

    private static final String SESSION_TABLE = "session";
    private static final String SESSION_TABLE_ID = "session_id";
    private static final String SESSION_TABLE_PHONE_NUMBER = "session_phone";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SESSION_TABLE = "CREATE TABLE " + SESSION_TABLE + "("
                + SESSION_TABLE_ID + " TEXT PRIMARY KEY," + SESSION_TABLE_PHONE_NUMBER + " TEXT" + ")";
        db.execSQL(CREATE_SESSION_TABLE);

        String CREATE_CHARGER_TABLE = "CREATE TABLE " + CHARGER_TABLE + "("
                + CHARGER_TABLE_ID + " TEXT PRIMARY KEY," + CHARGE_TABLE_PHONE_NUMBER + " TEXT" + ")";
        db.execSQL(CREATE_CHARGER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + CHARGER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void tagPhoneNumberToCharger(String chargerId, String chargerPhoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CHARGER_TABLE_ID, chargerId);
        values.put(CHARGE_TABLE_PHONE_NUMBER, chargerPhoneNumber);
        db.insert(CHARGER_TABLE, null, values);
        db.close();
    }

    public String getPhoneNumberTaggedToCharger(String chargerId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(CHARGER_TABLE, new String[] { CHARGER_TABLE_ID,
                        CHARGE_TABLE_PHONE_NUMBER }, CHARGER_TABLE_ID + "=?",
                new String[] { String.valueOf(chargerId) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            String phoneNumber = cursor.getString(1);
            cursor.close();
            return phoneNumber;
        }
        return "NULL";
    }

    // code to get all contacts in a list view
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // code to update the single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
