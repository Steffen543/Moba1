package com.am.sk.passwordmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import Data.Category.CategoryDataSource;
import Model.CategoryModel;

/**
 * Created by Steffen on 23.06.2017.
 */

public class EditCategoryActivity extends AppCompatActivity {

    private CategoryDataSource categoryDataSource;
    private CategoryModel category;
    EditText editTextName;
    EditText editTextDescription;
    TextView addedTextView;
    TextView editedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        categoryDataSource = new CategoryDataSource(this);

        Intent i = getIntent();
        category = (CategoryModel) i.getSerializableExtra("category");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        editTextName = (EditText) findViewById(R.id.nameCategoryText);
        editTextDescription = (EditText) findViewById(R.id.descriptionCategoryText);
        addedTextView = (TextView) findViewById(R.id.addedCategoryTextView);
        editedTextView = (TextView) findViewById(R.id.editedCategoryTextView);


        InitView();
    }

    private void InitView()
    {
        setTitle(category.getName() + " bearbeiten");
        editTextName.setText(category.getName());
        editTextDescription.setText(category.getDescription());
        addedTextView.setText("Erstellt am " + category.getAdded());
        editedTextView.setText("Zuletzt bearbeitet am " + category.getEdited());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.menuDeleteCategory:
                final EditCategoryActivity activity = this;
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                categoryDataSource.deleteCategory(category);
                                final Intent mainIntent = new Intent(activity, MainActivity.class);
                                startActivity(mainIntent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Diese Kategorie wirklich löschen?").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Abbrechen", dialogClickListener).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activit_edit_category_menu, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        categoryDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();

        String newName = editTextName.getText().toString();
        String newDescription = editTextDescription.getText().toString();

        category = categoryDataSource.updateCategory(category.getId(), newName, newDescription);
        InitView();

        categoryDataSource.close();
    }

}
