package Data.Category;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    class CATEGORY_TABLE
    {
        static final String COLUMN_ID = "_id";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_DESCRIPTION = "description";
        static final String COLUMN_ADDED= "added";
        static final String COLUMN_EDITED = "edited";
    }

    public class PASSWORD_TABLE
    {
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ADDED= "added";
        public static final String COLUMN_EDITED = "edited";
        public static final String COLUMN_PARENTID = "parentId";
    }

    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();
    private static final String DB_NAME = "passwordmanager.db";
    private static final int DB_VERSION = 1;

    static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_PASSWORDS = "passwords";



    private static final String SQL_CREATE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES +
                    "(" + CATEGORY_TABLE.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CATEGORY_TABLE.COLUMN_NAME + " TEXT NOT NULL, " +
                    CATEGORY_TABLE.COLUMN_DESCRIPTION + " TEXT, " +
                    CATEGORY_TABLE.COLUMN_ADDED + " TEXT, " +
                    CATEGORY_TABLE.COLUMN_EDITED + " TEXT);";

    private static final String SQL_CREATE_PASSWORDS =
            "CREATE TABLE " + TABLE_PASSWORDS +
                    "(" + PASSWORD_TABLE.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PASSWORD_TABLE.COLUMN_NAME + " TEXT NOT NULL, " +
                    PASSWORD_TABLE.COLUMN_EMAIL + " TEXT, " +
                    PASSWORD_TABLE.COLUMN_USERNAME + " TEXT, " +
                    PASSWORD_TABLE.COLUMN_DESCRIPTION + " TEXT, " +
                    PASSWORD_TABLE.COLUMN_PASSWORD + " TEXT, " +
                    PASSWORD_TABLE.COLUMN_ADDED + " TEXT, " +
                    PASSWORD_TABLE.COLUMN_EDITED + " TEXT, " +
                    PASSWORD_TABLE.COLUMN_PARENTID + " int);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            //Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE_CATEGORIES);
            db.execSQL(SQL_CREATE_PASSWORDS);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
