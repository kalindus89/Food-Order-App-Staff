package com.foodorderappstaff.view_order_foods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.foodorderappstaff.R;

import java.text.SimpleDateFormat;

public class FoodViewAdapter extends FirestoreRecyclerAdapter<CartModel, FoodViewAdapter.CartViewHolder> {


    Context context;
    FirestoreRecyclerOptions<CartModel> fireStoreRecyclerOptions;

    public FoodViewAdapter(Context context, @NonNull FirestoreRecyclerOptions<CartModel> fireStoreRecyclerOptions) {
        super(fireStoreRecyclerOptions);
        this.context = context;
        this.fireStoreRecyclerOptions = fireStoreRecyclerOptions;
    }
    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartModel model) {


        holder.cart_item_name.setText(model.getProductName());

        holder.cart_item_quantity.setText(model.getQuantity());

        holder.cart_item_price.setText("Total: $"+model.getPrice()+" x "+(model.getQuantity())+" = $"+model.getItemTotal());

        String date_time =(new SimpleDateFormat("EEEE MMM d - hh.mm aa").format(model.getOrderTime()));
        holder.cart_item_date.setText(date_time);

    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foods_only,parent,false);
        return new CartViewHolder(view);
    }


    public class CartViewHolder extends RecyclerView.ViewHolder {
       private TextView cart_item_name,cart_item_price,cart_item_date,cart_item_quantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cart_item_name =itemView.findViewById(R.id.cart_item_name);
            cart_item_price = itemView.findViewById(R.id.cart_item_price);
            cart_item_date = itemView.findViewById(R.id.cart_item_date);
            cart_item_quantity = itemView.findViewById(R.id.cart_item_quantity);
        }
    }


}
