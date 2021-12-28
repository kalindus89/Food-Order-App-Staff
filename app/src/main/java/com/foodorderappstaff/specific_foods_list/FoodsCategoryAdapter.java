package com.foodorderappstaff.specific_foods_list;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodorderappstaff.R;
import com.foodorderappstaff.SessionManagement;
import com.squareup.picasso.Picasso;

public class FoodsCategoryAdapter extends FirebaseRecyclerAdapter<FoodsModel, FoodsCategoryAdapter.FoodViewHolder> {

    Context context;
    FirebaseRecyclerOptions<FoodsModel> options;

    public FoodsCategoryAdapter(@NonNull FirebaseRecyclerOptions<FoodsModel> options, Context context) {
        super(options);
        this.context = context;
        this.options = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull FoodsModel model) {

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle("Select the action");
                contextMenu.add(0,0,holder.getAbsoluteAdapterPosition(), SessionManagement.UPDATE);
                contextMenu.add(0,0,holder.getAbsoluteAdapterPosition(), SessionManagement.DELETE);

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent intent = new Intent(context, FoodDetailActivity.class);
                intent.putExtra("food_id",getRef(holder.getAbsoluteAdapterPosition()).getKey());
                context.startActivity(intent);*/


            }
        });

        holder.foodName.setText(model.getName());
        Picasso.get().load(model.getImage()).placeholder(R.drawable.loading_new).into(holder.foodImage);

    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_list,parent,false);
        return new FoodViewHolder(view);
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private TextView foodName;
        private ImageView foodImage;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage =itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
        }
    }
}
