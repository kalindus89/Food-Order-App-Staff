package com.foodorderappstaff.all_foods_home;

import android.content.Context;
import android.content.Intent;
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
import com.squareup.picasso.Picasso;

public class AdapterCategory extends FirebaseRecyclerAdapter<CategoryModel,AdapterCategory.CatViewHolder> {

    Context context;
    FirebaseRecyclerOptions<CategoryModel> options;


    public AdapterCategory(@NonNull FirebaseRecyclerOptions<CategoryModel> options, Context context) {
        super(options);
        this.context = context;
        this.options = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull CatViewHolder holder, int position, @NonNull CategoryModel model) {


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent = new Intent(context, FoodsListActivity.class);
                intent.putExtra("cat_id",getRef(holder.getAbsoluteAdapterPosition()).getKey());
                context.startActivity(intent);*/
            }
        });

        holder.catName.setText(model.getName());
        Picasso.get().load(model.getImage()).placeholder(R.drawable.loading_image).into(holder.cateImage);

    }


    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_layout,parent,false);
        return new CatViewHolder(view);
    }

    public class CatViewHolder extends RecyclerView.ViewHolder {
        private TextView catName;
        private ImageView cateImage;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);

            cateImage =itemView.findViewById(R.id.cateImage);
            catName = itemView.findViewById(R.id.catName);
        }
    }


}
