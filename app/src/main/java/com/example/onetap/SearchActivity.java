package com.example.onetap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onetap.adapter.SearchNewsAdapter;
import com.example.onetap.model.NewsItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private RecyclerView searchRecycler;
    private ImageView btnBack;
    private ProgressBar loadingIndicator;
    private TextView noResultsText;
    private SearchNewsAdapter searchAdapter;

    private List<NewsItem> allNews;
    private List<NewsItem> filteredNews;
    private List<NewsItem> displayedNews;

    // Pagination variables
    private int itemsPerPage = 15;
    private int currentPage = 0;
    private boolean isLoadingMore = false;
    private String currentQuery = "";

    // Optimization variables
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private ExecutorService searchExecutor = Executors.newSingleThreadExecutor();
    private static final int SEARCH_DELAY = 300; // 300ms debounce

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_optimized);

        initViews();
        setupRecyclerView();
        setupSearchFunctionality();
        setupBackButton();
    }

    private void initViews() {
        searchInput = findViewById(R.id.search_input);
        searchRecycler = findViewById(R.id.search_recycler);
        btnBack = findViewById(R.id.btn_back);
        loadingIndicator = findViewById(R.id.loading_indicator);
        noResultsText = findViewById(R.id.no_results_text);

        // Ambil semua data berita dari SharedData
        allNews = new ArrayList<>(SharedData.getNewsList());
        filteredNews = new ArrayList<>();
        displayedNews = new ArrayList<>();

        // Hide loading and no results initially
        loadingIndicator.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        searchAdapter = new SearchNewsAdapter(displayedNews);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        searchRecycler.setLayoutManager(layoutManager);
        searchRecycler.setAdapter(searchAdapter);

        searchAdapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(SearchActivity.this, DetailNewsActivity.class);
            intent.putExtra("news", item);
            startActivity(intent);
        });

        searchRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Load more when 80% scrolled (not 100%)
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Load more when 80% scrolled
                    if (!isLoadingMore && !currentQuery.isEmpty() &&
                            (visibleItemCount + firstVisibleItemPosition) >= totalItemCount * 0.8) {
                        loadMoreSearchResults();
                    }
                }
            }
        });
    }

    private void setupSearchFunctionality() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                // ✅ DEBOUNCING: Cancel previous search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // ✅ Create new search task with delay
                searchRunnable = () -> performSearch(query);
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchInput.requestFocus();
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void performSearch(String query) {
        currentQuery = query;
        resetPagination();

        if (query.isEmpty()) {
            showNoResults(false);
            return;
        }

        // Show loading
        showLoading(true);

        // ✅ BACKGROUND THREAD: Perform search in background
        searchExecutor.execute(() -> {
            List<NewsItem> results = filterNewsInBackground(query);

            // ✅ MAIN THREAD: Update UI
            runOnUiThread(() -> {
                showLoading(false);
                filteredNews.clear();
                filteredNews.addAll(results);

                if (results.isEmpty()) {
                    showNoResults(true);
                } else {
                    showNoResults(false);
                    loadInitialSearchResults();
                }
            });
        });
    }

    private List<NewsItem> filterNewsInBackground(String query) {
        List<NewsItem> results = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();

        // ✅ OPTIMIZATION: Limit search to recent news only (last 1000 items)
        List<NewsItem> recentNews = allNews.size() > 1000 ?
                allNews.subList(0, 1000) : allNews;

        for (NewsItem news : recentNews) {
            if (matchesQuery(news, lowerCaseQuery)) {
                results.add(news);
            }

            // ✅ OPTIMIZATION: Limit results to 500 max
            if (results.size() >= 500) {
                break;
            }
        }

        // Sort by date (latest first)
        Collections.sort(results, (n1, n2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date d1 = sdf.parse(n1.getPubDate());
                Date d2 = sdf.parse(n2.getPubDate());
                return d2.compareTo(d1);
            } catch (Exception e) {
                return 0;
            }
        });

        return results;
    }

    private boolean matchesQuery(NewsItem news, String query) {
        // ✅ OPTIMIZATION: Check title first (most common match)
        if (news.getTitle() != null && news.getTitle().toLowerCase().contains(query)) {
            return true;
        }

        // ✅ OPTIMIZATION: Check category (faster than description)
        if (news.getCategory() != null && news.getCategory().toLowerCase().contains(query)) {
            return true;
        }

        // ✅ OPTIMIZATION: Check description last (most expensive)
        if (news.getDescription() != null && news.getDescription().toLowerCase().contains(query)) {
            return true;
        }

        return false;
    }

    private void resetPagination() {
        currentPage = 0;
        isLoadingMore = false;
        displayedNews.clear();
        searchAdapter.notifyDataSetChanged();
    }

    private void loadInitialSearchResults() {
        displayedNews.clear();
        currentPage = 0;

        List<NewsItem> initialResults = getNextPage();
        displayedNews.addAll(initialResults);
        searchAdapter.notifyDataSetChanged();
    }

    private void loadMoreSearchResults() {
        if (isLoadingMore || filteredNews.isEmpty()) return;

        isLoadingMore = true;

        // ✅ OPTIMIZATION: No delay for pagination (faster UX)
        List<NewsItem> moreResults = getNextPage();
        if (!moreResults.isEmpty()) {
            int startPosition = displayedNews.size();
            displayedNews.addAll(moreResults);
            searchAdapter.notifyItemRangeInserted(startPosition, moreResults.size());
        }
        isLoadingMore = false;
    }

    private List<NewsItem> getNextPage() {
        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, filteredNews.size());

        if (start >= filteredNews.size()) {
            return new ArrayList<>();
        }

        currentPage++;
        return new ArrayList<>(filteredNews.subList(start, end));
    }

    private void showLoading(boolean show) {
        loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        searchRecycler.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showNoResults(boolean show) {
        noResultsText.setVisibility(show ? View.VISIBLE : View.GONE);
        searchRecycler.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ✅ CLEANUP: Shutdown executor
        if (searchExecutor != null) {
            searchExecutor.shutdown();
        }
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
