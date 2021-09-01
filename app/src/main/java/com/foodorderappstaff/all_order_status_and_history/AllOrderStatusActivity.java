package com.foodorderappstaff.all_order_status_and_history;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodorderappstaff.R;
import com.foodorderappstaff.SessionManagement;
import com.foodorderappstaff.notification_manager.APIService;
import com.foodorderappstaff.notification_manager.Client;
import com.foodorderappstaff.notification_manager.Data;
import com.foodorderappstaff.notification_manager.MyResponse;
import com.foodorderappstaff.notification_manager.NotificationSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllOrderStatusActivity extends AppCompatActivity {


    ImageView goBack;
    RecyclerView recyclerView;
    AdapterOrderStatus adapterOrderStatus;

    MaterialSpinner materialSpinner;
    String selectStatus="";
    FirebaseRecyclerOptions<OrderPlacedModel> allUserNotes;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        goBack = findViewById(R.id.go_back);
        recyclerView = findViewById(R.id.recyclerView);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Query query = FirebaseDatabase.getInstance().getReference("PlaceOrders");
        allUserNotes   = new FirebaseRecyclerOptions.Builder<OrderPlacedModel>().setQuery(query, OrderPlacedModel.class).build();
        adapterOrderStatus  = new AdapterOrderStatus(allUserNotes,getApplicationContext());
        recyclerView.setAdapter(adapterOrderStatus);
        adapterOrderStatus.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterOrderStatus.startListening();
        recyclerView.setAdapter(adapterOrderStatus);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterOrderStatus.stopListening();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals(SessionManagement.UPDATE)) {
            //  Toast.makeText(this, catAdapter.getRef(item.getOrder()).getKey(), Toast.LENGTH_SHORT).show();
          //  Toast.makeText(this, allUserNotes.getSnapshots().get(item.getOrder()).getAddress(), Toast.LENGTH_SHORT).show();
          //  Toast.makeText(this, String.valueOf(allUserNotes.getSnapshots().size()), Toast.LENGTH_SHORT).show();

          getDriverOrder(item);

        } else {
          //  deleteCategory(catAdapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);

    }

    private void getDriverOrder(MenuItem item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AllOrderStatusActivity.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");
        alertDialog.setPositiveButton("Update", null);
        alertDialog.setNegativeButton("Cancel", null);

        LayoutInflater inflater = this.getLayoutInflater();
        View view =inflater.inflate(R.layout.spinner_order_update,null);

        materialSpinner = (MaterialSpinner)view.findViewById(R.id.materialSpinner);
        materialSpinner.setItems("--Select--","Get Order");
        alertDialog.setView(view);


        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if(item.equals("Get Order")){
                    selectStatus="1";
                }else {
                    selectStatus="0";
                }
            }
        });

        final AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (!selectStatus.isEmpty() && !selectStatus.equals("0")) {

                            ProgressDialog dialog = ProgressDialog.show(AllOrderStatusActivity.this, "",
                                    "Placing Order. Please wait...", true);
                            dialog.show();

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderStatus").child(new SessionManagement().getPhone(getApplicationContext())).child("Ongoing").child(adapterOrderStatus.getRef(item.getOrder()).getKey());
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    OrderPlacedModel user = new OrderPlacedModel(allUserNotes.getSnapshots().get(item.getOrder()).getName(),
                                            allUserNotes.getSnapshots().get(item.getOrder()).getPhone(), allUserNotes.getSnapshots().get(item.getOrder()).getAddress(),
                                            allUserNotes.getSnapshots().get(item.getOrder()).getTotal());
                                    user.setStatus(selectStatus);
                                    databaseReference.setValue(user);

                                    notifyCustomer(allUserNotes.getSnapshots().get(item.getOrder()).getPhone(),adapterOrderStatus.getRef(item.getOrder()).getKey());

                                    FirebaseFirestore.getInstance().document("FoodOrders/"+allUserNotes.getSnapshots().get(item.getOrder()).getPhone()+"/orderFoods/00000orderHistory/ongoingOrderIds/"+adapterOrderStatus.getRef(item.getOrder()).getKey()).update("status",selectStatus);

                                    FirebaseDatabase.getInstance().getReference().child("PlaceOrders").child(adapterOrderStatus.getRef(item.getOrder()).getKey()).removeValue();

                                    dialog.dismiss();
                                    mAlertDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else{
                            Snackbar.make(view,"Please select your status!",Snackbar.LENGTH_SHORT).show();

                        }
                    }
                });
                Button bn = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                bn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        mAlertDialog.dismiss();
                    }
                });
            }
        });
        mAlertDialog.show();

    }

    private void notifyCustomer(String phoneNumber,String orderNumber) {

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        DocumentReference nycRef = FirebaseFirestore.getInstance().collection("FoodOrders").document(phoneNumber);

        nycRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        sendNotifications(document.get("messagingToken").toString(),"Order No. "+orderNumber,"Your Food is now preparing");
                    } else {
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Not ok big", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendNotifications(String userToken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, userToken);
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