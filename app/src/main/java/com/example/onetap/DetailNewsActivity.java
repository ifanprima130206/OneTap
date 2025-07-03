package com.example.onetap;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.onetap.model.NewsItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DetailNewsActivity extends AppCompatActivity {

    private TextView titleText, categoryText, authorText, dateText, contentText;
    private ImageView thumbnailImage, btnBack, btnBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupClickListeners();
        loadNewsData();
    }

    private void initViews() {
        titleText = findViewById(R.id.detail_title);
        categoryText = findViewById(R.id.detail_category);
        authorText = findViewById(R.id.detail_author);
        dateText = findViewById(R.id.detail_date);
        contentText = findViewById(R.id.detail_content);
        thumbnailImage = findViewById(R.id.detail_image);
        btnBack = findViewById(R.id.btn_back);
        btnBookmark = findViewById(R.id.btn_bookmark);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnBookmark.setOnClickListener(v -> shareNews());
    }

    private void loadNewsData() {
        NewsItem news = (NewsItem) getIntent().getSerializableExtra("news");

        if (news != null) {
            titleText.setText(news.getTitle());
            categoryText.setText(capitalize(news.getCategory()));
            authorText.setText(getAuthorFromUrl(news.getLink()));
            dateText.setText(formatDate(news.getPubDate()));
            contentText.setText(news.getDescription());

            Glide.with(this)
                    .load(news.getThumbnail())
                    .centerCrop()
                    .into(thumbnailImage);
        }
    }

    private String formatDate(String pubDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(pubDate);
            long time = date.getTime();
            long now = System.currentTimeMillis();
            return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return "Waktu tidak valid";
        }
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    private String getAuthorFromUrl(String url) {
        if (url.contains("cnn")) return "CNN";
        if (url.contains("antara")) return "Antara";
        if (url.contains("tribun")) return "Tribun";
        if (url.contains("tempo")) return "Tempo";
        if (url.contains("jpnn")) return "JPNN";
        if (url.contains("kumparan")) return "Kumparan";
        if (url.contains("merdeka")) return "Merdeka";
        if (url.contains("cnbc")) return "CNBC";
        if (url.contains("republika")) return "Republika";
        if (url.contains("suara")) return "Suara";
        if (url.contains("okezone")) return "Okezone";
        if (url.contains("sindonews")) return "SindoNews";
        return "Media";
    }

    private void shareNews() {
        NewsItem news = (NewsItem) getIntent().getSerializableExtra("news");
        if (news != null) {
            String shareText = news.getTitle() + "\n\nBaca selengkapnya: " + news.getLink();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, news.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            startActivity(Intent.createChooser(shareIntent, "Bagikan berita via"));
        }
    }

}
