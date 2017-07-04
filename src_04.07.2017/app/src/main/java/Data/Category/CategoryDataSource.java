package Data.Category;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Model.CategoryModel;

/**
 * Created by Steffen on 23.06.2017.
 */

public class CategoryDataSource {

    private static final String LOG_TAG = CategoryDataSource.class.getSimpleName();

    private String[] columns = {
            DatabaseHelper.CATEGORY_TABLE.COLUMN_ID,
            DatabaseHelper.CATEGORY_TABLE.COLUMN_NAME,
            DatabaseHelper.CATEGORY_TABLE.COLUMN_DESCRIPTION,
            DatabaseHelper.CATEGORY_TABLE.COLUMN_ADDED,
            DatabaseHelper.CATEGORY_TABLE.COLUMN_EDITED
    };


    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;


    public CategoryDataSource(Context context) {
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

    private CategoryModel cursorToCategoryModel(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.CATEGORY_TABLE.COLUMN_ID);
        int idName = cursor.getColumnIndex(DatabaseHelper.CATEGORY_TABLE.COLUMN_NAME);
        int idDescription = cursor.getColumnIndex(DatabaseHelper.CATEGORY_TABLE.COLUMN_DESCRIPTION);
        int idAdded = cursor.getColumnIndex(DatabaseHelper.CATEGORY_TABLE.COLUMN_ADDED);
        int idEdited = cursor.getColumnIndex(DatabaseHelper.CATEGORY_TABLE.COLUMN_EDITED);

        int id = cursor.getInt(idIndex);
        String name = cursor.getString(idName);
        String description = cursor.getString(idDescription);
        String added = cursor.getString(idAdded);
        String edited = cursor.getString(idEdited);

        CategoryModel category = new CategoryModel(id, name, description, added, edited);

        return category;
    }

    public CategoryModel createCategoryModel(String name, String description) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORY_TABLE.COLUMN_NAME, name);
        values.put(DatabaseHelper.CATEGORY_TABLE.COLUMN_DESCRIPTION, description);
        String currentDate = Helper.DateTimeConverter.DateToString(new Date());
        values.put(DatabaseHelper.CATEGORY_TABLE.COLUMN_ADDED, currentDate);
        values.put(DatabaseHelper.CATEGORY_TABLE.COLUMN_EDITED, currentDate);

        long insertId = database.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);

        Cursor cursor = database.query(DatabaseHelper.TABLE_CATEGORIES,
                columns, DatabaseHelper.CATEGORY_TABLE.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        CategoryModel categoryModel = cursorToCategoryModel(cursor);
        cursor.close();

        categoryModel.setChildCount(0);

        return categoryModel;
    }

    public CategoryModel getCategory(int id)
    {
        //database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_CATEGORIES,
                columns, " _id = ?", new String[] {String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();
        CategoryModel category = null;

        while(!cursor.isAfterLast()) {

            category = cursorToCategoryModel(cursor);
            cursor.close();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            long count = DatabaseUtils.queryNumEntries(db, "passwords",
                    DatabaseHelper.PASSWORD_TABLE.COLUMN_PARENTID + "=?", new String[] { String.valueOf(category.getId())});
            category.setChildCount(count);
            return category;

        }
        return null;


    }

    public List<CategoryModel> getAllCategories() {
        List<CategoryModel> categoryList = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_CATEGORIES,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        CategoryModel category;

        while(!cursor.isAfterLast()) {
            category = cursorToCategoryModel(cursor);

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            long count = DatabaseUtils.queryNumEntries(db, "passwords",
                    DatabaseHelper.PASSWORD_TABLE.COLUMN_PARENTID + "=?", new String[] { String.valueOf(category.getId())});
            category.setChildCount(count);


            categoryList.add(category);
            Log.d(LOG_TAG, "ID: " + category.getId() + ", Inhalt: " + category.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return categoryList;
    }

    public void deleteCategory(CategoryModel category) {
        long id = category.getId();

        database.delete(DatabaseHelper.TABLE_CATEGORIES,
                DatabaseHelper.CATEGORY_TABLE.COLUMN_ID + "=" + id,
                null);

        database.delete(DatabaseHelper.TABLE_PASSWORDS,
                DatabaseHelper.PASSWORD_TABLE.COLUMN_PARENTID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + category.toString());
    }

    public CategoryModel updateCategory(long id, String name, String description) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORY_TABLE.COLUMN_NAME, name);
        values.put(DatabaseHelper.CATEGORY_TABLE.COLUMN_DESCRIPTION, description);
        values.put(DatabaseHelper.CATEGORY_TABLE.COLUMN_EDITED, Helper.DateTimeConverter.DateToString(new Date()));

        database.update(DatabaseHelper.TABLE_CATEGORIES,
                values,
                DatabaseHelper.CATEGORY_TABLE.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(DatabaseHelper.TABLE_CATEGORIES,
                columns, DatabaseHelper.CATEGORY_TABLE.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        CategoryModel category = cursorToCategoryModel(cursor);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "passwords",
                DatabaseHelper.PASSWORD_TABLE.COLUMN_PARENTID + "=?", new String[] { String.valueOf(category.getId())});
        category.setChildCount(count);

        cursor.close();

        return category;
    }

}
