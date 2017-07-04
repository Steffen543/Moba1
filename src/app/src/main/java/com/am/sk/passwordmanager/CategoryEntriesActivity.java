package com.am.sk.passwordmanager;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.List;

import Adapter.CategoryListAdapter;
import Adapter.PasswordListAdapter;
import Data.Category.CategoryDataSource;
import Data.Password.PasswordDataSource;
import Model.CategoryModel;
import Model.ISearchable;
import Model.PasswordModel;

/**
 * Created by Steffen on 22.06.2017.
 */

public class CategoryEntriesActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener {

    private CategoryModel category;
    private CategoryDataSource categoryDataSource;
    private PasswordDataSource passwordDataSource;
    private PasswordListAdapter passwordAdapter;
    private ListView passwordListView;
    private int counterSelections = 0;
    public static final String LOG_TAG = CategoryEntriesActivity.class.getSimpleName();
    private ActionMode _actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryDataSource = new CategoryDataSource(this);
        passwordDataSource = new PasswordDataSource(this);
        passwordListView = (ListView) findViewById(R.id.passwordListView);

        Intent i = getIntent();
        int categoryId =  Integer.parseInt(i.getStringExtra("categoryid"));
        categoryDataSource.open();
        passwordDataSource.open();

        category = categoryDataSource.getCategory(categoryId);

        FloatingActionButton addPasswordButton = (FloatingActionButton) findViewById(R.id.buttonAddPassword);
        addPasswordButton.setOnClickListener(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        initView();
        initializeContextualActionBar();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_category_menu, menu);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            //SearchView search = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
            //search.setOnQueryTextListener(this);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                  this.finish();
                return true;
            case R.id.menuEditCategory:
                final Intent intent = new Intent(this, EditCategoryActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView()
    {
        setTitle(category.getName());

        List<PasswordModel> passwords = passwordDataSource.getAllPasswords(category.getId());
        passwordAdapter = new PasswordListAdapter(this, passwords);
        passwordListView.setAdapter(passwordAdapter);
        passwordListView.setOnItemClickListener(this);
    }

    private void initializeContextualActionBar()  {

        passwordListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        passwordListView.setMultiChoiceModeListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        categoryDataSource.open();
        passwordDataSource.open();
        category = categoryDataSource.getCategory(category.getId());
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        categoryDataSource.close();
        passwordDataSource.close();
        if(_actionMode != null)
            _actionMode.finish();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.buttonAddPassword:
                showAddDialog();
                break;
        }
    }

    private void showAddDialog()
    {
        final CategoryEntriesActivity thisActivity = this;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        final View dialogsView = getLayoutInflater().inflate(R.layout.dialog_add_password, null);
        builder.setView(dialogsView);
        builder.setTitle("Passwort hinzufügen");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {/* will be later overwritten */ }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        final android.support.v7.app.AlertDialog addPasswordDialog = builder.create();
        addPasswordDialog.show();

        addPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editTextName = (EditText) dialogsView.findViewById(R.id.add_password_name);
                String newName = editTextName.getText().toString();

                if (!TextUtils.isEmpty(newName)) {
                    PasswordModel pwd = passwordDataSource.createPasswordModel(newName, "", "", "", "", category.getId());
                    final Intent intent = new Intent(thisActivity, EditPasswordActivity.class);
                    intent.putExtra("passwordId", String.valueOf(pwd.getId()));
                    intent.putExtra("autofocus", true);
                    startActivity(intent);

                    initView();
                    addPasswordDialog.dismiss();
                }
            }
        });

        EditText editTextName = (EditText) dialogsView.findViewById(R.id.add_password_name);
        if(editTextName.requestFocus()) {
            addPasswordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final PasswordModel item = (PasswordModel) adapterView.getItemAtPosition(i);

        final Intent intent = new Intent(this, EditPasswordActivity.class);
        intent.putExtra("passwordId", String.valueOf(item.getId()));
        startActivity(intent);
    }




    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int i, long l, boolean checked) {
        if(checked)
            counterSelections++;
        else counterSelections--;
        String cabTitle = counterSelections + " " + getString(R.string.cab_checked_string);
        mode.setTitle(cabTitle);
        mode.invalidate();
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        _actionMode = actionMode;
        getMenuInflater().inflate(R.menu.activity_main_contextual_action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cab_delete:

                final SparseBooleanArray touchedPasswordPositions = passwordListView.getCheckedItemPositions();


                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                for (int i=0; i < touchedPasswordPositions.size(); i++) {
                                    boolean isChecked = touchedPasswordPositions.valueAt(i);
                                    if(isChecked) {
                                        int postitionInListView = touchedPasswordPositions.keyAt(i);
                                        PasswordModel password = (PasswordModel) passwordListView.getItemAtPosition(postitionInListView);
                                        Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + password.toString());
                                        passwordDataSource.deletePassword(password);
                                    }
                                }
                                initView();
                                mode.finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Wirklich " + touchedPasswordPositions.size() + " Passwörter löschen?").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Abbrechen", dialogClickListener).show();



                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        counterSelections = 0;
    }
}
