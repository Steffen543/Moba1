package com.hs_weingarten.sk.am.password_manager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Password-Manager.db";
    public static final String TABLE_NAME = "Password-Table";

    public static final String COL_0 = "ID";
    public static final String COL_1 = "FOLDER";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "EMAIL";
    public static final String COL_4 = "USERNAME";
    public static final String COL_5 = "PASSWORD";
    public static final String COL_6 = "DESCRIPTION";
    public static final String COL_7 = "CREATED_DATE";
    public static final String COL_8 = "EDITED_DATE";

    public DataBaseHandler(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_NEW_FOLDER = "CREATE TABLE " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, FOLDER TEXT, NAME TEXT, EMAIL TEXT, USERNAME TEXT, " +
                "PASSWORD TEXT, DESCRIPTION TEXT, CREATED_DATE INTEGER, EDITED_DATE INTEGER)";
        db.execSQL(CREATE_TABLE_NEW_FOLDER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(DROP_TABLE);
    }

    public boolean insertData(String folder, String name, String email, String username, String password, String description, String created, String edited){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, folder);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, email);
        contentValues.put(COL_4, username);
        contentValues.put(COL_5, password);
        contentValues.put(COL_6, description);
        contentValues.put(COL_7, created);
        contentValues.put(COL_8, edited);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        // Insertion check
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getFolders(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select FOLDER from " + TABLE_NAME, null);
        return res;
    }

    public Cursor getPasswordNames(String folder){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select NAME from " + TABLE_NAME + " where FOLDER == " + folder, null);
        return res;
    }

    public Cursor getAllData(String folder, String passwordname){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select EMAIL, USERNAME, PASSWORD, DESCRIPTION, CREATED_DATE, EDITED_DATE from "
                + TABLE_NAME + " where NAME == " + passwordname + " and FOLDER == " + folder, null);
        return res;
    }

    public boolean updateData(String id, String folder, String name, String email, String username, String password, String description, String created, String edited){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, folder);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, email);
        contentValues.put(COL_4, username);
        contentValues.put(COL_5, password);
        contentValues.put(COL_6, description);
        contentValues.put(COL_7, created);
        contentValues.put(COL_8, edited);

        int result = db.update(TABLE_NAME, contentValues, "ID =?", new String[]{id});
        if (result > 0){
            return true;
        } else {
            return false;
        }
    }
}
