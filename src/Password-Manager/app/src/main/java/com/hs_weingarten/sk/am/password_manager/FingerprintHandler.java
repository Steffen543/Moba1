package com.hs_weingarten.sk.am.password_manager;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback{

    // The CancellationSignal should be used whenever the app can't process user input, like when it goes into background
    // When you don't do this other apps can't use the fingerprintsensor, even the lockscreen
    private CancellationSignal cancellationSignal;
    private Context context;
    private MainActivity mainActivity;

    public FingerprintHandler(Context mContext){
        context = mContext;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject, MainActivity main){
        mainActivity = main;
        cancellationSignal = new CancellationSignal();
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    // fatal error has occurred
    public void onAuthenticationError(int errMsgId, CharSequence errString){
        Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
    }

    // fingerprint doesn't match with the registered fingerprints in the device
    public void onAuthenticationFailed(){
        Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show();
    }

    // non-fatal error has occurred
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString){
        Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
    }

    // authentication success
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result){
        mainActivity.MainApp();
        Toast.makeText(context, "LogIn completed!", Toast.LENGTH_SHORT).show();
    }
}