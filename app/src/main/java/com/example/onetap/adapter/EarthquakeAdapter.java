package com.example.onetap.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.onetap.R;
import com.example.onetap.model.EarthquakeItem;
import java.util.List;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.ViewHolder> {
    private List<EarthquakeItem> list;

    public EarthquakeAdapter(List<EarthquakeItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_earthquake, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EarthquakeItem item = list.get(position);

        // Set data
        holder.wilayah.setText(item.getWilayah());
        holder.tanggal.setText(item.getTanggal() + " • " + item.getJam());
        holder.magnitude.setText(item.getMagnitude());
        holder.magnitudeLevel.setText(getMagnitudeLevel(item.getMagnitude()));
        holder.kedalaman.setText(item.getKedalaman());
        holder.koordinat.setText(item.getLintang() + ", " + item.getBujur());

        // Set potensi dengan styling
        String potensi = item.getPotensi();
        holder.potensi.setText(potensi);
        if (potensi != null && potensi.toLowerCase().contains("tidak")) {
            holder.potensi.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.potensi.setTextColor(Color.parseColor("#F44336"));
        }

        // Set magnitude container background color
        GradientDrawable magnitudeBg = new GradientDrawable();
        magnitudeBg.setShape(GradientDrawable.RECTANGLE);
        magnitudeBg.setCornerRadius(36f); // 12dp in pixels
        magnitudeBg.setColor(getMagnitudeColor(item.getMagnitude()));
        holder.magnitudeContainer.setBackground(magnitudeBg);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String getMagnitudeLevel(String magnitude) {
        try {
            double mag = Double.parseDouble(magnitude);
            if (mag < 3.0) return "Ringan";
            else if (mag < 5.0) return "Sedang";
            else if (mag < 7.0) return "Kuat";
            else return "Sangat Kuat";
        } catch (NumberFormatException e) {
            return "Unknown";
        }
    }

    private int getMagnitudeColor(String magnitude) {
        try {
            double mag = Double.parseDouble(magnitude);
            if (mag < 3.0) return Color.parseColor("#4CAF50"); // Green
            else if (mag < 5.0) return Color.parseColor("#FF9800"); // Orange
            else if (mag < 7.0) return Color.parseColor("#F44336"); // Red
            else return Color.parseColor("#9C27B0"); // Purple
        } catch (NumberFormatException e) {
            return Color.parseColor("#757575"); // Gray
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView wilayah, tanggal, magnitude, magnitudeLevel, kedalaman, koordinat, potensi;
        LinearLayout magnitudeContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wilayah = itemView.findViewById(R.id.text_wilayah);
            tanggal = itemView.findViewById(R.id.text_tanggal);
            magnitude = itemView.findViewById(R.id.text_magnitude);
            magnitudeLevel = itemView.findViewById(R.id.text_magnitude_level);
            kedalaman = itemView.findViewById(R.id.text_kedalaman);
            koordinat = itemView.findViewById(R.id.text_koordinat);
            potensi = itemView.findViewById(R.id.text_potensi);
            magnitudeContainer = itemView.findViewById(R.id.magnitude_container);
        }
    }
}
