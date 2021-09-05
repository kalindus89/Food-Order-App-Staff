package com.foodorderappstaff.updateBanners;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodorderappstaff.R;
import com.foodorderappstaff.all_foods_home.AdapterCategory;
import com.foodorderappstaff.all_foods_home.CategoryModel;
import com.foodorderappstaff.all_foods_home.HomeActivity;
import com.foodorderappstaff.specific_foods_list.FoodsListActivity;
import com.foodorderappstaff.specific_foods_list.FoodsModel;
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

public class UpdateBannerActivity extends AppCompatActivity {

    ImageView goBack;
    FloatingActionButton add_new_banner;
    RecyclerView recyclerView;
    private final int PICK_IMAGE_REQUEST = 71;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Uri saveUri;

    AdapterBanner bannerAdapter;

    FirebaseRecyclerOptions<CategoryModel> allUserNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_banner);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        goBack=findViewById(R.id.goBack);
        add_new_banner=findViewById(R.id.add_new_banner);
        recyclerView = findViewById(R.id.recyclerView);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add_new_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogBoxToUploadBanners(true,null);
            }
        });

        loadData();
    }

    private void loadData() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Query query = FirebaseDatabase.getInstance().getReference().child("Banners");

        allUserNotes = new FirebaseRecyclerOptions.Builder<CategoryModel>().setQuery(query, CategoryModel.class).build();
        bannerAdapter = new AdapterBanner(allUserNotes, this);

        recyclerView.setAdapter(bannerAdapter);
        bannerAdapter.updateOptions(allUserNotes);
        bannerAdapter.notifyDataSetChanged();

    }

    private void showDialogBoxToUploadBanners(boolean isNew, String keyId) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateBannerActivity.this);
        alertDialog.setTitle("Add New Category");
        alertDialog.setMessage("Please fill all information");
        alertDialog.setPositiveButton("Upload", null);
        alertDialog.setNegativeButton("Cancel", null);


        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_menu_layout, null);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        EditText nameMenu = add_menu_layout.findViewById(R.id.nameMenu);
        EditText foodId = add_menu_layout.findViewById(R.id.foodId);
        foodId.setVisibility(View.VISIBLE);
        Button btnSelect = add_menu_layout.findViewById(R.id.btnSelect);

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

                        ProgressDialog dialog = ProgressDialog.show(UpdateBannerActivity.this, "",
                                "Category Uploading. Please wait...", true);
                        dialog.show();
                        if (nameMenu.getText().toString().isEmpty() || saveUri == null|| foodId.getText().toString().isEmpty()) {

                            Toast.makeText(getApplicationContext(), "Fill both fields", Toast.LENGTH_SHORT).show();
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
                                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Banners").push();
                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        BannerModel user = new BannerModel(nameMenu.getText().toString(), uri.toString(),foodId.getText().toString());
                                                        databaseReference.setValue(user);

                                                        Toast.makeText(getApplicationContext(), "Menu item added", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        mAlertDialog.dismiss();
                                                        saveUri = null;
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        dialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Failed Menu item added", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } /*else {
                                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Category").child(keyId);

                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("name", nameMenu.getText().toString());
                                                        map.put("image", uri.toString());
                                                        databaseReference.updateChildren(map);
                                                        dialog.dismiss();
                                                        mAlertDialog.dismiss();
                                                        saveUri = null;
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }*/

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

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) // results of user selection image
        {
            saveUri = data.getData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bannerAdapter.startListening();
        recyclerView.setAdapter(bannerAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(bannerAdapter!=null){
            bannerAdapter.stopListening();
        }
    }
}