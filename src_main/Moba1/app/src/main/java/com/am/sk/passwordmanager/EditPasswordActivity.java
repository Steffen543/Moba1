package com.am.sk.passwordmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import Data.Password.PasswordDataSource;
import Helper.ClipboardHelper;
import Model.PasswordModel;

public class EditPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private PasswordDataSource passwordDataSource;
    private PasswordModel password;

    private EditText editTextName;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextEmail;
    private EditText editTextDescription;
    private TextView addedTextView;
    private TextView editedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        passwordDataSource = new PasswordDataSource(this);

        Intent i = getIntent();
        int passwordId = Integer.parseInt(i.getStringExtra("passwordId"));
        passwordDataSource.open();
        password = passwordDataSource.getPassword(passwordId);



        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ImageButton copyButton = (ImageButton) findViewById(R.id.CopyPasswordButton);
        ImageButton showPasswordButton = (ImageButton) findViewById(R.id.ShowPasswordButton);
        ImageButton createPasswordButton = (ImageButton) findViewById(R.id.CreatePasswordButton);
        copyButton.setOnClickListener(this);
        showPasswordButton.setOnClickListener(this);
        createPasswordButton.setOnClickListener(this);

        editTextName = (EditText) findViewById(R.id.namePasswordText);
        editTextUsername = (EditText) findViewById(R.id.usernamePasswordText);
        editTextEmail = (EditText) findViewById(R.id.emailPasswordText);
        editTextPassword = (EditText) findViewById(R.id.passwordPasswordText);
        editTextDescription = (EditText) findViewById(R.id.descriptionPasswordText);
        addedTextView = (TextView) findViewById(R.id.addedPasswordTextView);
        editedTextView = (TextView) findViewById(R.id.editedPasswordTextView);

        editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        initView();

        if(i.hasExtra("autofocus"))
        {
            boolean autofocus = (boolean) i.getSerializableExtra("autofocus");
            if(autofocus)
            {
                if(editTextEmail.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_edit_password_menu, menu);
        return true;
    }

    private void initView()
    {
        editTextName.setText(password.getName());
        editTextEmail.setText(password.getEmail());
        editTextUsername.setText(password.getUsername());
        editTextPassword.setText(password.getPassword());
        editTextDescription.setText(password.getDescription());
        addedTextView.setText("Erstellt am " + password.getAdded());
        editedTextView.setText("Zuletzt bearbeitet am " + password.getEdited());
        setTitle(password.getName() + " bearbeiten");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.menuDeletePassword:
                final EditPasswordActivity activity = this;
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                passwordDataSource.deletePassword(password);
                                password = null;
                                activity.finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Dieses Passwort wirklich löschen?").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Abbrechen", dialogClickListener).show();
                return true;
             default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        String newName = editTextName.getText().toString();
        String newDescription = editTextDescription.getText().toString();

        String newEmail = editTextEmail.getText().toString();
        String newUsername = editTextUsername.getText().toString();
        String newPassword = editTextPassword.getText().toString();

        if(password != null)
        {
            password = passwordDataSource.updatePassword(password.getId(), newName, newEmail, newUsername, newPassword, newDescription);
            initView();
        }


        passwordDataSource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        passwordDataSource.open();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {

            case R.id.CopyPasswordButton:

                String copyText = editTextPassword.getText().toString();
                ClipboardHelper.setClipboard(this, copyText);
                Toast.makeText(this, "Passwort kopiert", Toast.LENGTH_SHORT).show();

                break;
            case R.id.ShowPasswordButton:
                ImageButton btn = (ImageButton) findViewById(R.id.ShowPasswordButton);
                if(editTextPassword.getInputType() == InputType.TYPE_CLASS_TEXT)
                {
                    editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btn.setImageResource(R.drawable.ic_visibility_white_24dp);
                }
                else
                {
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btn.setImageResource(R.drawable.ic_visibility_off_white_24dp);
                }
                break;
            case R.id.CreatePasswordButton:

                OpenCreatePasswordDialog();

                break;
        }
    }

    private void OpenCreatePasswordDialog() {
        final EditPasswordActivity thisActivity = this;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        final View dialogsView = getLayoutInflater().inflate(R.layout.dialog_create_password, null);
        builder.setView(dialogsView);
        builder.setTitle("Passwort generieren");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {/* will be later overwritten */ }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        final android.support.v7.app.AlertDialog createPasswordDialog = builder.create();

        SeekBar numberSeekBar = dialogsView.findViewById(R.id.seekBarCountCharacters);

        final TextView headerCountLetters = dialogsView.findViewById(R.id.textViewPasswordCount);

        numberSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                headerCountLetters.setText("Passwort Länge: " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        numberSeekBar.setProgress(10);

        createPasswordDialog.show();
        createPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean useLetters =  ((CheckBox) dialogsView.findViewById(R.id.checkBoxLetters)).isChecked();
                boolean useNumbers =  ((CheckBox) dialogsView.findViewById(R.id.checkBoxNumber)).isChecked();
                boolean useSpecialCharacters =  ((CheckBox) dialogsView.findViewById(R.id.checkBoxSpecialCharacters)).isChecked();
                int countNumbers = ((SeekBar) dialogsView.findViewById(R.id.seekBarCountCharacters)).getProgress();

                if(!useLetters && !useNumbers && !useSpecialCharacters)
                    return;

                String randomString = Helper.RandomTextGenerator.GenerateText(countNumbers, useLetters, useNumbers, useSpecialCharacters);

                createPasswordDialog.dismiss();
                ClipboardHelper.setClipboard(thisActivity, randomString);
                editTextPassword.setText(randomString);
            }
        });

    }
}
