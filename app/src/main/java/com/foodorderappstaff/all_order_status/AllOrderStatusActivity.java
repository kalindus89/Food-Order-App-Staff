package com.foodorderappstaff.all_order_status;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
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
import com.foodorderappstaff.all_foods_home.CategoryModel;
import com.foodorderappstaff.all_foods_home.HomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AllOrderStatusActivity extends AppCompatActivity {


    ImageView goBack;
    RecyclerView recyclerView;
    AdapterOrderStatus adapterOrderStatus;

    MaterialSpinner materialSpinner;
    FirebaseRecyclerOptions<OrderPlacedModel> allUserNotes;
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
            Toast.makeText(this, allUserNotes.getSnapshots().get(item.getOrder()).getAddress(), Toast.LENGTH_SHORT).show();

            getDriverOrder(adapterOrderStatus.getRef(item.getOrder()).getKey());

        } else {
          //  deleteCategory(catAdapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);

    }

    private void getDriverOrder( String key) {




        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AllOrderStatusActivity.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");
        alertDialog.setPositiveButton("Update", null);
        alertDialog.setNegativeButton("Cancel", null);

        LayoutInflater inflater = this.getLayoutInflater();
        View view =inflater.inflate(R.layout.spinner_order_update,null);

        materialSpinner = (MaterialSpinner)view.findViewById(R.id.materialSpinner);
        materialSpinner.setItems("Placed","On my way","Completed");
        alertDialog.setView(view);

        final AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("OrderStatus").child(new SessionManagement().getPhone(getApplicationContext())).child("Ongoing").child(key);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                               /* OrderPlacedModel user = new OrderPlacedModel(new SessionManagement().getName(getApplicationContext()),
                                        new SessionManagement().getPhone(getApplicationContext()), editText.getText().toString(),
                                        totalAmountTxt.getText().toString());
                                databaseReference.setValue(user);*/
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

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
}