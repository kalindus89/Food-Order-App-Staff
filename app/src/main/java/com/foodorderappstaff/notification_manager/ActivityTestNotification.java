package com.foodorderappstaff.notification_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.foodorderappstaff.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityTestNotification extends AppCompatActivity {

    Button sendMsg;
    String userID="userID", title="User Title", message="user message";
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_notification);

        sendMsg=findViewById(R.id.sendMsg);


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  Snackbar.make(view,"this is test text",Snackbar.LENGTH_SHORT).show();
                DocumentReference nycRef = FirebaseFirestore.getInstance().collection("FoodOrders").document("94777171342");

                nycRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Toast.makeText(getApplicationContext(),document.get("messagingToken").toString(), Toast.LENGTH_SHORT).show();;
                                sendNotifications(document.get("messagingToken").toString(),title,message);
                            } else {
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Not ok big", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

      //  updateFirebaseToken();
    }
  /*  private void updateFirebaseToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "token receive failed", Toast.LENGTH_SHORT).show();

                            return;
                        }

                        String refreshToken = task.getResult();

                       // Toast.makeText(getApplicationContext(), refreshToken, Toast.LENGTH_SHORT).show();

                        FirebaseFirestore.getInstance().document("FoodOrders/94777171342").update("messagingToken", refreshToken);

                    }
                });

    }*/

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
      //  Toast.makeText(getApplicationContext(), "111111 ", Toast.LENGTH_LONG).show();
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {

                    if (response.body().success != 1) {
                        Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}