package com.foodorderappstaff.specific_foods_list;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FoodsListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView goBack;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FoodsCategoryAdapter foodAdapter;
    FloatingActionButton add_new_foods;
    StorageReference storageReference;
    String catId;
    private final int PICK_IMAGE_REQUEST = 71;
    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods_list);

        recyclerView=findViewById(R.id.recyclerView);
        goBack=findViewById(R.id.goBack);
        add_new_foods=findViewById(R.id.add_new_foods);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add_new_foods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogBoxToUploadNewMenu(true,null);
            }
        });

        catId =getIntent().getStringExtra("cat_id");

        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Query query=FirebaseDatabase.getInstance().getReference().child("Foods").orderByChild("menuID").equalTo(catId);

        FirebaseRecyclerOptions<FoodsModel> allUserNotes = new FirebaseRecyclerOptions.Builder<FoodsModel>().setQuery(query, FoodsModel.class).build();
        foodAdapter  = new FoodsCategoryAdapter(allUserNotes,this);

        recyclerView.setAdapter(foodAdapter);
        foodAdapter.notifyDataSetChanged();

    }

    private void showDialogBoxToUploadNewMenu(boolean isNew, MenuItem item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodsListActivity.this);
        alertDialog.setTitle("Add New Category");
        alertDialog.setMessage("Please fill all information");
        alertDialog.setPositiveButton("Upload", null);
        alertDialog.setNegativeButton("Cancel", null);

        final EditText editText = new EditText(FoodsListActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);

        editText.setHint("Category");
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        EditText food_name = add_menu_layout.findViewById(R.id.food_name);
        EditText food_description = add_menu_layout.findViewById(R.id.food_description);
        EditText food_price = add_menu_layout.findViewById(R.id.food_price);
        EditText food_discount = add_menu_layout.findViewById(R.id.food_discount);

        Button btnSelect = add_menu_layout.findViewById(R.id.btnSelect);

        if(item!=null){
            food_name.setText(foodAdapter.getItem(item.getOrder()).getName());
            food_description.setText(foodAdapter.getItem(item.getOrder()).getDescription());
            food_price.setText(foodAdapter.getItem(item.getOrder()).getPrice());
            food_discount.setText(foodAdapter.getItem(item.getOrder()).getDiscount());
        }

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
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

                        ProgressDialog dialog = ProgressDialog.show(FoodsListActivity.this, "",
                                "Food Uploading. Please wait...", true);
                        dialog.show();

                        if (food_name.getText().toString().isEmpty() ||food_description.getText().toString().isEmpty() ||
                                food_price.getText().toString().isEmpty() ||food_discount.getText().toString().isEmpty() ||
                                saveUri == null) {

                            Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {

                            String imageName = UUID.randomUUID().toString();
                            storageReference = FirebaseStorage.getInstance().getReference().child("All-Food-Images").child(imageName);
                            storageReference.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            if (isNew == true) {
                                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Foods").push();
                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        FoodsModel user = new FoodsModel(food_name.getText().toString(), uri.toString(),
                                                                food_description.getText().toString(),food_discount.getText().toString(),
                                                                food_price.getText().toString(),catId);
                                                        databaseReference.setValue(user);

                                                        Toast.makeText(getApplicationContext(), "Food item added", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        mAlertDialog.dismiss();
                                                        saveUri = null;
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        dialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Failed Food item added", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Foods").child(foodAdapter.getRef(item.getOrder()).getKey());

                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("name", food_name.getText().toString());
                                                        map.put("image", uri.toString());
                                                        map.put("description", food_description.getText().toString());
                                                        map.put("discount",food_discount.getText().toString());
                                                        map.put("price", food_price.getText().toString());
                                                        databaseReference.updateChildren(map);
                                                        dialog.dismiss();
                                                        mAlertDialog.dismiss();
                                                        saveUri = null;
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }

                                        }
                                    });
                                }
                            });
                        }

                    }
                });
            }
        });
        mAlertDialog.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) // results of user selection image
        {
            saveUri = data.getData();
        }
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);


    }


    @Override
    protected void onStart() {
        super.onStart();
        foodAdapter.startListening();
        recyclerView.setAdapter(foodAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(foodAdapter!=null){
            foodAdapter.stopListening();
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals(SessionManagement.UPDATE)) {
             // Toast.makeText(this,, Toast.LENGTH_SHORT).show();

            showDialogBoxToUploadNewMenu(false, item);

        } else {
            deleteCategory(foodAdapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);

    }

    private void deleteCategory(String key) {

        FirebaseDatabase.getInstance().getReference().child("Foods").child(key).removeValue();
    }
}