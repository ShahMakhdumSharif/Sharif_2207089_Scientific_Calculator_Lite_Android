package com.example.calculator;
import android.util.Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "app.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL UNIQUE, " + // UNIQUE prevents duplicates
                COL_PASSWORD + " TEXT NOT NULL" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: " + oldVersion + " -> " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_USERS,
                new String[]{COL_ID, COL_USERNAME, COL_PASSWORD},
                null, null, null, null, COL_USERNAME + " ASC");
    }

    public boolean isUsernameExists(String username) {
        if (username == null) return false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_USERS,
                    new String[]{COL_ID},
                    COL_USERNAME + " = ?",
                    new String[]{username},
                    null, null, null);
            return c != null && c.getCount() > 0;
        } finally {
            if (c != null) c.close();
        }
    }

    public long addUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) return -1;
        if (isUsernameExists(username)) return -1;

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);

        long id = -1;
        try {
            id = db.insertOrThrow(TABLE_USERS, null, values);
            Log.i(TAG, "addUser inserted id=" + id + " user=" + username);
            return id;
        } catch (Exception e) {
            Log.w(TAG, "addUser failed for " + username + " : " + e.getMessage());
            return -1;
        }
    }
    public int getUsersCount() {
        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
            if (c.moveToFirst()) return c.getInt(0);
            return 0;
        } catch (Exception e) {
            android.util.Log.w("DatabaseHelper", "getUsersCount failed: " + e.getMessage());
            return -1;
        } finally {
            if (c != null) c.close();
        }
    }

    public void dumpUsersToLog() {
        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            android.util.Log.i("DatabaseHelper", "DB path: " + db.getPath());
            c = db.query(TABLE_USERS, new String[]{COL_ID, COL_USERNAME, COL_PASSWORD},
                    null, null, null, null, COL_USERNAME + " ASC");
            android.util.Log.i("DatabaseHelper", "dumpUsersToLog count=" + (c == null ? "null" : c.getCount()));
            if (c != null && c.moveToFirst()) {
                int idCol = c.getColumnIndexOrThrow(COL_ID);
                int uCol = c.getColumnIndexOrThrow(COL_USERNAME);
                int pCol = c.getColumnIndexOrThrow(COL_PASSWORD);
                do {
                    android.util.Log.i("DatabaseHelper",
                            "user: id=" + c.getLong(idCol)
                                    + " username=" + c.getString(uCol)
                                    + " password=" + c.getString(pCol));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            android.util.Log.w("DatabaseHelper", "dumpUsersToLog failed: " + e.getMessage());
        } finally {
            if (c != null) c.close();
        }
    }

}
