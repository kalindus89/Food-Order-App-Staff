package com.foodorderappstaff;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.foodorderappstaff.all_foods_home.HomeActivity;
import com.foodorderappstaff.singin_signup.SignInActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button btnSignIn;

   @Override
    protected void onStart() {
        super.onStart();

        if(new SessionManagement().isLogin(this)==true){
            Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnSignIn = findViewById(R.id.btnSignIn );

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }
}