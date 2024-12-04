package net.gamedoctor.TusurPixelBattle.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ServerDB {
    final private Database dbHelper;
    private final String tableName = "account_info";
    private SQLiteDatabase database;

    public ServerDB(Context context) {
        dbHelper = new Database(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createAccountData(String name, String password) {
        boolean exist = false;
        Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE name = '" + name + "'", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(0) != null) {
                exist = true;
            }
            cursor.moveToNext();
        }
        cursor.close();

        if (!exist) {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("password", password);
            database.insert(tableName, null, values);
        }
    }

    public void deleteAccountData(String name) {
        database.delete(tableName, "name = ?",
                new String[]{name});
    }

    public void updateAccountData(String name, String password) {
        try {
            deleteAccountData(name);
            createAccountData(name, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAccountExist() {
        boolean exist = false;
        Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(0) != null) {
                exist = true;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return exist;
    }

    public String getName() {
        String result = null;
        Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(0) != null) {
                result = cursor.getString(0);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public String getPassword() {
        String result = null;
        Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(1) != null) {
                result = cursor.getString(1);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }
}