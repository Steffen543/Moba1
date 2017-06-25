package com.am.sk.passwordmanager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.loginButton:
                final Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                super.finish();
                break;
        }
    }


}
