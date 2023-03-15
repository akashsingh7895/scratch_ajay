package com.ajsofttech.earn.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.ajsofttech.earn.R;
import com.ajsofttech.earn.fragments.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout_Login, LoginFragment.newInstance()).commit();

    }
}