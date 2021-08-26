package com.foodorderappstaff.order_status;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodorderappstaff.R;

import java.util.List;

public class AdapterOrderStatus2 extends  RecyclerView.Adapter<AdapterOrderStatus2.TempViewHolder>{

    List<OrderPlacedModel> orderPlacedModelList;
    Context context;

    public AdapterOrderStatus2(List<OrderPlacedModel> orderPlacedModelList,Context context) {

        this.context = context;
        this.orderPlacedModelList = orderPlacedModelList;
    }
    @Override
    public void onBindViewHolder(@NonNull AdapterOrderStatus2.TempViewHolder holder, int position) {

        holder.order_total.setText("Total: $"+orderPlacedModelList.get(position).getTotal());

        if(orderPlacedModelList.get(position).getStatus().equals("1")){
            holder.order_status.setText("Status: Shipping");
        }else if(orderPlacedModelList.get(position).getStatus().equals("2")){
            holder.order_status.setText("Status: Shipped");
        }else{
            holder.order_status.setText("Status: Placed");
        }

        holder.order_total.setText("Total: $"+orderPlacedModelList.get(position).getTotal());
        holder.order_address.setText("Address: "+orderPlacedModelList.get(position).getAddress());
    }

    @NonNull
    @Override
    public AdapterOrderStatus2.TempViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status,parent,false);
        return new TempViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return orderPlacedModelList.size();
    }

    public class TempViewHolder extends RecyclerView.ViewHolder {


        private TextView order_id,order_status,order_total,order_address;

        public TempViewHolder(@NonNull View itemView) {
            super(itemView);
            order_id =itemView.findViewById(R.id.order_id);
            order_status =itemView.findViewById(R.id.order_status);
            order_total =itemView.findViewById(R.id.order_total);
            order_address =itemView.findViewById(R.id.order_address);
        }
    }
}