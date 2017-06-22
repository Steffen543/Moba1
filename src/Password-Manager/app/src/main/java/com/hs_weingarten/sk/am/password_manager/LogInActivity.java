package com.hs_weingarten.sk.am.password_manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

// import android.support.design.widget.Snackbar;
// import android.text.method.HideReturnsTransformationMethod;
// import android.widget.RelativeLayout;

// import android.support.design.widget.CoordinatorLayout;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "Password-Manager";
    private static final String KEY = "fingerprintKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private FingerprintHandler helper;
    private FingerprintManager fingerprintManager;
    private FingerprintManager.CryptoObject cryptoObject;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    // private Snackbar login_info;
    // private CoordinatorLayout coordinatorLayout;
    // private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If the SDKVersion is lower than 23, a verification is needed that the device is running Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            if (!fingerprintManager.isHardwareDetected()) {
                // If the device isn't equiped with a fingerprintsensor
                PasswordSetCheck();
            }

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
                // If the user isn't getting us the permission for his fingerprintsensor
                PasswordSetCheck();
            }

            if(!fingerprintManager.hasEnrolledFingerprints()){
                // If the user hasn't configured any fingerprints
                PasswordSetCheck();
            }

            if(!keyguardManager.isKeyguardSecure()){
                // If the user hasn't secured his lockscreen
                PasswordSetCheck();
            }else{
                try{
                    generateKey();
                } catch (FingerprintException e){
                    e.printStackTrace();
                }

                if(initCipher()){
                    // If the initialization of the cipher was successful
                    setContentView(R.layout.fingerprint_layout);
                    Button changeToPassword = (Button) findViewById(R.id.change_button);
                    changeToPassword.setOnClickListener(changeOnClick);

                    cryptoObject = new FingerprintManager.CryptoObject(cipher);

                    helper = new FingerprintHandler(this);
                    helper.startAuth(fingerprintManager, cryptoObject, this);
                }
            }
        }else{
            PasswordSetCheck();
        }

    }

    @Override
    protected void onPause(){
        // needed to give other apps and lockscreen the permission of the fingerprintsensor
        if(helper != null){
            Log.d(TAG, "onPause: canceling the FingerPrintHandler");
            helper.cancellationSignal.cancel();
        }

        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume(){
        // needed to restart the fingerprintsensor
        helper.startAuth(fingerprintManager, cryptoObject, this);
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException{
        try{
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            // Generate the key
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            // Initialization of a empty KeyStore
            keyStore.load(null);
            // Initialization of the KeyGenerator
            keyGenerator.init(new
                    // operations this key can be used for
                    KeyGenParameterSpec.Builder(KEY,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // user has to authenticate each time they want to use the app
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            // generation of the key
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc){
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean initCipher(){
        try {
            // configuration of the cipher for the use of the fingerprint
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"   // AES-encrypt algorithm (Rijndael-algorithm); symmetric and free-to-use
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try{
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // cipher has been successfully initialized
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            // cipher initialization failed
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private class FingerprintException extends Exception{
        FingerprintException(Exception e){
            super(e);
        }
    }

    View.OnClickListener changeOnClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            PasswordSetCheck();
        }
    };

    @SuppressLint("CommitPrefEdits")
    private void PasswordSetCheck(){
        pref = getSharedPreferences("Password-Manager", MODE_PRIVATE);
        editor = pref.edit();
        //check, if password was set
        if(pref.getString("Password", "").equals("")){
            //password isn't set
            PasswordInput();
        }else {
            //password was set
            PasswordRequest();
        }
    }

    public void PasswordInput(){
        setContentView(R.layout.firstlogin_layout);
        Button saveButton = (Button) findViewById(R.id.save);
        // if clicked on the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckNewPassword();
            }
        });
    }

    public void CheckNewPassword(){
        // save new password by click on save
        EditText password1 = (EditText) findViewById(R.id.password);
        EditText password2 = (EditText) findViewById(R.id.password2);

        if(password1.getText().toString().length() == 0){
            Toast.makeText(LogInActivity.this, "Password need charackters", Toast.LENGTH_LONG).show();
        }else {
            // check if the passwords are equal
            if (password1.getText().toString().equals(password2.getText().toString())) {
                // passwords are equal. Save it and open the login window
                editor.putString("Password", password1.getText().toString());
                editor.commit();
                MainApp();
            } else {
                // passwords weren't equal
                // login_info.make(relativeLayout, "Passwords aren't equal", Snackbar.LENGTH_LONG).show();
                Toast.makeText(LogInActivity.this, "Passwords aren't equal", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void PasswordRequest(){
        setContentView(R.layout.login_layout);
        final EditText password = (EditText) findViewById(R.id.password);
        Button loginButton = (Button) findViewById(R.id.login);
        ImageButton eye = (ImageButton) findViewById(R.id.eye);
        // by click on the eye image the password will transform from dots to charackters
        eye.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // hide the keyboard if the eye is clicked
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(password.getWindowToken(), 0);

                switch ( event.getAction() ) {

                    case MotionEvent.ACTION_UP:
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        password.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                }
                return true;
            }
        });

        // login if password is correct
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // check if password is correct
                if(pref.getString("Password", "").equals(password.getText().toString())){
                    // password is correct, show MainApp
                    MainApp();
                }else {
                    // password isn't correct
                    // login_info.make(relativeLayout, "Password incorrect", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(LogInActivity.this, "Password incorrect", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void MainApp(){
        // show the MainApp
        // all methods of the LogInActivity

        setContentView(R.layout.activity_main);
    }
}
