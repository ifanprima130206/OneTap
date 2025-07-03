package com.example.onetap.adapter;

import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onetap.DetailNewsActivity;
import com.example.onetap.R;
import com.example.onetap.model.NewsItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;

    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        int realPosition = position % newsList.size();
        NewsItem news = newsList.get(realPosition);

        holder.title.setText(news.getTitle());

        String categoryText = news.getCategory();
        if (categoryText != null && !categoryText.isEmpty()) {
            categoryText = categoryText.substring(0, 1).toUpperCase() + categoryText.substring(1).toLowerCase();
        }
        holder.category.setText(categoryText);

        holder.publishDate.setText(getTimeAgo(news.getPubDate()));

        String author = getAuthorFromUrl(news.getLink());
        holder.author.setText(author);

        Glide.with(holder.itemView.getContext())
                .load(news.getThumbnail())
                .placeholder(R.drawable.news)
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailNewsActivity.class);
            intent.putExtra("news", news);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // infinite loop
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, category, author, publishDate;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.slider_image);
            title = itemView.findViewById(R.id.slider_title);
            category = itemView.findViewById(R.id.slider_category);
            author = itemView.findViewById(R.id.slider_author);
            publishDate = itemView.findViewById(R.id.slider_publish_date);
        }
    }

    private String getTimeAgo(String pubDate) {
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

    // Fungsi untuk menentukan sumber dari URL
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

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

}
