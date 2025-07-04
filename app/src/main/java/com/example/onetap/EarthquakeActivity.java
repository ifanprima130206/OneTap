package com.example.onetap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onetap.adapter.EarthquakeAdapter;
import com.example.onetap.api.BmkgClient;
import com.example.onetap.api.BmkgService;
import com.example.onetap.model.EarthquakeItem;
import com.example.onetap.model.InfoGempa;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EarthquakeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private EarthquakeAdapter adapter;
    private MaterialToolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        // Initialize views
        initViews();

        // Setup toolbar and navigation
        setupToolbarAndNavigation();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch earthquake data
        fetchDataGempa();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recycler_earthquake);
        btnSearch = findViewById(R.id.btn_search);

        // Debug: Check if views are found
        Log.d("EarthquakeActivity", "toolbar: " + (toolbar != null));
        Log.d("EarthquakeActivity", "drawerLayout: " + (drawerLayout != null));
        Log.d("EarthquakeActivity", "navigationView: " + (navigationView != null));
        Log.d("EarthquakeActivity", "recyclerView: " + (recyclerView != null));
        Log.d("EarthquakeActivity", "btnSearch: " + (btnSearch != null));
    }

    private void setupToolbarAndNavigation() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            // Setup navigation icon dengan background seperti di MainActivity
            Drawable navIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu);
            Drawable circleBg = ContextCompat.getDrawable(this, R.drawable.circle_gray);
            if (navIcon != null && circleBg != null) {
                LayerDrawable layeredIcon = new LayerDrawable(new Drawable[]{circleBg, navIcon});
                layeredIcon.setLayerInset(1, 12, 12, 12, 12);
                toolbar.setNavigationIcon(layeredIcon);
            }

            // Set click listener untuk membuka drawer
            toolbar.setNavigationOnClickListener(v -> {
                Log.d("EarthquakeActivity", "Navigation icon clicked");
                if (drawerLayout != null && navigationView != null) {
                    drawerLayout.openDrawer(navigationView);
                }
            });
        }

        // Set navigation item selected listener
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Setup search button - dengan null check
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> {
                Intent intent = new Intent(EarthquakeActivity.this, SearchActivity.class);
                startActivity(intent);
            });
        } else {
            Log.w("EarthquakeActivity", "btnSearch is null - search functionality disabled");
        }
    }

    private void fetchDataGempa() {
        BmkgService service = BmkgClient.getInstance().create(BmkgService.class);
        service.getGempa().enqueue(new Callback<InfoGempa>() {
            @Override
            public void onResponse(Call<InfoGempa> call, Response<InfoGempa> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EarthquakeItem> list = response.body().getGempaList();
                    Log.d("EarthquakeActivity", "Jumlah data: " + list.size());
                    adapter = new EarthquakeAdapter(list);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("EarthquakeActivity", "Gagal parsing response");
                    Toast.makeText(EarthquakeActivity.this, "Gagal ambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InfoGempa> call, Throwable t) {
                Log.e("EarthquakeActivity", "Error: " + t.getMessage());
                Toast.makeText(EarthquakeActivity.this, "Gagal koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Log untuk debugging
        Log.d("EarthquakeActivity", "Menu item clicked: " + item.getTitle());

        // Tutup drawer terlebih dahulu
        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Log.d("EarthquakeActivity", "Home clicked - navigating to MainActivity");
            Intent intent = new Intent(EarthquakeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;

        } else if (id == R.id.nav_earthquake) {
            Log.d("EarthquakeActivity", "Earthquake clicked - already in earthquake page");
            showToast("Anda sudah di halaman Earthquake");
            return true;

        } else if (id == R.id.nav_weather) {
            Log.d("EarthquakeActivity", "Weather clicked");
            showToast("Weather belum tersedia");
            return true;
        }

        return false;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
