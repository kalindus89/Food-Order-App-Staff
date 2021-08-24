package com.foodorderappstaff.all_foods_home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodorderappstaff.R;
import com.foodorderappstaff.SessionManagement;
import com.foodorderappstaff.WelcomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ImageView drawerIcon, searchIcon, backToMainPage;
    TextView textView;
    RecyclerView recyclerView;
    EditText searchKeyword;

    LinearLayout searchLayout, ll_First, ll_Second, current_status, ll_Third, ll_Fourth, ll_Fifth, ll_Sixth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    AdapterCategory catAdapter;
    FloatingActionButton viewCart;

    FirebaseRecyclerOptions<CategoryModel> allUserNotes;
    ArrayList<CategoryModel> categoryModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recyclerView = findViewById(R.id.recyclerView);
        viewCart = findViewById(R.id.viewCart);
        searchIcon = findViewById(R.id.searchIcon);
        textView = findViewById(R.id.textView);
        searchLayout = findViewById(R.id.searchLayout);
        backToMainPage = findViewById(R.id.backToMainPage);
        searchKeyword = findViewById(R.id.searchKeyword);

        onSetNavigationDrawerEvents();

        categoryModelArrayList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Category");


        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(HomeActivity.this, FoodCartActivity.class));
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

    }



    private void loadData() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Query query = databaseReference;

        allUserNotes = new FirebaseRecyclerOptions.Builder<CategoryModel>().setQuery(query, CategoryModel.class).build();
        catAdapter = new AdapterCategory(allUserNotes, this);

        recyclerView.setAdapter(catAdapter);
        catAdapter.updateOptions(allUserNotes);
       catAdapter.notifyDataSetChanged();

    }

    private void searchData(String keyword) {

        Query query = databaseReference.orderByChild("name").startAt(keyword).endAt(keyword + "\uf8ff");

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

        drawerIcon.setOnClickListener(this);
        ll_First.setOnClickListener(this);
        ll_Second.setOnClickListener(this);
        current_status.setOnClickListener(this);
        ll_Third.setOnClickListener(this);
        ll_Fourth.setOnClickListener(this);
        ll_Fifth.setOnClickListener(this);
        ll_Sixth.setOnClickListener(this);
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

               // startActivity(new Intent(HomeActivity.this, FoodCartActivity.class));
                drawerLayout.closeDrawer(navigationView, true);
                break;
            case R.id.current_status:
                drawerLayout.closeDrawer(navigationView, true);
               // Intent intent2 = new Intent(HomeActivity.this, OrderStatusActivity.class);
               // startActivity(intent2);
                break;
            case R.id.ll_Third:
                showToast("ll_Third");
                drawerLayout.closeDrawer(navigationView, true);
                break;
            case R.id.ll_Fourth:
                showToast("ll_Fourth");
                drawerLayout.closeDrawer(navigationView, true);
                break;
            case R.id.ll_Fifth:
                showToast("ll_Fifth");
                drawerLayout.closeDrawer(navigationView, true);
                break;
            case R.id.ll_Sixth:
                showToast("tv_logout");
                drawerLayout.closeDrawer(navigationView, true);

                new SessionManagement().setUserName(this, "no number", "no name", "log out");

                Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                startActivity(intent);
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
}