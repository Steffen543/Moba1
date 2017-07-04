package com.am.sk.passwordmanager;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Adapter.CategoryListAdapter;
import Adapter.SearchListAdapter;
import Data.Category.CategoryDataSource;
import Data.Password.PasswordDataSource;
import Model.CategoryModel;
import Model.ISearchable;
import Model.PasswordModel;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, View.OnClickListener, AbsListView.MultiChoiceModeListener {

    private CategoryListAdapter categoryAdapter;
    private SearchListAdapter searchAdapter;
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private CategoryDataSource categoryDataSource;
    private PasswordDataSource passwordDataSource;
    private ListView categoryListView;
    private int counterSelections = 0;
    private ActionMode _actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        categoryDataSource = new CategoryDataSource(this);
        passwordDataSource = new PasswordDataSource(this);
        categoryListView = (ListView) findViewById(R.id.categoryListView);

        FloatingActionButton addCategoryButton = (FloatingActionButton) findViewById(R.id.buttonAddCategory);
        addCategoryButton.setOnClickListener(this);



        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_screen_lock_portrait_white_24dp);

        initializeContextualActionBar();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
            search.setOnCloseListener(this);
            search.setOnQueryTextListener(this);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final ISearchable item = (ISearchable) adapterView.getItemAtPosition(i);

        if(item instanceof CategoryModel) {
            CategoryModel category = (CategoryModel) item;
            final Intent intent = new Intent(this, CategoryEntriesActivity.class);
            intent.putExtra("categoryid", String.valueOf(category.getId()));
            startActivity(intent);
        }
        if(item instanceof PasswordModel) {
            PasswordModel password = (PasswordModel) item;
            final Intent intent = new Intent(this, EditPasswordActivity.class);
            intent.putExtra("passwordId", String.valueOf(password.getId()));
            startActivity(intent);
        }



    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        //categoryAdapter.getFilter().filter(s);
        categoryListView.setAdapter(searchAdapter);
        searchAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.buttonAddCategory:
                showAddDialog();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        categoryDataSource.open();
        passwordDataSource.open();
        initializeCategories();
        initializeSearch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        categoryDataSource.close();
        passwordDataSource.close();
        if(_actionMode != null)
            _actionMode.finish();
    }

    private void initializeSearch()
    {
        List<CategoryModel> categories = categoryDataSource.getAllCategories();
        List<PasswordModel> passwords = passwordDataSource.getAllPasswords();

        List<ISearchable> searchables = new ArrayList<ISearchable>();

        for(PasswordModel pwd : passwords)
            searchables.add(pwd);
        for(CategoryModel category : categories)
            searchables.add(category);


        searchAdapter = new SearchListAdapter(this, searchables);
    }

    private void showAddDialog()  {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        final View dialogsView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogsView);
        builder.setTitle("Kategorie hinzufügen");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {/* will be later overwritten */ }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        final android.support.v7.app.AlertDialog addCategoryDialog = builder.create();
        addCategoryDialog.show();
        addCategoryDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editTextName = (EditText) dialogsView.findViewById(R.id.add_category_name);
                String newName = editTextName.getText().toString();

                if (!TextUtils.isEmpty(newName)) {
                    CategoryModel category = categoryDataSource.createCategoryModel(newName, "");
                    initializeCategories();
                    addCategoryDialog.dismiss();
                }
            }
        });

        EditText editTextName = (EditText) dialogsView.findViewById(R.id.add_category_name);
        if(editTextName.requestFocus()) {
            addCategoryDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }


    }

    private void initializeContextualActionBar()  {

        categoryListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        categoryListView.setMultiChoiceModeListener(this);
    }

    private void initializeCategories()  {
        List<CategoryModel> categories = categoryDataSource.getAllCategories();
        categoryAdapter = new CategoryListAdapter(this, categories);
        categoryListView.setAdapter(categoryAdapter);
        categoryListView.setOnItemClickListener(this);
    }



    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int i, long l, boolean checked) {
        if(checked) {
            counterSelections++;
        }
        else {
            counterSelections--;
        }

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

                final SparseBooleanArray touchedCategoriesPositions = categoryListView.getCheckedItemPositions();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                for (int i=0; i < touchedCategoriesPositions.size(); i++) {
                                    boolean isChecked = touchedCategoriesPositions.valueAt(i);
                                    if(isChecked) {
                                        int postitionInListView = touchedCategoriesPositions.keyAt(i);
                                        ISearchable checkedObj = (ISearchable) categoryListView.getItemAtPosition(postitionInListView);

                                        if(checkedObj instanceof CategoryModel) {
                                            CategoryModel category = (CategoryModel) checkedObj;
                                            Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + category.toString());
                                            categoryDataSource.deleteCategory(category);
                                        }
                                        if(checkedObj instanceof PasswordModel) {
                                            PasswordModel password = (PasswordModel) checkedObj;
                                            Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + password.toString());
                                            passwordDataSource.deletePassword(password);
                                        }

                                    }
                                }
                                initializeCategories();
                                mode.finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Wirklich " + touchedCategoriesPositions.size() + " Einträge löschen?").setPositiveButton("Ja", dialogClickListener)
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

    @Override
    public boolean onClose() {
        categoryListView.setAdapter(categoryAdapter);;
        return false;
    }
}
