package com.foodorderappstaff.send_notification_all_subscribers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foodorderappstaff.R;
import com.foodorderappstaff.WelcomeActivity;
import com.foodorderappstaff.notification_manager.APIService;
import com.foodorderappstaff.notification_manager.Client;
import com.foodorderappstaff.notification_manager.Data;
import com.foodorderappstaff.notification_manager.MyResponse;
import com.foodorderappstaff.notification_manager.NotificationSender;
import com.foodorderappstaff.singin_signup.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationsAllActivity extends AppCompatActivity {

    Button btnNotifyAll;
    MaterialEditText titleMsg,bodyTitle;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notifications_all_subscribers);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnNotifyAll = findViewById(R.id.btnNotifyAll );
        titleMsg = findViewById(R.id.titleMsg );
        bodyTitle = findViewById(R.id.bodyTitle );
        progressBar = findViewById(R.id.progressBar );

        btnNotifyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnNotifyAll.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                createMessageInDatabase();
            }
        });
    }

    private void createMessageInDatabase() {

        Map<String, Object> map = new HashMap<>();
        map.put("title", titleMsg.getText().toString());
        map.put("message",bodyTitle.getText().toString());

        FirebaseDatabase.getInstance().getReference("News").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    sendNotificationsToAllSubscribers(titleMsg.getText().toString(),bodyTitle.getText().toString());
                }
            }
        });
    }

    public void sendNotificationsToAllSubscribers( String title, String message) {

        APIService  apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        Data notification = new Data(title, message);
        NotificationSender toTopic = new NotificationSender();

        toTopic.to =new StringBuilder("/topics/").append("news").toString();
        toTopic.data=notification;

        apiService.sendNotification(toTopic).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                if(response.isSuccessful()){

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Message send", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Message send failed", Toast.LENGTH_SHORT).show();
                    btnNotifyAll.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });

    }
}