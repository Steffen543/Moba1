package Data.Password;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Data.Category.DatabaseHelper;
import Model.PasswordModel;

public class PasswordDataSource {
    private static final String LOG_TAG = PasswordDataSource.class.getSimpleName();

    private String[] columns = {
            DatabaseHelper.PASSWORD_TABLE.COLUMN_ID,
            DatabaseHelper.PASSWORD_TABLE.COLUMN_NAME,
            DatabaseHelper.PASSWORD_TABLE.COLUMN_EMAIL,
            DatabaseHelper.PASSWORD_TABLE.COLUMN_USERNAME,
            DatabaseHelper.PASSWORD_TABLE.COLUMN_PASSWORD,
            DatabaseHelper.PASSWORD_TABLE.COLUMN_DESCRIPTION,
            DatabaseHelper.PASSWORD_TABLE.COLUMN_ADDED,
            DatabaseHelper.PASSWORD_TABLE.COLUMN_EDITED,
            DatabaseHelper.PASSWORD_TABLE.COLUMN_PARENTID
    };


    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;


    public PasswordDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    private PasswordModel cursorToPasswordModel(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.PASSWORD_TABLE.COLUMN_ID);
        int idName = cursor.getColumnIndex(DatabaseHelper.PASSWORD_TABLE.COLUMN_NAME);
        int idEmail = cursor.getColumnIndex(DatabaseHelper.PASSWORD_TABLE.COLUMN_EMAIL);
        int idUsername = cursor.getColumnIndex(DatabaseHelper.PASSWORD_TABLE.COLUMN_USERNAME);
        int idPassword = cursor.getColumnIndex(DatabaseHelper.PASSWORD_TABLE.COLUMN_PASSWORD);
        int idDescription = cursor.getColumnIndex(DatabaseHelper.PASSWORD_TABLE.COLUMN_DESCRIPTION);
        int idAdded = cursor.getColumnIndex(DatabaseHelper.PASSWORD_TABLE.COLUMN_ADDED);
        int idEdited = cursor.getColumnIndex(DatabaseHelper.PASSWORD_TABLE.COLUMN_EDITED);
        int idParentId = cursor.getColumnIndex(DatabaseHelper.PASSWORD_TABLE.COLUMN_PARENTID);

        int id = cursor.getInt(idIndex);
        String name = cursor.getString(idName);
        String email = cursor.getString(idEmail);
        String username = cursor.getString(idUsername);
        String password = cursor.getString(idPassword);
        String description = cursor.getString(idDescription);
        String added = cursor.getString(idAdded);
        String edited = cursor.getString(idEdited);
        int parentId = cursor.getInt(idParentId);

        return new PasswordModel(id, name, email, username, password, description, added, edited, parentId);

    }

    public PasswordModel createPasswordModel(String name, String email, String username, String password, String description, int parentId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_NAME, name);
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_USERNAME, username);
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_PASSWORD, password);
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_DESCRIPTION, description);
        String currentDate = Helper.DateTimeConverter.DateToString(new Date());
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_ADDED, currentDate);
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_EDITED, currentDate);
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_PARENTID, parentId);

        long insertId = database.insert(DatabaseHelper.TABLE_PASSWORDS, null, values);

        Cursor cursor = database.query(DatabaseHelper.TABLE_PASSWORDS,
                columns, DatabaseHelper.PASSWORD_TABLE.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        PasswordModel passwordModel = cursorToPasswordModel(cursor);
        cursor.close();

        return passwordModel;
    }

    public PasswordModel getPassword(int id)
    {
        Cursor cursor = database.query(DatabaseHelper.TABLE_PASSWORDS,
                columns, " _id = ?", new String[] {String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();
        PasswordModel password;

        if(!cursor.isAfterLast()) {

            password = cursorToPasswordModel(cursor);
            cursor.close();
            return password;
        }

        return null;
    }

    public List<PasswordModel> getAllPasswords() {
        List<PasswordModel> pwdList = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_PASSWORDS,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        PasswordModel password;

        while(!cursor.isAfterLast()) {
            password = cursorToPasswordModel(cursor);
            pwdList.add(password);
            Log.d(LOG_TAG, "ID: " + password.getId() + ", Inhalt: " + password.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return pwdList;
    }

    public List<PasswordModel> getAllPasswords(int parentId) {
        List<PasswordModel> pwdList = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_PASSWORDS,
                columns, "parentId =?", new String[] { String.valueOf(parentId) }, null, null, null);

        cursor.moveToFirst();
        PasswordModel password;

        while(!cursor.isAfterLast()) {
            password = cursorToPasswordModel(cursor);
            pwdList.add(password);
            Log.d(LOG_TAG, "ID: " + password.getId() + ", Inhalt: " + password.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return pwdList;
    }

    public void deletePassword(PasswordModel password) {
        long id = password.getId();
        database.delete(DatabaseHelper.TABLE_PASSWORDS,
                DatabaseHelper.PASSWORD_TABLE.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + password.toString());
    }

    public PasswordModel updatePassword(long id, String name, String email, String username, String password, String description) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_NAME, name);
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_USERNAME, username);
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_PASSWORD, password);
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_DESCRIPTION, description);
        values.put(DatabaseHelper.PASSWORD_TABLE.COLUMN_EDITED, Helper.DateTimeConverter.DateToString(new Date()));

        database.update(DatabaseHelper.TABLE_PASSWORDS,
                values,
                DatabaseHelper.PASSWORD_TABLE.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(DatabaseHelper.TABLE_PASSWORDS,
                columns, DatabaseHelper.PASSWORD_TABLE.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        PasswordModel passwordModel = cursorToPasswordModel(cursor);
        cursor.close();

        return passwordModel;
    }
}
