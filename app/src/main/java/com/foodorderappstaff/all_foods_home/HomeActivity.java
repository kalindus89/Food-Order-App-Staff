package com.foodorderappstaff.all_foods_home;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodorderappstaff.R;
import com.foodorderappstaff.SessionManagement;
import com.foodorderappstaff.WelcomeActivity;
import com.foodorderappstaff.all_order_status_and_history.AllOrderStatusActivity;
import com.foodorderappstaff.all_order_status_and_history.HistoryJobOrdersActivity;
import com.foodorderappstaff.all_order_status_and_history.CurrentJobOrdersActivity;
import com.foodorderappstaff.notification_manager.ActivityTestNotification;
import com.foodorderappstaff.notification_manager.ListenAllOrdersService;
import com.foodorderappstaff.send_notification_all_subscribers.SendNotificationsAllActivity;
import com.foodorderappstaff.updateBanners.UpdateBannerActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ImageView drawerIcon, searchIcon, backToMainPage;
    TextView textView;
    RecyclerView recyclerView;
    EditText searchKeyword;
    FloatingActionButton addNewMenu;

    LinearLayout searchLayout, ll_First, ll_Second, current_status, ll_Third, ll_Fourth, ll_Fifth, ll_Sixth, send_Msg_all;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    AdapterCategory catAdapter;

    FirebaseRecyclerOptions<CategoryModel> allUserNotes;
    ArrayList<CategoryModel> categoryModelArrayList;

    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recyclerView = findViewById(R.id.recyclerView);
        addNewMenu = findViewById(R.id.addNewMenu);
        searchIcon = findViewById(R.id.searchIcon);
        textView = findViewById(R.id.textView);
        searchLayout = findViewById(R.id.searchLayout);
        backToMainPage = findViewById(R.id.backToMainPage);
        searchKeyword = findViewById(R.id.searchKeyword);

        onSetNavigationDrawerEvents();

        categoryModelArrayList = new ArrayList<>();


        addNewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogBoxToUploadNewMenu(true, "");

            }
        });

        backToMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIcon.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.GONE);
                searchKeyword.setText("");
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIcon.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);

            }
        });

        searchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString() != null) {
                    searchData(editable.toString());

                } else {
                    searchData("");
                }

            }
        });

        loadData();

        //register service to get New notifications
        Intent service = new Intent(HomeActivity.this, ListenAllOrdersService.class);
        startService(service);

    }

    private void showDialogBoxToUploadNewMenu(boolean isNew, String keyId) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Add New Category");
        alertDialog.setMessage("Please fill all information");
        alertDialog.setPositiveButton("Upload", null);
        alertDialog.setNegativeButton("Cancel", null);

        final EditText editText = new EditText(HomeActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_menu_layout, null);

        editText.setHint("Category");
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        EditText nameMenu = add_menu_layout.findViewById(R.id.nameMenu);
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

                        ProgressDialog dialog = ProgressDialog.show(HomeActivity.this, "",
                                "Category Uploading. Please wait...", true);
                        dialog.show();
                        if (nameMenu.getText().toString().isEmpty() || saveUri == null) {

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
                                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Category").push();
                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        CategoryModel user = new CategoryModel(nameMenu.getText().toString(), uri.toString());
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
                                            } else {
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


    private void loadData() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Query query = FirebaseDatabase.getInstance().getReference().child("Category");

        allUserNotes = new FirebaseRecyclerOptions.Builder<CategoryModel>().setQuery(query, CategoryModel.class).build();
        catAdapter = new AdapterCategory(allUserNotes, this);

        recyclerView.setAdapter(catAdapter);
        catAdapter.updateOptions(allUserNotes);
        catAdapter.notifyDataSetChanged();

    }

    private void searchData(String keyword) {

        Query query = FirebaseDatabase.getInstance().getReference().child("Category").orderByChild("name").startAt(keyword).endAt(keyword + "\uf8ff");

        FirebaseRecyclerOptions<CategoryModel> allUserNotes2 = new FirebaseRecyclerOptions.Builder<CategoryModel>().setQuery(query, CategoryModel.class).build();
        catAdapter = new AdapterCategory(allUserNotes2, this);

        recyclerView.setAdapter(catAdapter);
        catAdapter.startListening();
        catAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        catAdapter.startListening();
        recyclerView.setAdapter(catAdapter);
    }

    private void onSetNavigationDrawerEvents() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        //drawerLayout.openDrawer(GravityCompat.END);

        drawerIcon = (ImageView) findViewById(R.id.drawerIcon);
        TextView userName = findViewById(R.id.userName);
        userName.setText(new SessionManagement().getName(this));
        ll_First = (LinearLayout) findViewById(R.id.ll_First);
        ll_Second = (LinearLayout) findViewById(R.id.ll_Second);
        current_status = (LinearLayout) findViewById(R.id.current_status);
        ll_Third = (LinearLayout) findViewById(R.id.ll_Third);
        ll_Fourth = (LinearLayout) findViewById(R.id.ll_Fourth);
        ll_Fifth = (LinearLayout) findViewById(R.id.ll_Fifth);
        ll_Sixth = (LinearLayout) findViewById(R.id.ll_Sixth);
        send_Msg_all = (LinearLayout) findViewById(R.id.send_Msg_all);

        drawerIcon.setOnClickListener(this);
        ll_First.setOnClickListener(this);
        ll_Second.setOnClickListener(this);
        current_status.setOnClickListener(this);
        ll_Third.setOnClickListener(this);
        ll_Fourth.setOnClickListener(this);
        ll_Fifth.setOnClickListener(this);
        ll_Sixth.setOnClickListener(this);
        send_Msg_all.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drawerIcon:
                drawerLayout.openDrawer(navigationView, true);
                break;
            case R.id.ll_First:
                showToast("ll_First");
                drawerLayout.closeDrawer(navigationView, true);
                break;
            case R.id.ll_Second:

                Intent intent = new Intent(HomeActivity.this, CurrentJobOrdersActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView, true);
                break;
            case R.id.current_status:
                drawerLayout.closeDrawer(navigationView, true);
                Intent intent2 = new Intent(HomeActivity.this, AllOrderStatusActivity.class);
                startActivity(intent2);
                break;
            case R.id.ll_Third:
                showToast("ll_Third");
                drawerLayout.closeDrawer(navigationView, true);
                Intent intent3 = new Intent(HomeActivity.this, HistoryJobOrdersActivity.class);
                startActivity(intent3);
                break;
            case R.id.ll_Fourth:
                showToast("ll_Fourth");
                drawerLayout.closeDrawer(navigationView, true);
                Intent intent4 = new Intent(HomeActivity.this, UpdateBannerActivity.class);
                startActivity(intent4);
                break;
            case R.id.send_Msg_all:
                drawerLayout.closeDrawer(navigationView, true);
                Intent intent5 = new Intent(HomeActivity.this, SendNotificationsAllActivity.class);
                startActivity(intent5);
                break;
            case R.id.ll_Fifth:
                showToast("ll_Fifth");
                drawerLayout.closeDrawer(navigationView, true);
                Intent intent6 = new Intent(HomeActivity.this, ActivityTestNotification.class);
                startActivity(intent6);
                break;
            case R.id.ll_Sixth:
                showToast("tv_logout");
                drawerLayout.closeDrawer(navigationView, true);
                new SessionManagement().setUserName(this, "no number", "no name", "log out");
                Intent intent7 = new Intent(HomeActivity.this, WelcomeActivity.class);
                startActivity(intent7);
                finish();
                break;
            default:
                showToast("Default");
                drawerLayout.closeDrawer(navigationView, true);
                break;

        }


    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView, true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals(SessionManagement.UPDATE)) {
            //  Toast.makeText(this, catAdapter.getRef(item.getOrder()).getKey(), Toast.LENGTH_SHORT).show();

            showDialogBoxToUploadNewMenu(false, catAdapter.getRef(item.getOrder()).getKey());

        } else {
            deleteCategory(catAdapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);

    }

    private void deleteCategory(String key) {

        FirebaseDatabase.getInstance().getReference().child("Category").child(key).removeValue();
    }
}