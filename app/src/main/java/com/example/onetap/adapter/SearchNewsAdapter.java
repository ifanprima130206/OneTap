package com.example.onetap.adapter;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onetap.R;
import com.example.onetap.model.NewsItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SearchNewsAdapter extends RecyclerView.Adapter<SearchNewsAdapter.SearchNewsViewHolder> {

    private final List<NewsItem> newsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NewsItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SearchNewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public SearchNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_news, parent, false);
        return new SearchNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchNewsViewHolder holder, int position) {
        NewsItem news = newsList.get(position);
        holder.title.setText(news.getTitle());
        holder.category.setText(capitalize(news.getCategory()));
        holder.publishDate.setText(getTimeAgo(news.getPubDate()));
        holder.author.setText(getAuthorFromUrl(news.getLink()));

        Glide.with(holder.itemView.getContext())
                .load(news.getThumbnail())
                .placeholder(R.drawable.news)
                .centerCrop()
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(news);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    // ✅ Method untuk menambah data (lazy loading)
    public void addMoreNews(List<NewsItem> moreNews) {
        int startPosition = newsList.size();
        newsList.addAll(moreNews);
        notifyItemRangeInserted(startPosition, moreNews.size());
    }

    // ✅ Method untuk clear data
    public void clearNews() {
        newsList.clear();
        notifyDataSetChanged();
    }

    public static class SearchNewsViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, category, author, publishDate;

        public SearchNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.news_image);
            title = itemView.findViewById(R.id.news_title);
            category = itemView.findViewById(R.id.news_category);
            author = itemView.findViewById(R.id.news_author);
            publishDate = itemView.findViewById(R.id.news_publish_date);
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
            return "Waktu tidak valid";
        }
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

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
