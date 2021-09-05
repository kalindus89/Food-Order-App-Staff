package com.foodorderappstaff.all_order_status_and_history;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodorderappstaff.R;
import com.foodorderappstaff.SessionManagement;
import com.foodorderappstaff.notification_manager.APIService;
import com.foodorderappstaff.notification_manager.Client;
import com.foodorderappstaff.notification_manager.Data;
import com.foodorderappstaff.notification_manager.MyResponse;
import com.foodorderappstaff.notification_manager.NotificationSender;
import com.foodorderappstaff.view_order_foods.ViewOrderFoods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterOrderStatus extends FirebaseRecyclerAdapter<OrderPlacedModel, AdapterOrderStatus.OderStatusViewHolder> {

    Activity context;
    FirebaseRecyclerOptions<OrderPlacedModel> options;
    boolean showUpdate;
    APIService apiService;
    String selectStatus = "";

    public AdapterOrderStatus(@NonNull FirebaseRecyclerOptions<OrderPlacedModel> options, Activity context, boolean showUpdate) {
        super(options);
        this.context = context;
        this.options = options;
        this.showUpdate = showUpdate;
    }

    @Override
    protected void onBindViewHolder(@NonNull OderStatusViewHolder holder, int position, @NonNull OrderPlacedModel model) {


        holder.order_id.setText("Oder ID: " + options.getSnapshots().getSnapshot(position).getKey());

        if (model.getStatus().equals("1")) {
            holder.order_status.setText("Status: Order Placed");
            holder.order_status.setTextColor(Color.parseColor("#54BF2D"));
        } else if (model.getStatus().equals("2")) {
            holder.order_status.setText("Status: Shipping");
            holder.order_status.setTextColor(Color.parseColor("#F32183"));
        } else if (model.getStatus().equals("0")) {
            holder.order_status.setText("Status: Waiting..");
            holder.order_status.setTextColor(Color.parseColor("#0C6C3A"));
        } else {
            holder.order_status.setText("Status: Completed");
            holder.order_status.setTextColor(Color.parseColor("#2196F3"));
        }

        holder.order_total.setText("Total: $" + model.getTotal());
        holder.order_address.setText("Address: " + model.getAddress());


        holder.menuPopButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setGravity(Gravity.END);

                if (showUpdate == true) {
                    popupMenu.getMenu().add("Update").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            getDriverOrder2(model, options.getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getKey());

                            return false;
                        }
                    });

                    popupMenu.getMenu().add("Directions").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {


                            return false;
                        }
                    });
                }

                popupMenu.getMenu().add("Details").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {


                      Intent intent = new Intent(context, ViewOrderFoods.class);
                        intent.putExtra("order_id", options.getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getKey());
                        intent.putExtra("order_status", model.getStatus());
                        intent.putExtra("order_total", model.getTotal());
                        intent.putExtra("order_address", model.getAddress());
                        intent.putExtra("user_number", model.getPhone());
                        context.startActivity(intent);

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @NonNull
    @Override
    public OderStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false);
        return new OderStatusViewHolder(view);
    }

    public class OderStatusViewHolder extends RecyclerView.ViewHolder {
        private TextView order_id, order_status, order_total, order_address;
        ImageView menuPopButton;

        public OderStatusViewHolder(@NonNull View itemView) {
            super(itemView);

            order_id = itemView.findViewById(R.id.order_id);
            order_status = itemView.findViewById(R.id.order_status);
            order_total = itemView.findViewById(R.id.order_total);
            order_address = itemView.findViewById(R.id.order_address);
            menuPopButton = itemView.findViewById(R.id.menuPopButton);
        }
    }

    private void getDriverOrder2(OrderPlacedModel model, String orderId) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");
        alertDialog.setPositiveButton("Update", null);
        alertDialog.setNegativeButton("Cancel", null);

        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.spinner_order_update, null);

        MaterialSpinner materialSpinner = (MaterialSpinner) view.findViewById(R.id.materialSpinner);
        if(model.getStatus().equals("0")) {
            materialSpinner.setItems("--Select--", "Get Order");
        }else{
            materialSpinner.setItems("--Select--","On my way","Completed");
        }
        alertDialog.setView(view);


        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if (item.equals("Get Order")) {
                    selectStatus = "1";
                } else if (item.equals("On my way")) {
                    selectStatus = "2";
                } else if (item.equals("Completed")) {
                    selectStatus = "3";
                } else {
                    selectStatus = "0";
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

                            ProgressDialog dialog = ProgressDialog.show(context, "", "Placing Order. Please wait...", true);
                            dialog.show();

                            if (selectStatus.equals("1")) {

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderStatus").child(new SessionManagement().getPhone(context)).child("Ongoing").child(orderId);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        OrderPlacedModel user = new OrderPlacedModel(model.getName(), model.getPhone(), model.getAddress(), model.getTotal());
                                        user.setStatus(selectStatus);
                                        databaseReference.setValue(user);

                                        notifyCustomer(model.getPhone(), orderId, "Your food is preparing");

                                        Map note = new HashMap();
                                        note.put("status", selectStatus);
                                        note.put("driverNumber",  new SessionManagement().getPhone(context));

                                        FirebaseFirestore.getInstance().document("FoodOrders/" + model.getPhone() + "/orderFoods/00000orderHistory/ongoingOrderIds/" + orderId).update(note);
                                        FirebaseFirestore.getInstance().document("FoodOrders/" + model.getPhone() + "/orderFoods/00000orderHistory/ongoingOrderIds/0000allOrders/placedOrderIds/" + orderId).update("driverNumber", new SessionManagement().getPhone(context));
                                        FirebaseDatabase.getInstance().getReference().child("PlaceOrders").child(orderId).removeValue();

                                        dialog.dismiss();
                                        mAlertDialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else if (selectStatus.equals("2")) {

                                Map note = new HashMap();
                                note.put("status", selectStatus);
                                FirebaseDatabase.getInstance().getReference().child("OrderStatus").child(new SessionManagement().getPhone(context)).child("Ongoing").child(orderId).updateChildren(note);

                                FirebaseFirestore.getInstance().document("FoodOrders/" + model.getPhone() + "/orderFoods/00000orderHistory/ongoingOrderIds/" + orderId).update("status", selectStatus);

                                notifyCustomer(model.getPhone(), orderId, "Your food is now on the way");

                                dialog.dismiss();
                                mAlertDialog.dismiss();
                            } else if (selectStatus.equals("3")) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderStatus").child(new SessionManagement().getPhone(context)).child("Completed").child(orderId);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        OrderPlacedModel user = new OrderPlacedModel(model.getName(), model.getPhone(), model.getAddress(), model.getTotal());
                                        user.setStatus(selectStatus);
                                        databaseReference.setValue(user);

                                        notifyCustomer(model.getPhone(), orderId, "Your food has arrived");

                                        FirebaseFirestore.getInstance().document("FoodOrders/" + model.getPhone() + "/orderFoods/00000orderHistory/ongoingOrderIds/" + orderId).delete();
                                        FirebaseFirestore.getInstance().document("FoodOrders/" + model.getPhone() + "/orderFoods/00000orderHistory/ongoingOrderIds/0000allOrders/placedOrderIds/" + orderId).update("status", selectStatus);

                                        FirebaseDatabase.getInstance().getReference().child("OrderStatus").child(new SessionManagement().getPhone(context)).child("Ongoing").child(orderId).removeValue();

                                        dialog.dismiss();
                                        mAlertDialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                Snackbar.make(view, "Please select your status!", Snackbar.LENGTH_SHORT).show();

                            }
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

    private void notifyCustomer(String phoneNumber, String orderNumber, String message) {

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        DocumentReference nycRef = FirebaseFirestore.getInstance().collection("FoodOrders").document(phoneNumber);

        nycRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        sendNotifications(document.get("messagingToken").toString(), "Order No. " + orderNumber, message);
                    } else {
                    }
                } else {
                    Toast.makeText(context, "Not ok big", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "Failed ", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

}
