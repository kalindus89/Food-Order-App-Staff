package com.foodorderappstaff.all_order_status;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodorderappstaff.R;
import com.foodorderappstaff.SessionManagement;

public class AdapterOrderStatus extends FirebaseRecyclerAdapter<OrderPlacedModel, AdapterOrderStatus.OderStatusViewHolder> {

    Context context;
    FirebaseRecyclerOptions<OrderPlacedModel> options;

    public AdapterOrderStatus(@NonNull FirebaseRecyclerOptions<OrderPlacedModel> options, Context context) {
        super(options);
        this.context = context;
        this.options = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull OderStatusViewHolder holder, int position, @NonNull OrderPlacedModel model) {

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle("Select the action");
                contextMenu.add(0,0,holder.getAbsoluteAdapterPosition(), SessionManagement.UPDATE);
                contextMenu.add(0,0,holder.getAbsoluteAdapterPosition(), SessionManagement.DELETE);

            }
        });
        holder.order_id.setText("Oder ID: "+options.getSnapshots().getSnapshot(position).getKey());

        if(model.getStatus().equals("1")){
            holder.order_status.setText("Status: Order Placed");
            holder.order_status.setTextColor(Color.parseColor("#54BF2D"));
        }else if(model.getStatus().equals("2")){
            holder.order_status.setText("Status: Shipping");
            holder.order_status.setTextColor(Color.parseColor("#F32183"));
        }else{
            holder.order_status.setText("Status: Completed");
            holder.order_status.setTextColor(Color.parseColor("#2196F3"));
        }

        holder.order_total.setText("Total: $"+model.getTotal());
        holder.order_address.setText("Address: "+model.getAddress());

    }

    @NonNull
    @Override
    public OderStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status,parent,false);
        return new OderStatusViewHolder(view);
    }

    public class OderStatusViewHolder extends RecyclerView.ViewHolder {
        private TextView order_id,order_status,order_total,order_address;

        public OderStatusViewHolder(@NonNull View itemView) {
            super(itemView);

            order_id =itemView.findViewById(R.id.order_id);
            order_status =itemView.findViewById(R.id.order_status);
            order_total =itemView.findViewById(R.id.order_total);
            order_address =itemView.findViewById(R.id.order_address);
        }
    }


}
