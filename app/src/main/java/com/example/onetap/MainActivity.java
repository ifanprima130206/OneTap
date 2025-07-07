package com.example.onetap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.onetap.adapter.LatestNewsAdapter;
import com.example.onetap.adapter.NewsAdapter;
import com.example.onetap.api.NewsClient;
import com.example.onetap.api.NewsService;
import com.example.onetap.model.NewsItem;
import com.example.onetap.model.NewsResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private ViewPager2 viewPager;
    private LinearLayout indicatorLayout;
    private Handler autoScrollHandler = new Handler();
    private RecyclerView latestList;
    private List<NewsItem> carouselItems = new ArrayList<>();
    private List<NewsItem> combinedNews = new ArrayList<>();
    private List<NewsItem> latestNews = new ArrayList<>();
    private LatestNewsAdapter latestAdapter;
    private int expectedResponses;
    private int actualResponses;
    private int itemsPerPage = 7;
    private int currentPage = 0;
    private boolean isLoadingMore = false;
    private ImageView btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        indicatorLayout = findViewById(R.id.indicator_layout);
        viewPager = findViewById(R.id.news_carousel);
        latestList = findViewById(R.id.latest_news_list);

        setSupportActionBar(toolbar);

        Drawable navIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu);
        Drawable circleBg = ContextCompat.getDrawable(this, R.drawable.circle_gray);
        LayerDrawable layeredIcon = new LayerDrawable(new Drawable[]{circleBg, navIcon});
        layeredIcon.setLayerInset(1, 12, 12, 12, 12);
        toolbar.setNavigationIcon(layeredIcon);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        navigationView.setNavigationItemSelectedListener(this);

        fetchNewsFromApi();

        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

    }
    private void fetchNewsFromApi() {
        NewsService newsService = NewsClient.getRetrofit().create(NewsService.class);
        String[] sources = {"antara", "cnbc", "cnn", "jpnn", "kumparan", "merdeka",
//                "okezone",
                "republika",
//                "sindonews",
                "suara", "tempo", "tribun"};
        String[] categories = {"bisnis", "bola", "cantik", "celebrity", "creativelab", "daerah", "dunia", "economy", "edukasi", "ekbis", "ekonomi", "entertainment", "event", "gayahidup", "gaya", "health", "hiburan", "humaniora", "infografis", "inforial", "internasional", "international", "investment", "islam", "jakarta", "jateng", "kalam", "kesehatan", "khazanah", "khas", "lifestyle", "market", "metro", "nasional", "opini", "olahraga", "otomotif", "parapuan", "politik", "profil", "sains", "sehat", "seleb", "sports", "sport", "superskor", "syariah", "tekno", "teknologi", "tech", "techno", "travel"};

        expectedResponses = sources.length * categories.length;
        actualResponses = 0;

        for (String source : sources) {
            for (String category : categories) {
                newsService.getNewsByCategory(source, category).enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        actualResponses++;
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<NewsItem> posts = response.body().data.posts;
                            for (NewsItem item : posts) {
                                item.setCategory(category);
                            }
                            combinedNews.addAll(posts);
                        }
                        if (actualResponses == expectedResponses) {
                            SharedData.setNewsList(combinedNews);
                            showNews(combinedNews);
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        actualResponses++;
                        if (actualResponses == expectedResponses) {
                            SharedData.setNewsList(combinedNews);
                            showNews(combinedNews);
                        }
                    }
                });
            }
        }
    }

    private void showNews(List<NewsItem> newsList) {
        // 1. Urutkan untuk carousel (breaking news)
        List<NewsItem> sortedNews = new ArrayList<>(newsList);
        Collections.sort(sortedNews, new Comparator<NewsItem>() {
            @Override
            public int compare(NewsItem n1, NewsItem n2) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date d1 = sdf.parse(n1.getPubDate());
                    Date d2 = sdf.parse(n2.getPubDate());
                    return d2.compareTo(d1); // Descending
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        // BREAKING NEWS
        carouselItems = sortedNews.subList(0, Math.min(5, sortedNews.size()));
        NewsAdapter adapter = new NewsAdapter(carouselItems); // pastikan adapter kamu mendukung infinite scroll
        viewPager.setAdapter(adapter);

        // 👇 TAMBAHAN UNTUK INFINITE SCROLL
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2, false); // mulai dari tengah biar bisa geser kiri-kanan terus
        setupIndicator();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int realPosition = position % carouselItems.size();
                setCurrentIndicator(realPosition);
            }
        });

        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(pageMarginPx));
        transformer.addTransformer((page, position) -> {
            float scale = 0.85f + (1 - Math.abs(position)) * 0.15f;
            page.setScaleY(scale);
        });
        viewPager.setPageTransformer(transformer);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        // viewPager.setCurrentItem(0, false); // ❌ tidak perlu lagi karena sudah pakai MAX_VALUE/2
        setCurrentIndicator(0);

        autoScrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int current = viewPager.getCurrentItem();
                viewPager.setCurrentItem(current + 1, true);
                autoScrollHandler.postDelayed(this, 3000);
            }
        }, 3000);

        // 2. Acak untuk recommendation
        List<NewsItem> shuffledNews = new ArrayList<>(newsList);
        Collections.shuffle(shuffledNews);

        latestNews.clear();
        latestNews.addAll(shuffledNews);

        List<NewsItem> initialNews = getNextPage();
        latestAdapter = new LatestNewsAdapter(initialNews);
        latestList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        latestList.setAdapter(latestAdapter);

        latestAdapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(MainActivity.this, DetailNewsActivity.class);
            intent.putExtra("news", item); // kirim objek langsung
            startActivity(intent);
        });



        latestList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1) && !isLoadingMore) {
                    isLoadingMore = true;
                    recyclerView.postDelayed(() -> {
                        List<NewsItem> more = getNextPage();
                        latestAdapter.addMoreNews(more);
                        isLoadingMore = false;
                    }, 1000);
                }
            }
        });
    }
    private List<NewsItem> getNextPage() {
        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, latestNews.size());
        currentPage++;
        return latestNews.subList(start, end);
    }

    private void setupIndicator() {
        indicatorLayout.removeAllViews();
        ImageView[] indicators = new ImageView[carouselItems.size()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            indicatorLayout.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = indicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) indicatorLayout.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive));
            }
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawers(); // Menutup drawer saat item diklik
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            showToast("Home clicked");
        } else if (id == R.id.nav_earthquake) {
            Intent intent = new Intent(MainActivity.this, EarthquakeActivity.class);
            startActivity(intent);
        }
//        else if (id == R.id.nav_weather) {
//            showToast("Weather belum tersedia");
//        }

        return true;
    }
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
